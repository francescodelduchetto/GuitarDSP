/*
 * Arduino code for sending data (audio, touch_x, touch_y, touch_pressure)
 */

#include <stdint.h>
#include "ButtonImpl.h"
#include "Counter.h"
#include "Dispatcher.h"

#define AUDIO_PIN (1 << 0)
#define N_BUTTON 8

volatile uint16_t i = 0, j = 0, count = 0;
volatile uint16_t timeStamp[2];

ButtonImpl *button[N_BUTTON];
Counter *counter;
Dispatcher *dispatcher;

void setup() {
    Serial.begin(2000000);
    
    // ------- COUNTER INTERRUPT -----
	TCCR0A = 0;// set entire TCCR0A register to 0
	TCCR0B = 0;// same for TCCR0B
	TCNT0  = 0;//initialize counter value to 0
	 // set compare match register for 2khz increments
	OCR0A = 255;// match register
	// turn on CTC mode
	TCCR0A |= (1 << WGM01);
	// Set CS01 and CS00 bits for 64 prescaler
	TCCR0B |= (1 << CS02) | (1 << CS00);   
	// enable timer compare interrupt
	//~ TIMSK0 |= (1 << OCIE0A);
	
	// ------- AUDIO INTERRUPT -----
	TCCR1A = 0;// set entire TCCR0A register to 0
	TCCR1B = 0;// same for TCCR0B
	TCNT1  = 0;//initialize counter value to 0
	 // set compare match register for 2khz increments
	OCR1A = 0;// match register
	// turn on CTC mode
	TCCR1A |= (1 << WGM11);
	// Set CS01 and CS00 bits for 64 prescaler
	TCCR1B |= (1 << CS10) | (1 << CS12);   
	// enable timer compare interrupt
	TIMSK1 |= (1 << OCIE1A);
	
    // ------- ADC -----
    //ADCSRA = 0;
    //ADCSRB = 0;

    //ADMUX |= (1 << REFS0); // reference voltage 5V

    //ADCSRA |= (1 << ADPS2); //| (1 << ADPS0); // ADC clock 32 prescaler - 16MHZ / 32 = 500kHZ
    //ADCSRA |= (1 << ADATE); // auto trigger 
    //ADCSRA |= (1 << ADIE); // enable interrupts after mesurement
    //ADCSRA |= (1 << ADIF); // enable interrupts flag 
    //ADCSRA |= (1 << ADEN); // enable ADC
    //ADCSRA |= (1 << ADSC); // start ADC
	
	dispatcher = new Dispatcher();
	for (int i=0; i<N_BUTTON; i++) {
		button[i] = new ButtonImpl(i);
		button[i]->registerListener(dispatcher);
	}
}

// -------------PIN CHANGE interrupt
ISR(PCINT0_vect) {
	for (int i=0; i<N_BUTTON; i++) {
		if (button[i]->isPressed(counter->getValue())) {
			button[i]->notifyListener();
		}
	}
}

ISR(TIMER0_COMPA_vect) {
	counter->tick();
}

ISR(TIMER1_COMPA_vect) {
	dispatcher->notifyNewSample(analogRead(A0));
}

void loop() {
	for (int i=0; i<N_BUTTON; i++) {
		if (button[i]->isPressed(counter->getValue())) {
			button[i]->notifyListener();
		}
	}
}
