#include <stdint.h>


uint8_t values[5] = {255, 255, 512, 512 , 0};

void setup() {
	Serial.begin(2000000);
}

void loop() {
	uint16_t value = analogRead(A0);
	values[2] = (value >> 2) & 255;
	values[3] = (value & 0x00000011) << 6;
	Serial.write(values, 4);
} 
