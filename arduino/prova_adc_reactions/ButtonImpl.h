#ifndef __BUTTONIMPLH__
#define __BUTTONIMPLH__

#include "Button.h"

#define MAX_BUTTONS 14
#define TOLERANCE 	100
 
class ButtonImpl: public Button {
public:
    ButtonImpl(uint8_t pin);
    virtual bool isPressed();
    uint8_t getPin();

private:
    uint8_t pin;
    long previousTimestamp;
};

#endif
