#include <MQ2.h>

int analog_IN = 2; // This is our input pin (IO2)
MQ2 mq2(analog_IN);

void setup()
{
  Serial.begin(9600);

  mq2.begin();
  delay(20000);
}

void loop()
{

  // float *values = mq2.read(false);
  float smokeLvl = mq2.readSmoke();
  float LPGLvl = mq2.readLPG();
  float COLvl = mq2.readCO();
  Serial.print("{\"smoke\":");
  Serial.print(smokeLvl);
  Serial.print(",\"LPG\":");
  Serial.print(LPGLvl);
  Serial.print(",\"CO\":");
  Serial.print(COLvl);
  Serial.print("}");

  delay(1000); // wait for a second
}
