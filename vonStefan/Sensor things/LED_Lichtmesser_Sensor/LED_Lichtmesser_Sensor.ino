static int LED1_Neg=6;
static int LED1_Pos=7;
static int LED2_Neg=2;
static int LED2_Pos=3;

void setup(){
  Serial.begin(9600); // um beobachten zu können wie sich die LED verhält kann später gelöscht werden
  pinMode(LED2_Neg,OUTPUT);
  pinMode(LED2_Pos,OUTPUT);
}

void loop(){
  unsigned long zeit;
 //led 1 verkehrt herum gescchaltet und aufladen
  pinMode(LED1_Neg,OUTPUT);
  pinMode(LED1_Pos,OUTPUT);
  digitalWrite(LED1_Neg,HIGH);
  digitalWrite(LED1_Pos,LOW);
  //n Seite der led zum eingang
  pinMode(LED1_Neg,INPUT);   
  digitalWrite(LED1_Neg,LOW);
  //zeit messen bis low
  zeit=millis();
  while(digitalRead(LED1_Neg)==HIGH);
  zeit=millis()-zeit;
  //ausgabe an rechner
  Serial.print("TIME from high to low: ");
  Serial.println(zeit);

  if(zeit<5) {
    digitalWrite(LED2_Pos,HIGH); // Die zweite LED an
    digitalWrite(LED2_Neg,LOW);
    }
  else {
    digitalWrite(LED2_Pos,LOW); // oder aus
    digitalWrite(LED2_Neg,LOW);
    }
}

