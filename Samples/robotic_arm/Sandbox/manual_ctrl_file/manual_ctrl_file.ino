//includes
#include <SoftwareSerial.h>
//variables
int LED = 13;
char inputData = 'I';


//===========================

int ctrlPin1 = 9;      //Control Pin 1 Reset 
int ctrlPin2 = 8;      //Control Pin 2 Set

int addrChangePin = 10;
int addrPin1 = 13;      //LSB
int addrPin2 = 12;
int addrPin3 = 11;     //MSB

int cmdRight = 2;
int cmdLeft = 1;
int cmdStop = 3;

int initFlag = 1;

int NUMBEROFMOTORS = 5;
int motorAdresses[] = {5,6,7,4,3};

//===========================

void setup() {
  //define the output pin on the Arduino Board
  pinMode(LED, OUTPUT);
  //Setup usb serial connection to computer
  Serial.begin(115200);
  //Setup Arduino Board (Arduino homepage)
  Serial.println("SET BT PAGEMODE 3 2000 1");
  Serial.println("SET BT NAME ARDUINOBT");
  Serial.println("SET BT ROLE 0 f 7d00");
  Serial.println("SET CONTROL ECHO 0");
  Serial.println("SET BT AUTH * 12345");
  Serial.println("SET CONTROL ESCAPE - 00 1");
  Serial.println("SET CONTROL BAUD 115200,8n1");  //works with 115200 baud      


//ROBO-ARM
  //-------------------------------------------------------------------------------------------------
  pinMode(ctrlPin1, OUTPUT);
  pinMode(ctrlPin2, OUTPUT);

  pinMode(addrChangePin, OUTPUT);
  pinMode(addrPin1, OUTPUT);
  pinMode(addrPin2, OUTPUT);
  pinMode(addrPin3, OUTPUT);  
  
  digitalWrite(addrChangePin, 0);
  //-------------------------------------------------------------------------------------------------

}

void loop() {
    //wait for incoming data
    if (Serial.available() > 0) {
    // read the oldest byte in the serial buffer:
    inputData = Serial.read();
    //define commands
    checkInput(inputData);
  }
}
//functions
void checkInput(char inputData) {
    switch(inputData)
    {
      case 'A':  //activate
        //digitalWrite(LED, HIGH);
        Serial.write('L');
        execCmd(1, cmdLeft);//cmdRight cmdLeft
        break; 
      case 'D':  //deactivate
        //digitalWrite(LED, LOW);
        execCmd(6, 2);
        Serial.write('H');
        break; 
        
 //============================================
 
      case 'q':  //activate
        Serial.write('L');
        execCmd(1, cmdLeft);//cmdRight cmdLeft
        break; 
      case 'w':  //deactivate
        execCmd(1, cmdRight);
        Serial.write('H');
        break; 
        
 //============================================
 
      case 'e':  //activate
        Serial.write('L');
        execCmd(2, cmdLeft);//cmdRight cmdLeft
        break; 
      case 'r':  //activate
        Serial.write('L');
        execCmd(2, cmdRight);//cmdRight cmdLeft
        break; 
        
 //============================================      
 
 //DONT USE THIS MOTOR ==== DANGER
      case 'f':  //activate
        Serial.write('L');
        execCmd(3, cmdLeft);//cmdRight cmdLeft
        break; 
      case 'g':  //deactivate
        execCmd(3, cmdRight);
        Serial.write('H');
        break; 
        
 //============================================
 
      case 'y':  //activate
        Serial.write('L');
        execCmd(4, cmdLeft);//cmdRight cmdLeft
        break; 
      case 'x':  //activate
        Serial.write('L');
        execCmd(4, cmdRight);//cmdRight cmdLeft
        break; 
 
  //============================================
 
      case 'c':  //activate
        Serial.write('L');
        execCmd(5, cmdLeft);//cmdRight cmdLeft
        break; 
      case 'v':  //activate
        Serial.write('L');
        execCmd(5, cmdRight);//cmdRight cmdLeft
        break; 
  //============================================
 
      case 'b':  //activate
        Serial.write('L');
        execCmd(0, cmdLeft);//cmdRight cmdLeft
        break; 
      case 'n':  //activate
        Serial.write('L');
        execCmd(0, cmdRight);//cmdRight cmdLeft
        break; 
               
        
 //============================================
      case 's':  //stop all
        execCmd(6, 2);
        Serial.write('H');
        break; 
    }
}

void execCmd(int motorNum, int command)
{
  //write Command
  digitalWrite(ctrlPin1, (1 & command));
  digitalWrite(ctrlPin2, (2 & command));
  
  //Select engine
  digitalWrite(addrPin1, !(1 & motorNum));
  digitalWrite(addrPin2, !(2 & motorNum));
  digitalWrite(addrPin3, !(4 & motorNum));
  
  //activate Multiplexer
  digitalWrite(addrChangePin, 1);
  delay(300);
  digitalWrite(addrChangePin, 0);
}

void stopMotor()
{
  
}
