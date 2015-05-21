#include <react.h>

// ----------------Event implementation
Event::Event(int type, EventSource* source) {
	this->type = type;
	this->source = source;
}

int Event::getType() {
	return this->type;
}

EventSource* Event::getSource() {
	return this->source;
}

// ----------------Reaction implementation
Reaction::Reaction(int type, EventSource* source, void (*proc)(Event* event)){
  this->type = type;
  this->source = source;
  this->proc = proc;
}

bool Reaction::isTriggered(Event* event){
  if (event->getType() == this->type && event->getSource() == this->source){
    return true;
  } else {
    return false; 
  }
}
  
void Reaction::fire(Event* event){
  (*proc)(event);
}

// ----------------ReactionManager implementation
ReactionManager* ReactionManager::getInstance(){
  return instance;
}

ReactionManager::ReactionManager(){
  nReactions = 0;  
}

ReactionManager* ReactionManager::instance = new ReactionManager();
  
bool ReactionManager::addReaction(int type, EventSource* source, void (*proc)(Event* event)){
  if (nReactions < MAX_REACTIONS){
    reactions[nReactions] = new Reaction(type, source, proc);
    nReactions++;
    return true;
  } else {
    return false;  
  }
}
  
void ReactionManager::checkReactions(Event* event){
  for (int i = 0; i < nReactions; i++){
    if (reactions[i]->isTriggered(event)){
      reactions[i]->fire(event);
    }  
  }
}

// ----------------GLOBAL
bool addReaction(int type, EventSource* source, void (*proc)(Event* event)){
  return ReactionManager::getInstance()->addReaction(type, source, proc);
}  

void checkReactions(Event* event){
  ReactionManager::getInstance()->checkReactions(event);
}
