/* 
 * File:   main.cpp
 * Author: b3rnd
 *
 * Created on March 13, 2015, 5:44 PM
 */

#include <Arduino.h>
#include <SPI.h>
#include <Ethernet.h>
#include <PN532_SPI.h>
#include "emulatetag.h"

#include "defines.h"


#ifndef DEBUG_SERIAL
//#define DEBUG_SERIAL /*< macro to switch debug logging on and off */
#define DEBUG_SERIAL_BR 9600
#endif

//---------------------------------------------------------------------
///
/// IP address of the server
IPAddress server_ip_( 192, 168, 8, 16 );

//---------------------------------------------------------------------
///
/// MAC address of the server
uint8_t server_mac_[6];

//---------------------------------------------------------------------
///
/// Instance of the server object
EthernetServer server_( TCP_PORT );

//---------------------------------------------------------------------
///
/// Instance of the client object
EthernetClient client_;

//---------------------------------------------------------------------
///
/// variable to save timeout value of TCP connections.
/// \see checkTcpTimeout
volatile unsigned long tcp_timeout_;

//---------------------------------------------------------------------
///
/// buffer array to quickly save ADC values used to detect vibration
/// \see vibrationCalibration
/// \see measureVibration
volatile uint16_t vibvalues_[BUFFER_SIZE_VIBRATION];

//---------------------------------------------------------------------
///
/// threshold to decide if the vibration sensor detected vibration
/// or not.
/// \see vibrationCalibration
/// \see measureVibration
uint16_t vibration_threshold_;

//---------------------------------------------------------------------
///
/// Instance of the SPI interface to talk to the NFC shield
PN532_SPI nfc_spi_( SPI, PIN_PN532_CS );

//---------------------------------------------------------------------
///
/// Instance of the NFC tag emulator
EmulateTag nfc_( nfc_spi_ );

//---------------------------------------------------------------------
///
/// function to initialize pin modes and default values
void setup();

//---------------------------------------------------------------------
///
/// main state logic. this function will start immediately after
/// setup() and will be executed forever
/// \see setup()
void loop();

//---------------------------------------------------------------------
///
/// Both shields, the Ethernet shield and the NFC shield are connected
/// over SPI. Only one can be active at a time. This function is used
/// to switch from one slave to the other.
/// \see PIN_ETHERNET_CS
/// \see PIN_PN532_CS
/// \param target_pin either PIN_ETHERNET_CS or PIN_PN532_CS
///        the selected pin number will be the new active SPI slave
void changeSPI( uint8_t target_pin );

//---------------------------------------------------------------------
///
/// Block execution until a client connects to the server. If a client
/// connects, switch on the status led and reset the TCP timeout
/// counter.
/// \see setTcpTimeout
/// \see client_
void waitForIncomingConnection();

//---------------------------------------------------------------------
///
/// This function saves the sum of the current millisecond-counter
/// and the \see TCP_CONNECTION_TIMEOUT_MS value.
/// \see tcp_timeout_
void setTcpTimeout();

//---------------------------------------------------------------------
///
/// Decide if the current TCP connection should be killed
/// \see setTcpTimeout
/// \return true, if a TCP connection timed out
bool checkTcpTimeout();

//---------------------------------------------------------------------
///
/// Get the first byte that has been sent by the client. The command
/// for the server is encoded in this first byte as a character.
/// \return the command identifier
uint8_t getCommand();

//---------------------------------------------------------------------
///
/// Decode the command identifier and call the corresponding function
/// if the command code was valid
/// \param command Number to identifying the command
void processCommand( uint8_t command );

//---------------------------------------------------------------------
///
/// Function to detect a light source. Used to measure if the cameras
/// flash light of the test device is switched on, or not.
/// \return true, if the light is on, false otherwise
bool measureLight();

//---------------------------------------------------------------------
///
/// Function to detect if the test device vibrator is active.
/// \return true, if the sensor detected vibration, false otherwise
bool measureVibration();

//---------------------------------------------------------------------
///
/// Function to emulate a NFC tag using the NFC shield. This function
/// switches between the Ethernet connection and the NFC shield to
/// read additional data, needed for tag emulation.
/// \see changeSPI()
/// \see nfc_
void emulateNfcTag();

//---------------------------------------------------------------------
///
/// Measure the current level of noise on the vibration pin. This
/// value can be used to set a threshold to distinguish between noise
/// level and signals produces by vibration.
/// \see vibration_threshold_
void vibrationCalibration();

//---------------------------------------------------------------------
///
/// Control how the TCP connection should be terminated - switch off
/// the status led and flush the input buffer.
void terminateConnection();

