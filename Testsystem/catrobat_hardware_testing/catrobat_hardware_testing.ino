#include "SPI.h"
#include "PN532_SPI.h"
#include "emulatetag.h"
#include "NdefMessage.h"

/*
   *******************  COMMMANDS *******************
  - NFC Emulation:
    Command: "0<WRITEABLE><NFC-UID>"
      - WRITEABLE hexstring of size 1 ("0" is writeable, else not writeable)
      - <NFC-UID> hexstring of size 6 ( i.e. 123456 )    
*/

enum serialCommandPrefix { NFC_EMULATE, VIBRATION_VALUES };

#define COMMAND_INITALIZATION_FINISHED "INITIALIZATION_FINISHED"
#define COMMAND_NFC_EMULATION_STARTED  "STARTED_NFC_EMULATION"
#define COMMAND_NFC_EMULATION_TIMEDOUT "TIMEDOUT_NFC_EMULATION"
#define COMMAND_NFC_EMULATION_FINISHED "FINISHED_NFC_EMULATION"

// ******************* Settings  *******************

#define NFC_EMULATION_TIMEOUT 2000 
#define SERIAL_BUFFER_SIZE 128

// **************************************************

// TODO: remove this section, this is just for testing
#define TMP_BUFFER_SIZE 10
uint8_t vibrationBuffer[TMP_BUFFER_SIZE];
int vibrationCounter;

// **************************************************

char serialBuffer[SERIAL_BUFFER_SIZE];

PN532_SPI pn532spi(SPI, 10);
EmulateTag nfc(pn532spi);
  
void setup()
{
  Serial.begin(115200);
  nfc.init();
  Serial.println(COMMAND_INITALIZATION_FINISHED);
}

void loop(){
    // commands from 
    if(Serial.available() ) { 
      // reading one line from Serial
     
      byte readLength = Serial.readBytesUntil('\n', serialBuffer, sizeof(serialBuffer));
      
      if(readLength == 0){
          // TODO: better error handling
          Serial.println("ERROR: serial read too short");
      } else {
        uint8_t command = hexCharToByte(serialBuffer[0]);
        bool tagWriteable;
        
        switch(command){
          case NFC_EMULATE:
          //  emulate(serialbuffer+1, readLength);
          
          
            tagWriteable = hexCharToByte(serialBuffer[1]) == 0;
            uint8_t nfcUid[3];
            if(readLength >= 8){
              nfcUid[0] = (hexCharToByte(serialBuffer[2]) << 4) + hexCharToByte(serialBuffer[3]);
              nfcUid[1] = (hexCharToByte(serialBuffer[5]) << 4) + hexCharToByte(serialBuffer[5]);
              nfcUid[2] = (hexCharToByte(serialBuffer[6]) << 4) + hexCharToByte(serialBuffer[7]);
              emulate(nfcUid,tagWriteable);
            } else {
              Serial.println("ERROR: nfc_emulate command too short!");
            }
            break;
           case VIBRATION_VALUES:
             for(int i=0; i < sizeof(vibrationBuffer); i++){
               if(vibrationBuffer[i] < 0x10){
                 Serial.print("0");
               }
               Serial.print(vibrationBuffer[i], HEX);
             }
             Serial.println("VIBRATION_END");
             break;
           default: 
              Serial.print("ERROR: command not understood:");
              Serial.println(serialBuffer[0]);
          }
      }
    }
    
    // TODO: remove this section, this is just for testing
    vibrationBuffer[vibrationCounter % sizeof(vibrationBuffer)] = vibrationCounter % 2;
    vibrationCounter++;
}


void emulate(uint8_t* uid, bool tagWriteable){
    Serial.println(COMMAND_NFC_EMULATION_STARTED);
    nfc.setUid(uid);
    nfc.setTagWriteable(tagWriteable);
    if(!nfc.emulate(NFC_EMULATION_TIMEOUT)){
        delay(1000); // delay is mandatory!
      Serial.println(COMMAND_NFC_EMULATION_TIMEDOUT);
    } else{  
      if(nfc.writeOccured()){
         uint8_t* tag_buf;
         uint16_t length;
       
         nfc.getContent(&tag_buf, &length);
         NdefMessage msg = NdefMessage(tag_buf, length);
         msg.print();
      }
      delay(1000); // delay is mandatory!
      Serial.println(COMMAND_NFC_EMULATION_FINISHED);
    } 
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
