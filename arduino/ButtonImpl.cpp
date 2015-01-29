#include "ButtonImpl.h"
#include "Arduino.h"


ButtonImpl::ButtonImpl(uint8_t pin) {
    this->pin = pin;
    this->timestamp = 0;
    pinMode(pin, INPUT);
    digitalWrite(pin, HIGH);
}

bool ButtonImpl::isPressed(uint16_t counterValue) {
    return (digitalRead(this->pin) == LOW) && (counterValue - this->timestamp < this->TOLERANCE);
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
