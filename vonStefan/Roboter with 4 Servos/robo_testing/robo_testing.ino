#include <EEPROM.h>
#include <Servo.h>
#include "IO_pins.h"


// define global variables

byte mode=0;                                                    // 0 = stop/kick/look   1 = walk  
byte cmode=0;                                                   // 0 = normal mode      1 = calibration mode

int Lkc;                                                        // Left  knee center position                                    
int Rkc;                                                        // Right knee center position
int Lhc;                                                        // Left  hip  center position
int Rhc;                                                        // Right hip  center position

int Lkp;                                                        // Left  knee position
int Rkp;                                                        // Right knee position
int Lhp;                                                        // Left  hip  position
int Rhp;                                                        // Right hip  position

int stride=0;
int flift=150; //150                                                  // how much to tilt foot so toe grips or slides
int time=2600;                                                  // Time must be greater than 2400 to recognise IRC start bit
int steer=0;                                                    // can be used to straighten robot walk
int led=1;

int IRC;                                                        // Infra Red Command from remote control
int pulse;                                                      // used to measure the pulse width of IR signals
int i=0;

int leftIRvalue;                                                // Eye left  sensors
int rightIRvalue;                                               // Eye right sensors
int upIRvalue;                                                  // Eye up    sensors
int downIRvalue;                                                // Eye down  sensors
int distance;                                                   // average reading of all Eye sensors
int lookUD;                                                     // Up - Down    evaluation
int lookLR;                                                     // Left - Right evaluation


// define servos
Servo Lknee;
Servo Rknee;
Servo Lhip;
Servo Rhip;

void setup()
{
//  Lkc=EEPROM.read(0)*256;                                       // high byte - initial value 1550
//  Lkc+=EEPROM.read(1);                                          // low byte
//
//  Rkc=EEPROM.read(2)*256;                                       // high byte - initial value 1550
//  Rkc+=EEPROM.read(3);                                          // low byte
//
//  Lhc=EEPROM.read(4)*256;                                       // high byte - initial value 1500
//  Lhc+=EEPROM.read(5);                                          // low byte
//
//  Rhc=EEPROM.read(6)*256;                                       // high byte - initial value 1500
//  Rhc+=EEPROM.read(7);                                          // low byte
//  
//  if (Lkc<1000 || Lkc>2000 || Lhc<1000 || Lhc>2000 || Rkc<1000 || Rkc>2000 || Rhc<1000 || Rhc>2000)
//  {                                                             // loads defaults if any value invalid
    Lkc=1650;  //1450 do be bend?
    Rkc=1500;  //1550 do be bend?
    Lhc=1500;
    Rhc=1500;
//    Store();                                                    // save defaults to EEPROM
//  }
  
  Lkp=Lkc;                                                      // set servos to center positions (legs straight)
  Rkp=Rkc;
  Lhp=Lhc;
  Rhp=Rhc;

  Lknee.attach(Lkneepin);
  Rknee.attach(Rkneepin);
  Lhip.attach(Lhippin);
  Rhip.attach(Rhippin);

  Servopos();
  
  pinMode(IRpin,OUTPUT);                                        // IR and Arduino LEDs
  pinMode(LEDpin,OUTPUT);                                       // Back pack LED
  pinMode(Speakerpin,OUTPUT);                                   // Speaker
  digitalWrite(3,1);                                            // turn on D3 pullup resistor

  //----------------------------- play tune on powerup / reset -----------------------------------------

  int melody[] = {262,196,196,220,196,1,247,262};
  int noteDurations[] = {4,8,8,4,4,4,4,4};
  for (byte Note = 0; Note < 8; Note++)                         // Play eight notes
  {
    long pulselength = 1000000/melody[Note];
    long noteDuration = 1000/noteDurations[Note];
    long pulses=noteDuration*1000/pulselength;
    if (pulselength>100000)                                     // Play pause
    {
      delay(noteDuration);
    }
    else
    {
      for(int p=0;p<pulses;p++)                                 // tone command not used to save memory
      {
    //    digitalWrite(Speakerpin,HIGH);
        delayMicroseconds(pulselength/2);
    //    digitalWrite(Speakerpin,LOW);
        delayMicroseconds(pulselength/2);
      }
      int pauseBetweenNotes = noteDuration * 0.30;
      delay(pauseBetweenNotes);
    }
  } 
}

