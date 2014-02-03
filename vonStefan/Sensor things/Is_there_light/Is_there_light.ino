int sensePin = 3;

void setup()
{
  analogReference(DEFAULT);
  Serial.begin(115200);  
}

void loop()
{
  //between 0 and 5 Volts are the values 0 to 1023
  Serial.println(analogRead(sensePin)); 
  //make decision if light is on or not
  if (analogRead(sensePin) < 100)
  {
    Serial.println("There is light");
  }
//  else if (analogRead(sensePin) == 0)
//  {
//    Serial.println("There is no light");
//  }
    else
  {
   Serial.println("No Light !!!"); 
  }
  delay(20);
}
