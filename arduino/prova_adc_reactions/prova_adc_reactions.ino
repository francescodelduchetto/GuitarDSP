//~ #include <stdint.h>
//~ #include <buttonImpl.cpp>
//~ 
//~ volatile uint8_t values[4] = {255, 255, 1, 0};
//~ buttonImpl buttons[8];
//~ 
//~ void setupADC() {
	//~ cli();
	//~ 
	  //~ //clear ADCSRA and ADCSRB registers
	  //~ ADCSRA = 0;
	  //~ ADCSRB = 0;
	  //~ 
	  //~ ADMUX |= (1 << REFS0); //set reference voltage
	  //~ ADMUX |= (1 << ADLAR); //left align the ADC value- so we can read highest 8 bits from ADCH register only
	  //~ 
	  //~ ADCSRA |= (1 << ADPS2) | (1 << ADPS1); //set ADC clock with 64 prescaler
	  //~ ADCSRA |= (1 << ADATE); //enabble auto trigger
	  //~ ADCSRA |= (1 << ADIE); //enable interrupts when measurement complete
	  //~ ADCSRA |= (1 << ADEN); //enable ADC
	  //~ ADCSRA |= (1 << ADSC); //start ADC measurements
	 //~ sei(); 
//~ }
//~ 
//~ 
//~ void pciSetup(byte pin) {
    //~ *digitalPinToPCMSK(pin) |= bit (digitalPinToPCMSKbit(pin));  // enable pin
    //~ PCIFR  |= bit (digitalPinToPCICRbit(pin)); // clear any outstanding interrupt
    //~ PCICR  |= bit (digitalPinToPCICRbit(pin)); // enable interrupt for the group 
//~ }
//~ 
//~ void setupButtons() {
	//~ for (int i=4; i<12; i++) {
		//~ pinMode(i, INPUT);
		//~ digitalWrite(i, HIGH);
		//~ pciSetup(i);
	//~ }
//~ }
//~ 
//~ void setup() {
	//~ Serial.begin(2000000);
	//~ // Create buttons
	//~ for (int i=0; i<8; i++) {
		//~ button[i] = new ButtonImpl(i+4);
	//~ }
//~ }
//~ 
//~ // Interrupt ADC
//~ ISR(ADC_vect) {
	//~ values[3] = ADCL;
	//~ values[2] = ADCH;
	//~ values[3] |= buttonPressed;
	//~ buttonPressed = 0;
	//~ Serial.write(values, 4);
//~ }
//~ 
//~ // Interrupt digital pins 8-13
//~ ISR (PCINT0_vect) {
	//~ for (int i=8; i<12; i++) {
		//~ if (digitalRead(i) == LOW) {
			//~ buttonPressed = i;
		//~ }
	//~ }
//~ }
//~ 
//~ // Interrupt digital pins 0-7
//~ ISR (PCINT2_vect) {
	//~ for (int i=4; i<8; i++) {
		//~ if (digitalRead(i) == LOW) {
			//~ buttonPressed = i;
		//~ }
	//~ }
//~ }
//~ 
//~ void loop() { 
//~ }

//~--------------------   Parte nuova

#include <react.h>
#include <ButtonImpl.h>
#include <AnalogDigitalConverter.h>
#include <PacketSender.h>

void buttonPressedReaction(Event* event) {
	PacketSender::getInstance()->setButtonPressed(static_cast<ButtonImpl*>(event->getSource())->getPin());
}

void sampleRead(Event* event) {
	PacketSender::getInstance()->setSample(static_cast<AnalogDigitalConverter*>(event->getSource())->getHighSample(), 
				static_cast<AnalogDigitalConverter*>(event->getSource())->getLowSample());
	PacketSender::getInstance()->sendPacket();
}

#define N_BUTTONS 8

Button buttons[N_BUTTONS];

void setup() {
	Serial.begin(2000000);
	// Buttons reactions
	for (int i=0; i<N_BUTTONS; i++) {
		addReaction(BUTTON_PRESSED, new ButtonImpl(i+4), buttonPressedReaction);
	}
	// ADC reactions
	AnalogDigitalConverter::getInstance()->init(F_500KHZ);
	addReaction(ADC_SAMPLE, AnalogDigitalConverter::getInstance(), sampleRead);
}

void loop() {
	// nothing
}






























