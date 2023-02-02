
int analog_IN = A0;  // This is our input pin

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(analog_IN, INPUT);
  Serial.begin(9600);
}

void loop() {
  int Value = analogRead(analog_IN);
  float voltage = Value * (5.0 / 1023.0);
  float CO_concentration = ((voltage * 1000) - 400) / 3.3;

  
  Serial.print("Analog read: ");
  Serial.println(CO_concentration);  

  delay(1000);                       // wait for a second
}
