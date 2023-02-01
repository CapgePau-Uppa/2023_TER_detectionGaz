
int analog_IN = A0;  // This is our input pin

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(analog_IN, INPUT);
  Serial.begin(9600);
}

void loop() {
  int Value = analogRead(analog_IN);  
  Serial.print("Analog read: ");
  Serial.println(Value);  

  delay(1000);                       // wait for a second
}
