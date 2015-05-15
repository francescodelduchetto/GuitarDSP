#ifndef __AUDIOLISTENERIMPLH__
#define __AUDIOLISTENERIMPLH__

#include <stdint.h>

class AudioListener {
public:
    virtual void notifyNewSample(uint16_t);
};

#endif