void loop()
{//------------------------------------------ Walking Routine -------------------------------------------

  if (distance>700 && flift>0) stride=0;                     // stop if object is blocking path
  if (steer>0)                                               // change angle of feet to steer
  {
    Rkp=Rkc-steer;
    Lkp=Lkc;
  }
  else
  {
    Rkp=Rkc;
    Lkp=Lkc-steer;
  }
                                                             // NOTE: pulseIn timeout valueis used instead 
                                                             // of delay. No interrupt required.

  Rkp=Rkp+flift;                                             // lift right toe so it does not have traction
  for (i=stride;i>-stride;i-=4)
  {
    Servopos();                                              // update servos
    if(pulseIn(IRXpin, LOW,time) >2000) IRcommand();         // start bit is greater than 2000uS
  }
  Rkp=Rkp-flift;                                             // lower right toe to gain traction

  if(cmode==0) IReye();                                      // normal mode - read eye
  if(pulseIn(IRXpin, LOW,time) >2000) IRcommand();           // start bit is greater than 2000uS

  Lkp=Lkp-flift;                                             // lift left toe so it does not have traction
  for (i=-stride;i<stride;i+=4)
  {
    Servopos();
    if(pulseIn(IRXpin, LOW,time) >2000) IRcommand();         // start bit is greater than 2000uS
  }
  Lkp=Lkp+flift;                                             // lower right toe to gain traction


  if(cmode==0) IReye();                                      // normal mode - read eye                                   
  if(pulseIn(IRXpin, LOW,time) >2000) IRcommand();           // start bit is greater than 2000uS
}


void IRcommand() 
{//----------------------------------------- Receive IR commands from remote control ----------------------------------

  digitalWrite(LEDpin,HIGH);
  IRC=0;
  int j=1;
  for(int k=0;k<7;k++)                                    // read 7 data bits (ignore 5 bit device ID)
  {		                        
    pulse = pulseIn(IRXpin, LOW);
    if(pulse > 900)                                       // a pulse greater than 900uS is considered to be a 1
    {		                        
      IRC+=j;                                             // if it is a 1 then add 2 to the power of i
    }  
    j*=2; 
  }
  //Serial.println(IRC);
  switch (IRC)                                            // respond to valid IR commands
  {
  case 16:                                                // go forward
    mode=1;
    stride=400; //400                                     //lower stride goes faster!!!
    steer=0;
    flift=abs(flift);
    break;

  case 17:                                                // go backward
    mode=1;
    stride=400;
    steer=0;
    flift=-abs(flift);
    break;

  case 18:                                                // turn / kick right
    steer=150*mode;
    if (mode==0) Kick();
    break;

  case 19:                                                // turn / kick left
    steer=-150*mode;
    if (mode==0) Kick();
    break;

  case 37:                                                // stop
    steer=0;
    stride=0;
    mode=0;
    i=0;
    Lkp=Lkc;
    Rkp=Rkc;
    Lhp=Lhc;
    Rhp=Rhc;
    Servopos();
    break;

  case 20:                                                // calibration mode  0=off  1=on
    cmode=!cmode;
    Store();                                              // update EEPROM
    delay(300);
    break;
  }
  if (cmode==1)                                           // additional commands for calibration mode
  {
    switch (IRC)
    {
    case 1:                                               // restore defaults
      Lkc=1500; //1550
      Rkc=1500; //1550
      Lhc=1500;
      Rhc=1500;
      Store();
      break;
      
    case 0:                                               // left hip forward
      Lhc+=1;
      break;

    case 3:                                               // left hip backward
      Lhc-=1;
      break;

    case 6:                                               // left knee forward
      Lkc-=1;
      break;

    case 29:                                              // left knee backward
      Lkc+=1;
      break;

    case 2:
      Rhc-=1;                                             // right hip forward
      break;

    case 5:
      Rhc+=1;                                             // right hip backward
      break;

    case 8:
      Rkc+=1;                                             // right knee forward
      break;

    case 59:
      Rkc-=1;                                             // right knee backward
      break;
    } 
    
    stride=0;                                             // reset variables
    mode=0;
    i=0;
    Lkp=Lkc;
    Rkp=Rkc;
    Lhp=Lhc;
    Rhp=Rhc;
    Servopos();
  }
  digitalWrite(LEDpin,cmode);
}


void Servopos()                                            // update servo positions
{
  Lknee.writeMicroseconds(Lkp+i);
  Rknee.writeMicroseconds(Rkp+i);
  Lhip.writeMicroseconds(Lhp+i);
  Rhip.writeMicroseconds(Rhp+i);
}

