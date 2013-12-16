/*

 This sketch provides an TCP interface for testing hardware (NFC,...)
 of i.e. Android devices.
 
 *******************  COMMMANDS *******************
 * NFC Emulation:
 Command: "0<WRITEABLE><NFC-UID><OPTIONAL_NDEF_BUFFER>"
 - WRITEABLE hexstring of size 1 ("0" is writeable, else not writeable)
 - <NFC-UID> hexstring of size 6 ( i.e. 123456 )    
 - <OPTIONAL_NDEF_BUFFER>  = <SIZE_NDEF_MESSAGE><NDEF_MESSGAGE>
 - <SIZE_NDEF_MESSAGE> = hexstring of size 4 (i.e. 001c)
 - <NDEF_MESSAGE> ... message in hexstring
 - full command example: 00123456001cD101185503646576656C6F7065722E636174726F6261742E6F72672F
 
 Board: Arduino Mega 2560
 Shields: NFC Shield V2.0, Ethernet Shield
 
 Attention both shields use the sampe SPI CS Pin (10). Therefore hardware modifications or "jumper cables" are necessary.
 
 Setup (see setup.jpg):
 Top:    Ethernet Shield
 Middle: NFC Shield V2.0
 Bottom: Arduino Mega 2560
 
 Pin 10 of of both Shields are bend out (not connected to base board). 
 Connect following pins with jumper wires: 
 - Arduino   Pin 10 <--> EthernetShield Pin10
 - NFCShield Pin  9 <--> EthernetShield Pin 9
 
 Now we can use pin 10 for Ehternet Shield and pin 9 for NFCShield.
 */

#include "SPI.h"
#include "PN532_SPI.h"
#include "emulatetag.h"
#include <Ethernet.h>

#define NFC_SHIELD_AVAILABLE

enum serialCommandPrefix { 
  NFC_EMULATE, VIBRATION_VALUES, LIGHT_VALUES
};

#define COMMAND_NFC_EMULATION_STARTED  "STARTED_NFC_EMULATION"
#define COMMAND_NFC_EMULATION_TIMEDOUT "TIMEDOUT_NFC_EMULATION"
#define COMMAND_NFC_EMULATION_FINISHED "FINISHED_NFC_EMULATION"
#define NDEF_MODIFIED                  "NDEF_MODIFIED:"

// ******************* Settings  *******************

#define STREAM_DELAY 2 // arduino is otherwise faster than the stream
#define NFC_EMULATION_TIMEOUT 5000 
#define SERIAL_BUFFER_SIZE 128

