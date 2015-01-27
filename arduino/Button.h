#ifndef __BUTTONH__
#define __BUTTONH__

class ButtonListener {
public:
    virtual void notifyButtonPressed() = 0;
};

class Button {
public:
    virtual bool isPressed() = 0;
    virtual bool registerListener(ButtonListener*) = 0;
};

#endif
