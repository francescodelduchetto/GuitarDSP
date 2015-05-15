#include "ButtonImpl.h"
#include "Arduino.h"


ButtonImpl::ButtonImpl(uint8_t pin) {
    this->pin = pin;
    pinMode(pin, INPUT);
    digitalWrite(pin, HIGH);
}

bool ButtonImpl::isPressed() {
	this->ticks++;
    if ((digitalRead(this->pin) == LOW) && (this->ticks > this->TOLERANCE)) {
		this->ticks = 0;
		return true;
	} else {
		return false;
	}
}

uint8_t ButtonImpl::getPin() {
    return this->pin;
}

void ButtonImpl::notifyListener() {
    this->listener->notifyButtonPressed(this->pin);
}

void ButtonImpl::registerListener(ButtonListener* listener) {
    this->listener = listener;
}
