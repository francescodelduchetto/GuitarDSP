#include "ButtonImpl.h"
#include "Arduino.h"

void pciSetup(byte pin) {
    //~ *digitalPinToPCMSK(pin) |= bit (digitalPinToPCMSKbit(pin));  // enable pin
    //~ PCIFR  |= bit (digitalPinToPCICRbit(pin)); // clear any outstanding interrupt
    //~ PCICR  |= bit (digitalPinToPCICRbit(pin)); // enable interrupt for the group 
}

ButtonImpl::ButtonImpl(uint8_t pin) {
    this->pin = pin;
    this->timestamp = 0;
    pciSetup(pin);
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
