#ifndef __BUTTONIMPLH__
#define __BUTTONIMPLH__

#include "Button.h"

#define MAX_BUTTONS 14
#define TOLERANCE 	1000
 
class ButtonImpl: public Button {
public:
    ButtonImpl(int pin);
    virtual bool isPressed();
    uint8_t getPin();

private:
    uint8_t pin;
    uint16_t ticks;
};

#endif
