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
	uint16_t value = analogRead(A0);
	t2 = micros();
	offset = t2 - t1 - offset;
	Serial.write(packet, 4);
	Serial.write((offset >> 0) & 255);
	Serial.write((offset >> 8) & 255);
	Serial.write((offset >> 16) & 255);
	Serial.write((offset >> 24) & 255);
} 