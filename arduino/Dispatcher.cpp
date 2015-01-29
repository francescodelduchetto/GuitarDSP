#include "Dispatcher.h"
#include "Arduino.h"

Dispatcher::Dispatcher() {
	this->packet[0] = this->packet[1] = 255;
}

void Dispatcher::notifyButtonPressed(uint8_t pin) {
	this->packet[4] = pin;
	this->commandAvailable = true;
}

void Dispatcher::notifyNewSample(uint16_t sample) {
	this->packet[2] = sample >> 8;
	this->packet[3] = sample & 255;
	if (this->commandAvailable) {
		//~ Serial.write(this->packet, 5);
		this->commandAvailable = false;
		//~ cli();
		for (int i=0; i<5; i++) {
			Serial.println((int)this->packet[i]);
		}
		//~ sei();
	} else {
		//~ Serial.write(this->packet, 4);
		//~ cli();
		for (int i=0; i<4; i++) {
			Serial.println((int)this->packet[i]);
		}
		//~ sei();
	}
}
