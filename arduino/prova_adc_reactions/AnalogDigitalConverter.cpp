#include <react.h>
#include <AnalogDigitalConverter.h>
#include <Arduino.h>

// Interrupt ADC
ISR(ADC_vect) {
	AnalogDigitalConverter* converter = AnalogDigitalConverter::getInstance(); 
	converter->setLowSample(ADCL);
	converter->setHighSample(ADCH);
	Event* event = new Event(ADC_SAMPLE, converter);
	checkReactions(event);
	delete event;
}

void setupADC(int frequency) {
	cli();
	
	//clear ADCSRA and ADCSRB registers
	ADCSRA = 0;
	ADCSRB = 0;

	ADMUX |= (1 << REFS0); //set reference voltage
	ADMUX |= (1 << ADLAR); //left align the ADC value- so we can read highest 8 bits from ADCH register only

	switch (frequency) {
		case F_8MHZ : 
			ADCSRA |= (1 << ADPS0) ; //set ADC clock with 2 prescaler
			break;
		case F_4MHZ :
			ADCSRA |= (1 << ADPS1); //set ADC clock with 4 prescaler
			break;
		case F_2MHZ : 
			ADCSRA |= (1 << ADPS0) | (1 << ADPS1); //set ADC clock with 8 prescaler
			break;
		case F_1MHZ : 
			ADCSRA |= (1 << ADPS2); //set ADC clock with 16 prescaler
			break;
		case F_500KHZ :
			ADCSRA |= (1 << ADPS0) | (1 << ADPS2); //set ADC clock with 32 prescaler
			break;
		case F_250KHZ : 
			ADCSRA |= (1 << ADPS1) | (1 << ADPS2); //set ADC clock with 64 prescaler
			break;
		default : 
			ADCSRA |= (1 << ADPS0) | (1 << ADPS1) | (1 << ADPS2); //set ADC clock with 128 prescaler
	}
	
	ADCSRA |= (1 << ADPS2) | (1 << ADPS1); //set ADC clock with 64 prescaler
	ADCSRA |= (1 << ADATE); //enabble auto trigger
	ADCSRA |= (1 << ADIE); //enable interrupts when measurement complete
	ADCSRA |= (1 << ADEN); //enable ADC
	ADCSRA |= (1 << ADSC); //start ADC measurements
	sei(); 
}

AnalogDigitalConverter* AnalogDigitalConverter::instance = new AnalogDigitalConverter();

AnalogDigitalConverter* AnalogDigitalConverter::getInstance() {
	return instance;
}

void AnalogDigitalConverter::init(int frequency) {
	this->frequency = frequency;
	setupADC(frequency);
}

uint8_t AnalogDigitalConverter::getLowSample() {
	return this->lowSample;
}

uint8_t AnalogDigitalConverter::getHighSample() {
	return this->highSample;
}

void AnalogDigitalConverter::setLowSample(uint8_t lSample) {
	this->lowSample = lSample;
}

void AnalogDigitalConverter::setHighSample(uint8_t hSample) {
	this->highSample = hSample;
}

AnalogDigitalConverter::AnalogDigitalConverter() { }
