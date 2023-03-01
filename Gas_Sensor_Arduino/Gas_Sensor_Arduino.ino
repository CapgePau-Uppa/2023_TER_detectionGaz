#include <MQ2.h>

int analog_IN = A0;  // This is our input pin
int digital_IN = D1; // This is our input pin
MQ2 mq2(analog_IN);

void setup()
{
  // pinMode(LED_BUILTIN, OUTPUT);
  // pinMode(analog_IN, INPUT);
  Serial.begin(9600);

  mq2.begin();
}

void loop()
{

  char cstr[16];
  float *values = mq2.read(false);
  // Serial.print("MQ2 Read CO : ");
  Serial.println(mq2.readCO());
  // Serial.print("Digital Read : ");
  // Serial.println(digitalRead(digital_IN));
  // Serial.print("Analogic Read : ");
  // Serial.println(analogRead(analog_IN));
  /*if (digitalRead(digital_IN) == LOW){
    Serial.println("Seuil dépassé : Gaz detecté");
  }*/
  /*
  while(Serial.available() > 0) {
    Serial.write(Serial.read());
  }*/
  // Serial.write(itoa(mq2.readCO(), cstr, 10));

  Serial.println();
  delay(1000); // wait for a second
}
