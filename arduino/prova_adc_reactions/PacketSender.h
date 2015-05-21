#include <react.h>

class PacketSender {
	public :
		static PacketSender* getInstance();
		void setButtonPressed(uint8_t button);
		void setSample(uint8_t highByte, uint8_t lowByte);
		void sendPacket();
		
	private :
		PacketSender(); 
		uint8_t packet[4];
		uint8_t button;
		static PacketSender* instance; // singleton
};