//---------------------------------------------------------------------
///
/// Convert a given hex string to actual values.
/// str = "fe" will be converted to bytes = { 0xfe }
/// the string representation can contain capital letters and non
/// capital letters.
/// \param str The input string that should be converted
/// \param strlen number of characters that should be converted
/// \param bytes Resulting byte array of length strlen/2
/// \return true, if any error happened, false otherwise
bool hexStringToByteArray( char *str, 
                           uint32_t strlen,
                           uint8_t bytes[]);

//---------------------------------------------------------------------
//---------------------------------------------------------------------
//---------------------------------------------------------------------
///
/// program entry point
int main()
{
    init();
    setup();
    for(;;) 
        loop();
    return 0;
}

//---------------------------------------------------------------------
void setup()
{
    // initialize output pins 
    pinMode( PIN_STATUS_LED, OUTPUT );
    pinMode( PIN_PN532_CS, OUTPUT );
    pinMode( PIN_ETHERNET_CS, OUTPUT );
    
    // initialize input pins
    pinMode( PIN_LIGHT_SENSOR, INPUT );
    
    // initialize output pin values
    digitalWrite( PIN_PN532_CS, HIGH );
    digitalWrite( PIN_ETHERNET_CS, HIGH );
    digitalWrite( PIN_STATUS_LED, LOW );
    
    // nfc shield interface
    changeSPI( PIN_PN532_CS );
    nfc_.init();
    
    // server mac address
    server_mac_[0] = 0xde;
    server_mac_[1] = 0xad;
    server_mac_[2] = 0xbe;
    server_mac_[3] = 0xef;
    server_mac_[4] = 0xfe;
    server_mac_[5] = 0xed;
    
    // ethernet interface
    changeSPI( PIN_ETHERNET_CS );
    Ethernet.begin( server_mac_, server_ip_ );
    server_.begin();
    
    vibrationCalibration();
    
#ifdef DEBUG_SERIAL
    Serial.begin( DEBUG_SERIAL_BR );
    Serial.print( "INFO: server started. IP = ");
    Serial.println( Ethernet.localIP() );
    Serial.print( "INFO: connection will time out after " );
    Serial.print( TCP_CONNECTION_TIMEOUT_MS / 1000 );
    Serial.println( " seconds!" );
#endif
}

//---------------------------------------------------------------------
void loop()
{
    waitForIncomingConnection();
    
    while ( client_.connected() )
    {
        uint8_t cmd = 255;
        
        if ( checkTcpTimeout() )
        {
#ifdef DEBUG_SERIAL
            Serial.println( "ERROR: connection timeout!" );
#endif
            break;
        }
        
        cmd = getCommand();
        if ( cmd == COMMAND_INVALID_COMMAND )
        {
#ifdef DEBUG_SERIAL
            Serial.print( "ERROR: invalid command received: ");
            Serial.println( cmd );
#endif
            break;
        }
        
        processCommand( cmd );
    }
    terminateConnection();
}

//---------------------------------------------------------------------
void changeSPI( uint8_t target_pin )
{
    if ( target_pin == PIN_PN532_CS )
    {
        digitalWrite( PIN_ETHERNET_CS, HIGH );
        SPI.setBitOrder( LSBFIRST );
    }
    else if ( target_pin == PIN_ETHERNET_CS )
    {
        digitalWrite( PIN_PN532_CS, HIGH );
        SPI.setBitOrder( MSBFIRST );
    }
}

//---------------------------------------------------------------------
void waitForIncomingConnection()
{
    do
    {
        client_ = server_.available();
        delay( STREAM_DELAY_MS );
    } while( !client_ );
    
    // now a client is connected
#ifdef DEBUG_SERIAL
    Serial.println("DEBUG: client connected!");
#endif
    digitalWrite( PIN_STATUS_LED, HIGH );
    setTcpTimeout();
}

//---------------------------------------------------------------------
void setTcpTimeout()
{
    tcp_timeout_ = millis() + TCP_CONNECTION_TIMEOUT_MS;
}

//---------------------------------------------------------------------
bool checkTcpTimeout()
{
    return ( tcp_timeout_ < millis() );
}

//---------------------------------------------------------------------
uint8_t getCommand()
{
    if ( !client_.available() )
        return (uint8_t)(-1);
    
    uint8_t in_buf = '\0';
    
    size_t bytes_read = client_.readBytes( (char*)&in_buf, 1 );
    if ( bytes_read < 1 )
        return (uint8_t)(-1);
    
    return in_buf;
}

