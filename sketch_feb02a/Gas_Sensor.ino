#include <MQ2.h>
#include <Wire.h> 

int analog_IN = A0;  // This is our input pin

int lpg, co, smoke;
MQ2 mq2(analog_IN);

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(analog_IN, INPUT);
  Serial.begin(9600);
  
  mq2.begin();
}

void loop() {
  float* values= mq2.read(true);
  
   lpg = mq2.readLPG();
  //co = values[1];
  co = mq2.readCO();
  //smoke = values[2];
  smoke = mq2.readSmoke();
  Serial.print("LPG:");
  Serial.println(lpg);
  Serial.print(" CO:");
  Serial.println(co);
  Serial.print("SMOKE:");
  Serial.println((smoke*100)/1000000);
  Serial.print(" %");

  delay(1000);                       // wait for a second
}
