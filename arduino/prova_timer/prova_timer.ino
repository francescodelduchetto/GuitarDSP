#include <stdint.h>

volatile uint16_t value = 100;
volatile bool finished = true;
volatile int i=0;

void setupTimer() {
	cli();
	
// Timer 1
	TCCR1A = 0;
	TCCR1B = 0;
	TCNT1 = 0;

	OCR1A = 256 - 1;
	// turn on CTC mode
	 TCCR1B |= (1 << WGM12);
	 // Set CS11 for 8 prescaler
	 TCCR1B |= (1 << CS11) | (1 << CS10);
	 // enable timer compare interrupt
	 TIMSK1 |= (1 << OCIE1A);

// Timer 0
	TCCR0A = 0;
	TCCR0B = 0;
	TCNT0 = 0;
	
	OCR0A = 256 - 1;

	// turn on CTC mode
	TCCR0A |= (1 << WGM01);
	// Set CS01 and CS00 bits for 64 prescaler
	TCCR0B |= (1 << CS01) | (1 << CS00);   
	// enable timer compare interrupt
	TIMSK0 |= (1 << OCIE0A);		

	 // enabling interrupt
	 sei(); 
}

void setup() {
	Serial.begin(2000000);
	setupTimer();
	pinMode(A0, INPUT);
}

ISR(TIMER1_COMPA_vect) {
	uint16_t copy = value;
	Serial.write(255);	
	Serial.write(255);
	Serial.write(copy);
	//Serial.flush();
}

ISR(TIMER0_COMPA_vect) {
sei();
	value = analogRead(A0);
}

void loop() {
}