//---------------------------------------------------------------------
void processCommand( uint8_t command )
{
    switch ( command )
    {
        //-------------------------------------------------------------
        case COMMAND_TAG_EMULATE:
        {
#ifdef DEBUG_SERIAL
            Serial.println( "DEBUG: nfc tag emulate" );
#endif
            emulateNfcTag();
            break;
        }
        //-------------------------------------------------------------
        case COMMAND_VIB_MEASUREMENT:
        {
#ifdef DEBUG_SERIAL
            Serial.println( "DEBUG: vibration measurement" );
#endif
            if ( measureVibration() )
                client_.println( VIB_MEASUREMENT_RESP_ON );
            else
                client_.println( VIB_MEASUREMENT_RESP_OFF );
            
            break;
        }
        //-------------------------------------------------------------
        case COMMAND_LIGHT_MEASUREMENT:
        {
#ifdef DEBUG_SERIAL
            Serial.println( "DEBUG: light measurement" );
#endif
            if ( measureLight() )
                client_.println( LIGHT_MEASUREMENT_RESP_ON );
            else
                client_.println( LIGHT_MEASUREMENT_RESP_OFF );
            
            break;
        }
        //-------------------------------------------------------------
        case COMMAND_VIB_CALIBRATE:
        {
#ifdef DEBUG_SERIAL
            Serial.println( "DEBUG: calibrate vibration sensor" );
#endif
            vibrationCalibration();
            break;
        }
        default:
            //handle error
            ;
#ifdef DEBUG_SERIAL
            Serial.println( "WARNING: unknown command" );
#endif
    }
}

//---------------------------------------------------------------------
bool measureLight()
{
    int light_value = analogRead( PIN_LIGHT_SENSOR );
#ifdef DEBUG_SERIAL
    Serial.print( "DEBUG: light sensor = ");
    Serial.println( light_value );
#endif
    return ( light_value < LIGHT_SENSOR_THRESHOLD );
}

//---------------------------------------------------------------------
bool measureVibration()
{
#ifdef DEBUG_SERIAL
    Serial.print("DEBUG: start vib measurement at ");
    uint32_t starttime = millis();
    Serial.println(starttime);
#endif
    for ( uint16_t i = 0; i < BUFFER_SIZE_VIBRATION; i++ )
        vibvalues_[i] = analogRead(PIN_VIBRATION_SENSOR);
    
    uint16_t max_value = 0;
    uint16_t min_value = 1023;
    for ( uint16_t i = 0; i < BUFFER_SIZE_VIBRATION; i++ )
    {
        if ( max_value < vibvalues_[i] )
            max_value = vibvalues_[i];
        
        if ( min_value > vibvalues_[i] )
            min_value = vibvalues_[i];
    }
#ifdef DEBUG_SERIAL
    Serial.print("DEBUG: end vib measurement after ");
    Serial.println(millis() - starttime);
    
//    for ( uint16_t i = 0; i < BUFFER_SIZE_VIBRATION; i++ )
//        client_.println(vibvalues_[i]);
    
    Serial.print("INFO: min = "); Serial.println(min_value);
    Serial.print("INFO: max = "); Serial.println(max_value);
    Serial.print("INFO: max - min = ");
    Serial.println(max_value - min_value);
    Serial.println("DEBUG: done sending");
#endif
    
    return (( max_value - min_value ) > vibration_threshold_ );
}

//---------------------------------------------------------------------
void vibrationCalibration()
{
    for ( uint16_t i = 0; i < BUFFER_SIZE_VIBRATION; i++ )
        vibvalues_[i] = analogRead(PIN_VIBRATION_SENSOR);
    
    uint16_t max_value = 0;
    uint16_t min_value = 1023;
    for ( uint16_t i = 0; i < BUFFER_SIZE_VIBRATION; i++ )
    {
        if ( max_value < vibvalues_[i] )
            max_value = vibvalues_[i];
        
        if ( min_value > vibvalues_[i] )
            min_value = vibvalues_[i];
    }
    vibration_threshold_ = ( max_value - min_value ) * 3;
#ifdef DEBUG_SERIAL
    Serial.print("DEBUG: max_value = ");
    Serial.println(max_value);
    Serial.print("DEBUG: min_value = ");
    Serial.println(min_value);
    Serial.print("DEBUG: new threshold = ");
    Serial.println(vibration_threshold_);
#endif
}

//---------------------------------------------------------------------
void terminateConnection()
{
    // flush input buffer
    while ( client_.read() != (-1) )
        delay( STREAM_DELAY_MS );
    
    client_.stop();
#ifdef DEBUG_SERIAL
    Serial.println( "DEBUG: client disconnected" );
#endif
    digitalWrite( PIN_STATUS_LED, LOW );
}

