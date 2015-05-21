#include <PacketSender.h>
#include <react.h>
#include <Arduino.h>

PacketSender* PacketSender::instance = new PacketSender();

PacketSender::PacketSender() {
	this->packet[0] = this->packet[1] = 255;
}

PacketSender* PacketSender::getInstance(){
	return instance;
}

void PacketSender::setButtonPressed(uint8_t button) {
	this->button = button;
}

void PacketSender::setSample(uint8_t highByte, uint8_t lowByte) {
	this->packet[2] = highByte;
	this->packet[3] = lowByte;
	this->packet[3] |= button;
}

void PacketSender::sendPacket() {
	Serial.write(this->packet, 4);
	this->button = 0;
}
