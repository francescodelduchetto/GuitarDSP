#ifndef __BUTTONLISTENERIMPLH__
#define __BUTTONLISTENERIMPLH__

#include "Button.h"

class ButtonListenerImpl: public ButtonListener {
public:
    virtual void notifyButtonPressed();
};

#endif
