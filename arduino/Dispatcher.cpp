#include "Dispatcher.h"
#include "Arduino.h"

#define DEBUG 0

Dispatcher::Dispatcher() {
	this->packet[0] = this->packet[1] = 255;
}

void Dispatcher::notifyButtonPressed(uint8_t pin) {
	this->packet[4] = pin;
	this->commandAvailable = true;
}

void Dispatcher::notifyNewSample(uint16_t sample) {
	this->packet[2] = 200;//sample & 255;
	this->packet[3] = 99;//sample >> 8;
	if (this->commandAvailable) {
		this->commandAvailable = false;
#if DEBUG
		for (int i=0; i<5; i++) {
			Serial.println((int)this->packet[i]);
		}
#else
		Serial.write(this->packet, 5);
#endif
	} else {
#if DEBUG
		for (int i=0; i<4; i++) {
			Serial.println((int)this->packet[i]);
		}
#else
		Serial.write(this->packet, 4);
#endif
	}
}
