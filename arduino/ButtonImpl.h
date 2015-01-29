#ifndef __BUTTONIMPLH__
#define __BUTTONIMPLH__

#include "Button.h"
#include <stdint.h>
 
class ButtonImpl: public Button {
public:
    ButtonImpl(uint8_t);
    bool isPressed(uint16_t);
    void registerListener(ButtonListener*);

    void notifyListener();
    uint8_t getPin();

private:
	const uint16_t TOLERANCE = 30;
    uint8_t pin;
    ButtonListener* listener;  
    uint16_t timestamp;
};

#endif
