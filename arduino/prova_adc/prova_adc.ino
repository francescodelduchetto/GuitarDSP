#include <stdint.h>

uint8_t values[4] = {255, 255, 1, 0};
uint8_t buttonPressed;

void setupADC() {
	cli();
	
	  //clear ADCSRA and ADCSRB registers
	  ADCSRA = 0;
	  ADCSRB = 0;
	  
	  ADMUX |= (1 << REFS0); //set reference voltage
	  //ADMUX |= (1 << ADLAR); //left align the ADC value- so we can read highest 8 bits from ADCH register only
	  
	  ADCSRA |= (1 << ADPS2) | (1 << ADPS1); //set ADC clock with 32 prescaler- 16mHz/32=500kHz
	  ADCSRA |= (1 << ADATE); //enabble auto trigger
	  ADCSRA |= (1 << ADIE); //enable interrupts when measurement complete
	  ADCSRA |= (1 << ADEN); //enable ADC
	  ADCSRA |= (1 << ADSC); //start ADC measurements
	 sei(); 
}

void setupButtons() {
	for (int i=4; i<12; i++) {
		pinMode(i, INPUT);
		digitalWrite(i, HIGH);
	}
}

void setup() {
	Serial.begin(2000000);
	setupADC();
	setupButtons();
}

ISR(ADC_vect) {
	values[3] = ADCL;
	values[2] = ADCH;
	values[2] |= (buttonPressed << 4);
	Serial.write(values, 4);
}

void loop() {
	buttonPressed = 0;
	for (int i=4; i<12; i++) {	
		if (digitalRead(i) == LOW) {
			buttonPressed = i;
		}
	} 
}

