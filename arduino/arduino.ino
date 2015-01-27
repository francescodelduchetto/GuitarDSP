/*
 * Arduino code for sending data (audio, touch_x, touch_y, touch_pressure)
 */

#include <stdint.h>

#define AUDIO_PIN (1 << 0)
volatile uint16_t sample;

volatile uint16_t i = 0;

//uint8_t packet[4] = {255, 255};

void setup(void) {
  Serial.begin(2000000);
  // ------- INTERRUPTS -----
  
  // ------- ADC -----
  //ADCSRA = 0;
  //ADCSRB = 0;

  //ADMUX |= (1 << REFS0); // reference voltage 5V
  
  //ADCSRA |= (1 << ADPS2); //| (1 << ADPS0); // ADC clock 32 prescaler - 16MHZ / 32 = 500kHZ
  //ADCSRA |= (1 << ADATE); // auto trigger 
  //ADCSRA |= (1 << ADIE); // enable interrupts after mesurement
  //ADCSRA |= (1 << ADIF); // enable interrupts flag 
  //ADCSRA |= (1 << ADEN); // enable ADC
  //ADCSRA |= (1 << ADSC); // start ADC
}

// -------------ADC interrupt
ISR(ADC_vect) {
  i++;
}

void loop(void) {
  //Serial.write(bytes, 10);
  //digitalWrite(13, LOW);
  //if (sample == 1023 || sample == 0) {
  //  digitalWrite(13, HIGH);
  //}
  //Serial.write(ADCH & 255);
  //Serial.write(ADCL & 255);
  uint16_t s = analogRead(A0);
  Serial.write(255);
  Serial.write(255); 
  Serial.write((s >> 8) & 255);
  Serial.write(s & 255);
}
