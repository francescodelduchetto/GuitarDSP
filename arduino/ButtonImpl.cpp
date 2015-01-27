#include "ButtonImpl.h"
#include "Arduino.h"

#define MAX_BUTTONS 8

ButtonImpl* buttons[MAX_BUTTONS];
uint8_t nButtons= 0;

void asd() {
}

bool registerNewButton(ButtonImpl* button) {
	if (nButtons < MAX_BUTTONS) {
		buttons[nButtons] = button;
		nButtons++;
		attachInterrupt(button->getPin(), asd, RISING);
		return true;
	} else {
		return false;
	}
}

ButtonImpl::ButtonImpl(uint8_t pin) {
	this->pin = pin;
	pinMode(pin, INPUT);
	this->nListeners = 0;
	registerNewButton(this);
}

uint8_t ButtonImpl::getPin() {
	return this->pin;
}

void ButtonImpl::notifyListeners() {
	for (int i=0; i<MAX_LISTENERS; i++) {
		this->listeners[i]->notifyButtonPressed();
	}
}

bool ButtonImpl::registerListener(ButtonListener* listener) {
	if (this->nListeners < MAX_LISTENERS) {
		this->listeners[this->nListeners] = listener;
		this->nListeners++;
		return true;
	} else {
		return false;
	}
}
