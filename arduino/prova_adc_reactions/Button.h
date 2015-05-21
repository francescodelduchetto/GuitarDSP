#ifndef __BUTTONH__
#define __BUTTONH__

#include <react.h>

class Button : public EventSource {
public:
    virtual bool isPressed();
};

#endif
