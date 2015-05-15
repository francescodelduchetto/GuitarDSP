#ifndef __BUTTONH__
#define __BUTTONH__

#include <stdint.h>

class ButtonListener {
public:
    virtual void notifyButtonPressed(uint8_t);
};

class Button {
public:
    virtual bool isPressed();
    virtual void registerListener(ButtonListener*);
};

#endif
