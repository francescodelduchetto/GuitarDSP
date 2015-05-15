#ifndef __COUNTERH__
#define __COUNTERH__

#include <stdint.h>

class Counter {
public:
    void tick();
    uint16_t getValue();
    
private:
	volatile uint16_t value = 0;
};

#endif
