#ifndef __REACT__
#define	__REACT__

#include <stdint.h>

#define BUTTON_PRESSED 1
#define ADC_SAMPLE 2

class EventSource { 
	public :
		virtual ~EventSource() {};
};

class Event {
	public :
	
		Event(int type, EventSource* source);
		int getType();
		EventSource* getSource();
		virtual ~Event() {};
		
	private :
	
		int type;
		EventSource* source;
};

class Reaction {
	public :
		
		Reaction(int type, EventSource* source, void (*proc)(Event* event));
		bool isTriggered(Event* event);
		void fire(Event* event);
		
	private : 
		
		int type;
		EventSource* source;
		void (*proc)(Event* event);
};

#define MAX_REACTIONS 20

class ReactionManager {
	public :
		static ReactionManager* getInstance();
		bool addReaction(int type, EventSource* source, void (*proc)(Event* event));
		void checkReactions(Event* ev);
		
	private :
		ReactionManager();
		int nReactions;
		Reaction* reactions[MAX_REACTIONS];
		static ReactionManager* instance; // singleton
};

bool addReaction(int type, EventSource* source, void (*proc)(Event* event));

void checkReactions(Event* event);

#endif
