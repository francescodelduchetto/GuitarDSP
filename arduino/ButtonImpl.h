#ifndef __BUTTONIMPLH__
#define __BUTTONIMPLH__

#include "Button.h"
#include <stdint.h>

class ButtonImpl: public Button {
public:
    ButtonImpl(uint8_t);
    bool isPressed();
    bool registerListener(ButtonListener*);

    void notifyListeners();
    uint8_t getPin();

private:
    uint8_t pin;
    uint8_t nListeners;
    ButtonListener* listeners[MAX_LISTENERS];  
};

#endif
