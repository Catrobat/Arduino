#include "SPI.h"
#include "PN532_SPI.h"
#include "emulatetag.h"
#include "NdefMessage.h"

/*
   *******************  COMMMANDS *******************
* NFC Emulation:
 Command: "0<WRITEABLE><NFC-UID><OPTIONAL_NDEF_BUFFER>"
 - WRITEABLE hexstring of size 1 ("0" is writeable, else not writeable)
 - <NFC-UID> hexstring of size 6 ( i.e. 123456 )    
 - <OPTIONAL_NDEF_BUFFER>  = <SIZE_NDEF_MESSAGE><NDEF_MESSGAGE>
   - <SIZE_NDEF_MESSAGE> = hexstring of size 4 (i.e. 001c)
   - <NDEF_MESSAGE> ... message in hexstring
 - full command: 00123456001cD101185503646576656C6F7065722E636174726F6261742E6F72672F
   
*/

enum serialCommandPrefix { 
  NFC_EMULATE, VIBRATION_VALUES
};

#define COMMAND_INITALIZATION_FINISHED "INITIALIZATION_FINISHED"
#define COMMAND_NFC_EMULATION_STARTED  "STARTED_NFC_EMULATION"
#define COMMAND_NFC_EMULATION_TIMEDOUT "TIMEDOUT_NFC_EMULATION"
#define COMMAND_NFC_EMULATION_FINISHED "FINISHED_NFC_EMULATION"

// ******************* Settings  *******************

#define STREAM_READ_DELAY 1 // arduino is otherwise faster than the stream
#define NFC_EMULATION_TIMEOUT 2000 
#define SERIAL_BUFFER_SIZE 128

// **************************************************

// TODO: remove this section, this is just for testing
#define TMP_BUFFER_SIZE 500
uint8_t vibrationBuffer[TMP_BUFFER_SIZE];
int vibrationCounter;

// **************************************************

// TODO: we can change this to the ethernet stream when we have ethernet shield
#define STREAM Serial

PN532_SPI pn532spi(SPI, 10);
EmulateTag nfc(pn532spi);

// **************************************************

void setup()
{
  Serial.begin(115200);
  nfc.init();
  Serial.println(COMMAND_INITALIZATION_FINISHED);
}

void loop(){
  if(STREAM.available()){
    
    uint8_t command;
    
    if(readCharConvertToByte(&command)){
      
      switch(command){
      case NFC_EMULATE:
        commandNfcEmulate();
        break;
      case VIBRATION_VALUES:
        commandVibrationValues();
        break;
      default:
        STREAM.print("ERROR: command not understood:");
        STREAM.println(command);
      }
      
      // read remaining bytes i.e. '\n'
      while(STREAM.read() != -1){ 
        delay(STREAM_READ_DELAY);
      }
    }
  }
  // TODO: remove this section, this is just for testing
  vibrationBuffer[vibrationCounter % sizeof(vibrationBuffer)] = vibrationCounter % 2;
  vibrationCounter++;
}

void commandNfcEmulate(){
  STREAM.println(COMMAND_NFC_EMULATION_STARTED);

  uint8_t tagwriteable;
  uint8_t uid[3];

  if(  !readCharConvertToByte(&tagwriteable)
    || !readTwoCharConvertToByte(uid)
    || !readTwoCharConvertToByte(&uid[1])
    || !readTwoCharConvertToByte(&uid[2])
    ){
    STREAM.println("ERROR");
    return;
  }
  
  uint8_t* ndef_file = nfc.getNdefFilePtr();
  uint8_t i = 0;
  for(; i < nfc.getNdefMaxLength(); i++){
      if(!readTwoCharConvertToByte(ndef_file+i))
        break;
  }
  uint16_t ndefSize = (ndef_file[0] << 4) + ndef_file[1];
  if(i == 0 || (ndefSize != i - 2)){
    ndef_file[0] = 0;
    ndef_file[1] = 0;
  }
  

  nfc.setTagWriteable(tagwriteable == 0);
  nfc.setUid(uid);

  if(!nfc.emulate(NFC_EMULATION_TIMEOUT)){
    delay(1000); // delay is mandatory!
    STREAM.println(COMMAND_NFC_EMULATION_TIMEDOUT); 
  } 
  else {
    if(nfc.writeOccured()){
      uint8_t* tag_buf;
      uint16_t length;

      nfc.getContent(&tag_buf, &length);
      NdefMessage msg = NdefMessage(tag_buf, length);
      msg.print();
    }
    delay(1000); // delay is mandatory!
    STREAM.println(COMMAND_NFC_EMULATION_FINISHED);
  }
}

void commandVibrationValues(){
  for(int i=0; i < sizeof(vibrationBuffer); i++){
    if(vibrationBuffer[i] < 0x10){
      Serial.print("0");
    }
    Serial.print(vibrationBuffer[i], HEX);
  }
  Serial.println("VIBRATION_END");
}

// **************************************************

bool readCharConvertToByte(uint8_t *resultingByte){
  int tmp = STREAM.read();
  if(tmp == -1){
    return 0;
  } 
  *resultingByte = hexCharToByte((char)tmp); 
  delay(STREAM_READ_DELAY);
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






