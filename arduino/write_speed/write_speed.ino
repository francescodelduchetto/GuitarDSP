#include <stdint.h>

void setup() {
	Serial.begin(2000000);
}

void loop() {
	uint8_t packet[] = {255, 255, 255, 255};
	uint32_t t1, t2, offset;
	t1 = micros();
	t2 = micros();
	offset = t2 - t1;
	t1 = micros();
	Serial.write(packet, 4);
	t2 = micros();
	offset = t2 - t1 - offset;
	Serial.println(offset);
}