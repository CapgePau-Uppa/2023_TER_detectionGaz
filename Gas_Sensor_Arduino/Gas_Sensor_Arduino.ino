#include <MQ2.h>
#include <math.h>

int analog_IN = 2; // This is our input pin (IO2)
MQ2 mq2(analog_IN);

void(* resetFunc) (void) = 0;

void setup()
{
  Serial.begin(9600);

  mq2.begin();
  delay(20000);
}

void loop()
{

  float *values = mq2.read(false);
  float smokeLvl = mq2.readSmoke()*1000;
  if(smokeLvl == NAN  || smokeLvl == INFINITY){
    Serial.println("inf");
    resetFunc();
  }
  float LPGLvl = mq2.readLPG()*1000;
  if(LPGLvl == NAN  || LPGLvl == INFINITY){
    Serial.println("inf");
    resetFunc();
  }
  float COLvl = mq2.readCO()*1000;
  if(COLvl == NAN  || COLvl == INFINITY){
    Serial.println("inf");
    resetFunc();
  }
  Serial.print("{\"smoke\":");
  Serial.print(smokeLvl);
  Serial.print(",\"LPG\":");
  Serial.print(LPGLvl);
  Serial.print(",\"CO\":");
  Serial.print(COLvl);
  
  Serial.print("}");

  delay(1000); // wait for a second
}
