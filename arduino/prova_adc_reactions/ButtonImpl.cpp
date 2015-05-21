#include "ButtonImpl.h"
#include "Arduino.h"
#include "react.h"

ButtonImpl* buttons[MAX_BUTTONS];
int nButtons = 0;

// Interrupt digital pins 0-7
ISR (PCINT2_vect) {
	for (int i=0; i<nButtons; i++) {
		if (buttons[i]->getPin() < 8 && buttons[i]->isPressed()) {
			Event* event = new Event(BUTTON_PRESSED, buttons[i]);
			checkReactions(event);
			delete event;
		}
	}
}

// Interrupt digital pins 8-13
ISR (PCINT0_vect) {
	for (int i=0; i<nButtons; i++) {
		if (buttons[i]->getPin() > 7 && buttons[i]->isPressed()) {
			Event* event = new Event(BUTTON_PRESSED, buttons[i]);
			checkReactions(event);
			delete event;
		}
	}
}

void registerNewButton(ButtonImpl* button) {
	if (nButtons < MAX_BUTTONS) {
		buttons[nButtons++] = button;
	}
	cli();
    *digitalPinToPCMSK(button->getPin()) |= bit (digitalPinToPCMSKbit(button->getPin()));  // enable pin
    PCIFR  |= bit (digitalPinToPCICRbit(button->getPin())); // clear any outstanding interrupt
    PCICR  |= bit (digitalPinToPCICRbit(button->getPin())); // enable interrupt for the group 
    sei();
}

ButtonImpl::ButtonImpl(uint8_t pin) {
	this->previousTimestamp = millis();
    this->pin = pin;
    pinMode(this->pin, INPUT);
    digitalWrite(this->pin, HIGH);
    registerNewButton(this);
}

bool ButtonImpl::isPressed() {
	long timestamp = millis();
    if (timestamp > TOLERANCE + this->previousTimestamp && digitalRead(this->pin) == LOW) {
		this->previousTimestamp = timestamp;
		return true;
	} else {
		return false;
	}
}

uint8_t ButtonImpl::getPin() {
    return this->pin;
}

