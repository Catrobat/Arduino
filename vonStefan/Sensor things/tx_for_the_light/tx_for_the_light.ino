int ledPin = 12;

void setup()
{
 pinMode(ledPin, OUTPUT);
 Serial.begin(115200);
}

void loop()
{
 unsigned char data;

 // receive char from serial port

 if (Serial.available() > 0)
 {
   data = Serial.read();
   sendChar(data);
 }



 //sendChar('3');
 //delay(100);
 //sendChar(0xad);
 //delay(1000);
}

void sendBit(int data)
{
 //Serial.println(data);
 digitalWrite(ledPin, HIGH);
 delay(1 + data);
 digitalWrite(ledPin, LOW);
 delay(2 - data);
}

void sendChar(unsigned char data)
{
 unsigned char mask = 0x80;
 int i;
 for (i=0;i<8;i++)
 {
   if (data & mask)
     sendBit(1);
   else
     sendBit(0);
   mask = mask /2;
 }
}