//---------------------------------------------------------------------
void emulateNfcTag()
{
    client_.println(CMD_NFC_EMULATION_STARTED);
    uint8_t tag_writeable = 0;
    char uid_str[6];
    uint8_t uid[] = {0, 0, 0};
    int16_t bytes_read = 0;
    bool err = false;
    
    bytes_read = client_.read( &tag_writeable, 1 );
    if ( bytes_read != 1 )
    {
#ifdef DEBUG_SERIAL
        Serial.print( "ERROR: emulateNfcTag: received " );
        Serial.print( bytes_read );
        Serial.println( " bytes");
#endif
        return;
    }
    
    bytes_read = client_.readBytes( uid_str, 6 );
    err = hexStringToByteArray( uid_str, 6, uid );
    if ( bytes_read != 6 )
    {
#ifdef DEBUG_SERIAL
        Serial.print( "ERROR: emulateNfcTag: received " );
        Serial.print( bytes_read );
        Serial.println( " bytes");
#endif
        return;
    }
    if ( err )
        return;
    
#ifdef DEBUG_SERIAL
    Serial.print( "DEBUG: tag writeable = ");
    Serial.println( tag_writeable - '0' );
    
    Serial.print( "DEBUG: uid = " );
    Serial.print( uid[0], HEX );
    Serial.print( " " );
    Serial.print( uid[1], HEX );
    Serial.print( " " );
    Serial.println( uid[2], HEX ); 
#endif
    
    // read hexstring from input and set it as NDEF message
    uint8_t *ndef_fd = nfc_.getNdefFilePtr();
    uint8_t max_ndef_len = nfc_.getNdefMaxLength();
    
#ifdef DEBUG_SERIAL
    Serial.print( "DEBUG: max ndef len = " );
    Serial.println( max_ndef_len );
#endif
    
    char ndef_msg_str[max_ndef_len];
    bytes_read = client_.readBytes( ndef_msg_str, max_ndef_len );
    
    bytes_read = ( bytes_read > max_ndef_len ) ? max_ndef_len : bytes_read;
    err = hexStringToByteArray( ndef_msg_str, bytes_read - 1, ndef_fd );
    if ( err )
        return;
    
    nfc_.setTagWriteable( tag_writeable == CMD_NFC_TAG_WRITEABLE );
    nfc_.setUid( uid );
    
    changeSPI( PIN_PN532_CS );
    
    if ( !nfc_.emulate(NFC_EMULATION_TIMEOUT_MS) )
    {
        delay(1000);
        changeSPI( PIN_ETHERNET_CS );
        client_.println( CMD_NFC_EMULATION_TIMEOUT );
#ifdef DEBUG_SERIAL
        Serial.print( "ERROR: ");
        Serial.println( CMD_NFC_EMULATION_TIMEOUT );
#endif
    }
    else
    {
        delay(1000);
        changeSPI( PIN_ETHERNET_CS );
        
        if ( nfc_.writeOccured() )
        {
            uint8_t *tagbuf;
            uint16_t length;
            nfc_.getContent( &tagbuf, &length );
            client_.println( NDEF_MODIFIED );
#ifdef DEBUG_SERIAL
            Serial.print( "ERROR: ");
            Serial.println( NDEF_MODIFIED );
#endif
            for ( uint8_t i = 0; i < length; i++ )
            {
                if ( tagbuf[i] < 0x10 )
                    client_.print( "0" );
                client_.print( tagbuf[i], HEX );
            }
            client_.print( "\n" );
        }
        client_.println( CMD_NFC_EMULATION_FINISHED );
    }
}

//---------------------------------------------------------------------
bool hexStringToByteArray( char *str, 
                           uint32_t strlen,
                           uint8_t bytes[])
{
    if ( strlen % 2 )
    {
#ifdef DEBUG_SERIAL
        Serial.print("ERROR: invalid string length to convert: ");
        Serial.println(strlen);
#endif
        return true;
    }
    
    for ( uint32_t c = 0; c < (strlen / 2); c++ )
    {
        if ( str[2 * c] >= '0' && str[2 * c] <= '9' )
            bytes[c] += (str[2 * c] - '0') << 4;
        else if ( str[2 * c] >= 'a' && str[2 * c] <= 'f' )
            bytes[c] += (str[2 * c] - 'a' + 10) << 4;
        else if ( str[2 * c] >= 'A' && str[2 * c] <= 'F' )
            bytes[c] += (str[2 * c] - 'A' + 10) << 4;
        else
        {
#ifdef DEBUG_SERIAL
            Serial.print("ERROR: invalid hex string");
#endif
            return true;
        }
        
        if ( str[2 * c + 1] >= '0' && str[2 * c + 1] <= '9' )
            bytes[c] += str[2 * c + 1] - '0';
        else if ( str[2 * c + 1] >= 'a' && str[2 * c + 1] <= 'f' )
            bytes[c] += str[2 * c + 1] - 'a' + 10;
        else if ( str[2 * c + 1] >= 'A' && str[2 * c + 1] <= 'F' )
            bytes[c] += str[2 * c + 1] - 'A' + 10;
        else
        {
#ifdef DEBUG_SERIAL
            Serial.println("ERROR: invalid hex string");
#endif
            return true;
        }
    }
    return false;
}
