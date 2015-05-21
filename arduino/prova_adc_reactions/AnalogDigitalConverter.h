#ifndef __ADCH__
#define __ADCH__

#include <react.h>
#include <stdint.h>

#define F_8MHZ 		1
#define F_4MHZ 		2
#define F_2MHZ 		3
#define F_1MHZ 		4
#define F_500KHZ 	5
#define F_250KHZ 	6
#define F_125KHZ 	7

class AnalogDigitalConverter : public EventSource {
	public :
		static AnalogDigitalConverter* getInstance();
		void init(int frequency);
		uint8_t getLowSample();
		uint8_t getHighSample();
		void setLowSample(uint8_t lSample);
		void setHighSample(uint8_t hSample);
		
	private :
		AnalogDigitalConverter();
		int frequency;
		uint8_t lowSample;
		uint8_t highSample;
		static AnalogDigitalConverter* instance; // Singleton
};

#endif