byte mac[] = { 
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
//IPAddress ip(192,168,8,16);
IPAddress ip(10,0,0,111);

#define TCP_PORT 6789

// **************************************************

const int lightSensorPin       = 2; // the ligth sensor sets a digital signal on pin 2
const int measurementStatusPin = 7; // indicates the status of the hardware; is on while client is connected 

// TODO: maybe remove this section, this is just for testing
#define TMP_BUFFER_SIZE 100
uint8_t vibrationBuffer[TMP_BUFFER_SIZE];
int vibrationCounter;

// **************************************************

#define PN532_CS 9
#define ETHNET_CS 10

PN532_SPI pn532spi(SPI, PN532_CS);
EmulateTag nfc(pn532spi);

EthernetServer server(TCP_PORT);
EthernetClient client;

// **************************************************

void setup()
{
  // disable all SPI
  pinMode(PN532_CS,OUTPUT);
  pinMode(ETHNET_CS,OUTPUT);
  digitalWrite(PN532_CS,HIGH);
  digitalWrite(ETHNET_CS,HIGH);

  pinMode(lightSensorPin, INPUT); // declare the lightSensor pin as a INPUT
  pinMode(measurementStatusPin, OUTPUT); // declare the status pin as a OUTPUT
  digitalWrite(measurementStatusPin,LOW);
  
  Serial.begin(115200);

  #ifdef NFC_SHIELD_AVAILABLE
  spiSelect(PN532_CS);
  nfc.init();
  #endif 

  spiSelect(ETHNET_CS);
  Ethernet.begin(mac, ip);
  server.begin();

  Serial.print("server is at ");
  Serial.println(Ethernet.localIP());

  Serial.println(F("INITIALIZATION_FINISHED"));
}

void loop(){
  client = server.available();

  if(client){
    Serial.println("client connected");
    
    if(client.connected())
      digitalWrite(measurementStatusPin,HIGH);
    
    while(client.connected()){
      uint8_t command;
      
      if(readCharConvertToByte(&command)){

        switch(command){
        case NFC_EMULATE:
          #ifdef NFC_SHIELD_AVAILABLE
          commandNfcEmulate();
          #endif 
          break;
        case VIBRATION_VALUES:
          commandVibrationValues();
          break;
        case LIGHT_VALUES:
          commandLightValues();
          break;
        default:
          client.print("ERROR: command not understood:");
          client.println(command);
        }

        // read remaining bytes i.e. '\n'
        while(client.read() != -1){ 
          delay(STREAM_DELAY);
        }
      }

      // TODO: maybe also read out sensor values here
    }
    delay(STREAM_DELAY);
    client.stop();
    Serial.println("client disconnected");
    
    digitalWrite(measurementStatusPin,LOW);
  }

  // TODO: remove this section, this is just for testing
  vibrationBuffer[vibrationCounter % sizeof(vibrationBuffer)] = vibrationCounter % 2;
  vibrationCounter++;
}

void commandNfcEmulate(){
  client.println(COMMAND_NFC_EMULATION_STARTED);

  uint8_t tagwriteable;
  uint8_t uid[3];

  if(  !readCharConvertToByte(&tagwriteable)
    || !readTwoCharConvertToByte(uid)
    || !readTwoCharConvertToByte(&uid[1])
    || !readTwoCharConvertToByte(&uid[2])
    ){
    client.println("ERROR");
    delay(STREAM_DELAY);
    return;
  }

  uint8_t* ndef_file = nfc.getNdefFilePtr();
  uint8_t i = 0;
  for(; i < nfc.getNdefMaxLength(); i++){
    if(!readTwoCharConvertToByte(ndef_file+i))
      break;
  }
  uint16_t ndefSize = (ndef_file[0] << 4) + ndef_file[1];
  if(i == 0 || (i < ndefSize)){
    ndef_file[0] = 0;
    ndef_file[1] = 0;
  }

  nfc.setTagWriteable(tagwriteable == 0);
  nfc.setUid(uid);

  spiSelect(PN532_CS);

//  nfc.init();

  if(!nfc.emulate(NFC_EMULATION_TIMEOUT)){
    delay(1000); // delay is mandatory!
    spiSelect(ETHNET_CS);
    client.println(COMMAND_NFC_EMULATION_TIMEDOUT); 
  } 
  else {
    delay(1000); // delay is mandatory!
    spiSelect(ETHNET_CS);
    if(nfc.writeOccured()){
      uint8_t* tag_buf;
      uint16_t length;

      nfc.getContent(&tag_buf, &length);

      client.print(NDEF_MODIFIED);
      uint16_t i;
      for(i = 0; i < length; i++){
        if(tag_buf[i] < 0x10){
          client.print("0"); 
        }
        client.print(tag_buf[i], HEX);         
      }
      client.print("\n");      
    }

    client.println(COMMAND_NFC_EMULATION_FINISHED);
  }
  delay(STREAM_DELAY);
}

// **************************************************

void commandLightValues(){

  client.print(digitalRead(lightSensorPin), HEX);
  client.println("LIGHT_END");
  delay(STREAM_DELAY);
}

// **************************************************

void commandVibrationValues(){
  for(int i=0; i < sizeof(vibrationBuffer); i++){
    if(vibrationBuffer[i] < 0x10){
      client.print("0");
    }
    client.print(vibrationBuffer[i], HEX);
  }
  client.println("VIBRATION_END");
  delay(STREAM_DELAY);
}


// **************************************************

void spiSelect(int CS) { 

  if(CS == ETHNET_CS){
    digitalWrite(PN532_CS,HIGH);
    SPI.setBitOrder(MSBFIRST);
  }
  else if(CS == PN532_CS){
    digitalWrite(ETHNET_CS,HIGH);
    SPI.setBitOrder(LSBFIRST);
  }

  // enable the chip we want
  digitalWrite(CS,LOW); 
}

bool readCharConvertToByte(uint8_t *resultingByte){
  if(!client.available())
    return 0;

  int tmp = client.read();
  if(tmp == -1){
    return 0;
  } 
  
  *resultingByte = hexCharToByte((char)tmp);  
  delay(STREAM_DELAY);
  
  return 1;  
}

bool readTwoCharConvertToByte(uint8_t *resultingByte){
  uint8_t msb;
  uint8_t lsb;

  if(readCharConvertToByte(&msb) && readCharConvertToByte(&lsb)){
    *resultingByte = (msb << 4) + lsb;
    return 1;
  }
  return 0;
}

uint8_t hexCharToByte(char c){
  if(c >= '0' && c <= '9')
    return c - '0';
  if(c >= 'a' && c <= 'f')
    return c - 'a' + 10;
  if(c >= 'A' && c <= 'F')
    return c - 'A' + 10;
  return 0;
}


