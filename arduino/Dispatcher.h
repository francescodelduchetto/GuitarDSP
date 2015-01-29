#ifndef __DISPATCHERH__
#define __DISPATCHERH__

#include "Button.h"
#include "AudioListener.h"

class Dispatcher: public ButtonListener, public AudioListener {
public:
	Dispatcher();
	void notifyButtonPressed(uint8_t);
	void notifyNewSample(uint16_t);
	void getPacket(uint8_t*);
	uint16_t getNBytes();
private:
	bool commandAvailable = false;
	uint8_t packet[5];
};
#endif
