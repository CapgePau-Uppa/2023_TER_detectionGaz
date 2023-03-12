#include <MQ2.h>

int analog_IN = D0; // This is our input pin (IO2)
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

  if (smokeLvl > 0.03)
  {
    Serial.println("Seuil dépassé : CO2 détecté");
  }

  delay(1000); // wait for a second
}
