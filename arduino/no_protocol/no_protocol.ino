#include <stdint.h>

void setup() {
	Serial.begin(9600);
}

void loop() {
	uint8_t v0 = 0;
	uint8_t v1 = 10;
	uint8_t v2 = 100;
	Serial.write(v0);
	Serial.write(v0);
	Serial.write(v1);
	Serial.write(v2);
}