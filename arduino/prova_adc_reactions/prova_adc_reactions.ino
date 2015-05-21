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






























