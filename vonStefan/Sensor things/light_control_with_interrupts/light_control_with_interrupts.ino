int sensePin = 2;
volatile unsigned long ticks;
volatile int pFlag = 0;

void setup()
{
  Serial.begin(115200);
  // pinMode(sensePin, INPUT);
  attachInterrupt(0, timeMeasure, RISING);
  Serial.println("init ...");
}


void loop()
{
  if (pFlag == 1)
  {
    if(ticks > 100000)
    {
      Serial.println("1");
    }
    else
    {
      Serial.println("0");
    }
    pFlag = 0;
    Serial.println(ticks);
  }

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


