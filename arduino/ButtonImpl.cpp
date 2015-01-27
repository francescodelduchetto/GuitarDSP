#include "ButtonImpl.h"
#include "Arduino.h"

#define MAX_BUTTONS 8

ButtonImpl* buttons[MAX_BUTTONS];
uint8_t nButtons= 0;

bool registerNewButton(ButtonImpl* button) {
	if (nButtons < MAX_BUTTONS) {
		buttons[nButtons] = button;
		nButtons++;
		attachInterrupt(button->getPin(), button->notifyListeners, RISING);
		return true;
	} else {
		return false;
	}
}

void ButtonImpl::ButtonImpl(uint8_t pin) {
	this->pin = pin;
	pinMode(pin, INPUT);
	this->nListeners = 0;
	registerNewButton(this);
}

uint8_t ButtonImpl::getPin() {
	return this->pin;
}

void ButtonImpl::notifyListeners() {
	for (auto listener: this->listeners) {
		listener->notifyButtonPressed();
	}
}

bool ButtonImpl::registerListener(ButtonListener* listener) {
	if (this->nListeners < this->MAX_LISTENERS) {
		this->listeners[this->nListeners] = listener;
		this->nListeners++;
		return true;
	} else {
		return false;
	}
}
