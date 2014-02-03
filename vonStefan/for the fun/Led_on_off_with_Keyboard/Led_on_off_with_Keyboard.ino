const unsigned int LED_PIN = 13;
const unsigned int BAUD_RATE = 9600;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  Serial.begin(BAUD_RATE);
}


void loop() {
  if (Serial.available() > 0) {
    int command = Serial.read();
    if (command == '1') {
      digitalWrite(LED_PIN, HIGH);
      Serial.println("LED on");
    } else if (command =='2') {
      digitalWrite(LED_PIN, LOW);
      Serial.println("LED off");
    } else {
      Serial.print("Unkonwn command: ");
      Serial.println(command);
      Serial.println(command, DEC);
      Serial.println(command, HEX);
      Serial.println(command, OCT);
      Serial.println(command, BIN);
//      Serial.println(command, BYTE); //no longer supported
//write instead:
      Serial.write(command);
    }
  }
}

      


