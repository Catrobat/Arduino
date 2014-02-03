int sensePin = 3;



void setup()
{
  analogReference(DEFAULT);
  Serial.begin(115200);
}

void loop()
{
  static unsigned long tic = 0, toc = 0;
  int intensity = 0;
  //between 0 and 5 Volts are the values 1023 to 0
  //if dark the value is 1023, full light is going to 13

  while (analogRead(sensePin) > 100);

  tic = micros(); // attention micros resolution is 4 microseconds
  intensity = analogRead(sensePin); //attention starting intensity, maybe average
  
  while(analogRead(sensePin) < 100);
  
  toc = micros();

  Serial.print("duration = ");
  Serial.print(toc-tic);
  Serial.print(" us, intensity = ");
  Serial.println(intensity); 
}

