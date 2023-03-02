#include <MQ2.h>

int analog_IN = 0; // This is our input pin
// int digital_IN = D1; // This is our input pin
MQ2 mq2(analog_IN);

float sensorValue;

void setup()
{
  // pinMode(LED_BUILTIN, OUTPUT);
  // pinMode(analog_IN, INPUT);
  Serial.begin(9600);

  mq2.begin();
  Serial.println("MQ2 warming up!");
  delay(20000); // allow the MQ2 to warm up
}

void loop()
{
  sensorValue = analogRead(analog_IN);
  float *values = mq2.read(false);
  // Serial.print("MQ2 Read CO : ");
  Serial.println(values[0]);
  // Serial.print("Digital Read : ");
  // Serial.println(digitalRead(digital_IN));
  Serial.print("Analogic Read : ");
  Serial.println(sensorValue);
  /*if (digitalRead(digital_IN) == LOW){
    Serial.println("Seuil dépassé : Gaz detecté");
  }*/
  // while(Serial.available() > 0) {
  //   Serial.write(Serial.read());
  // }
  // Serial.write(itoa(mq2.readCO(), cstr, 10));
  // Serial.println();
  delay(1000); // wait for a second
}