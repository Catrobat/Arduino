int sensePin = 2;
volatile unsigned long ticks;
volatile int pFlag = 0;
unsigned char value;

void setup()
{
  Serial.begin(115200);
  // pinMode(sensePin, INPUT);
  attachInterrupt(0, timeMeasure, RISING);
  Serial.println("init ...");
}

void loop()
{
  value = receiveChar();
  //   Serial.println(ticks);
  Serial.write(value);
}

void timeMeasure()
{
  ticks = 0;
  while (digitalRead(sensePin) == HIGH)
  {
    ticks++; 
  }
  pFlag=1;
}

unsigned char receiveChar()
{
  unsigned char character = 0;
  int counter = 8; 
  unsigned char mask = 0x80;

  for(int i = 0; i < counter; i++)
  {
    while (pFlag == 0);
    if(ticks > 280) //check if 0 or 1, 280 is a good meanvalue
    {
      //  Serial.print("1");
      character |= mask;
    }
    else
    {
      // Serial.print("0");
    }
    pFlag = 0;
    mask = mask/2;
  }
  //print rx data in ASCII
  //  Serial.println(character);
  return character;
}
