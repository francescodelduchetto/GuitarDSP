#include "Counter.h"
#include <stdint.h>

void Counter::tick() {
	this->value++;
}

uint16_t Counter::getValue() {
	return this->value;
}