void Kick()                                                // kick using left or right foot
{
  int LK=(IRC==19);
  int RK=(IRC==18);
  Lkp=Lkc-200*LK;
  Rkp=Rkp+200*RK;
  Servopos();
  delay(100);
  Lkp=Lkc+(400*LK)-(500*RK);
  Lhp=Lhc+(500*LK)-(500*RK);
  Rkp=Rkc+(500*LK)-(400*RK);
  Rhp=Rhc+(500*LK)-(500*RK);
  Servopos();
  delay(500);
  Lkp=Lkc;
  Rkp=Rkc;
  Lhp=Lhc;
  Rhp=Rhc;
  Servopos();
}


void IReye()//===============================================================Read IR compound eye================================================
{
  digitalWrite(IRpin,HIGH);                                     // turn on IR LEDs to read TOTAL IR LIGHT (ambient + reflected)
  delay(3);                                                     // Allow time for phototransistors to respond. (may not be needed)
  leftIRvalue=analogRead(Leftpin);                              // TOTAL IR = AMBIENT IR + LED IR REFLECTED FROM OBJECT
  rightIRvalue=analogRead(Rightpin);                            // TOTAL IR = AMBIENT IR + LED IR REFLECTED FROM OBJECT
  upIRvalue=analogRead(Uppin);                                  // TOTAL IR = AMBIENT IR + LED IR REFLECTED FROM OBJECT
  downIRvalue=analogRead(Downpin);                              // TOTAL IR = AMBIENT IR + LED IR REFLECTED FROM OBJECT

  digitalWrite(IRpin,LOW);                                      // turn off IR LEDs to read AMBIENT IR LIGHT (IR from indoor lighting and sunlight)
  delay(3);                                                     // Allow time for phototransistors to respond. (may not be needed)
  leftIRvalue=leftIRvalue-analogRead(Leftpin);                  // REFLECTED IR = TOTAL IR - AMBIENT IR
  rightIRvalue=rightIRvalue-analogRead(Rightpin);               // REFLECTED IR = TOTAL IR - AMBIENT IR
  upIRvalue=upIRvalue-analogRead(Uppin);                        // REFLECTED IR = TOTAL IR - AMBIENT IR
  downIRvalue=downIRvalue-analogRead(Downpin);                  // REFLECTED IR = TOTAL IR - AMBIENT IR

  distance=(leftIRvalue+rightIRvalue+upIRvalue+downIRvalue)/4;  // distance of object is average of reflected IR
  
  if (distance>110)                                             // object in range
  {
    int sound=1024-distance;                                    // generate sound to indicate distance
    for(int p=0;p<10;p++)
    {
  //    digitalWrite(Speakerpin,HIGH);
      delayMicroseconds(sound);
 //     digitalWrite(Speakerpin,LOW);
      delayMicroseconds(sound);
    }
  }
  lookUD=(upIRvalue-downIRvalue);
  lookLR=(leftIRvalue-rightIRvalue);

  /*
  Serial.print("Left:");
   Serial.print(leftIRvalue);
   Serial.print("  Right:");
   Serial.print(rightIRvalue);
   Serial.print("  Up:");
   Serial.print(upIRvalue);
   Serial.print("  Down:");
   Serial.print(downIRvalue);
   Serial.print("  Up/down:");
   Serial.print(lookUD);
   Serial.print("  Left/right:");
   Serial.println(lookLR);
   */

  if (distance>95)
  {
    lookUD=(upIRvalue-downIRvalue)/6;
    lookLR=(leftIRvalue-rightIRvalue)/6;
    Lhp=Lhp-lookUD;
    Lhp=constrain(Lhp,Lhc-400,Lhc+400);
    Rhp=Rhp+lookUD;
    Rhp=constrain(Rhp,Rhc-400,Rhc+400);
  }
  else
  {
    Lhp=Lhc;
    Rhp=Rhc;
  }
  Servopos();
  if (distance>390 && Lhp>Lhc)                                   // if object is close to feet
  {
    IRC=18+(lookLR>0);                                           // kick with closest foot
    Kick();
  }
}

void Store()                                                     // stores hip and knee positions in EEPROM
{
  EEPROM.write(0,highByte(Lkc));
  EEPROM.write(1,lowByte(Lkc));

  EEPROM.write(2,highByte(Rkc));
  EEPROM.write(3,lowByte(Rkc));

  EEPROM.write(4,highByte(Lhc));
  EEPROM.write(5,lowByte(Lhc));

  EEPROM.write(6,highByte(Rhc));
  EEPROM.write(7,lowByte(Rhc));
  /*
  Serial.print("   Left Knee: ");
   Serial.print(Lkc);
   Serial.print("   Right Knee: ");
   Serial.print(Rkc);
   Serial.print("   Left Hip: ");
   Serial.print(Lhc);
   Serial.print("   Right Hip: ");
   Serial.println(Rhc);
   */
}







