/*
 * Arduino code for sending data (audio, touch_x, touch_y, touch_pressure)
 */

#include <stdint.h>
#include "TouchScreen.h"

#define YP A2  // must be an analog pin, use "An" notation!
#define XM A3  // must be an analog pin, use "An" notation!
#define YM 8   // can be a digital pin
#define XP 9   // can be a digital pin

// For better pressure precision, we need to know the resistance
// between X+ and X- Use any multimeter to read it
// For the one we're using, its 300 ohms across the X plate
TouchScreen ts = TouchScreen(XP, YP, XM, YM, 300);
volatile int c=0;
volatile int n= 1000;
volatile uint16_t val[1000];

void setup(void) {
// ------- INTERRUPTS -----
  //cli();
  // set timer1 interrupt at ...
  //TCCR1A = 0; // entire TCCR1A register to 0
  //TCCR1B = 0; // same 
  //TCNT1 = 0; // init counter to 0
  
  //OCR1A = 2; // compare match register 16 MHZ increments
  
  //TCCR1B |= (1 << WGM12); // turn on ctc mode
  
  //TCCR1B |= (1 << CS12);// | (1 << CS10); // set cs12 and cs10  bits for prescaler 1024
  
  //TIMSK1 |= (1 << OCIE1A); // enable time compare interrupt
  
  //sei(); // allow interrupt
  
// ------- ADC -----
cli();
  ADCSRA = 0;
  ADCSRB = 0;
  
  ADMUX |= (1 << REFS0); // reference voltage 5V
  //ADMUX |= (1 << ADLAR); //left align
  
  ADCSRA |= (1 << ADPS2) | (1 << ADPS1) | (1 << ADPS0); //| (1 << ADPS0); // ADC clock 32 prescaler - 16MHZ / 32 = 500kHZ
  ADCSRA |= (1 << ADATE); // 
  ADCSRA |= (1 << ADIE); // enable interrupts after mesurement
  ADCSRA |= (1 << ADEN); // enable ADC
  ADCSRA |= (1 << ADSC); // start ADC
sei();
  Serial.begin(500000);
  for (int i=0; i<n;i++) {
    val[i]=0;
  }
  PORTB = B00100000;
}


ISR(TIMER1_COMPA_vect) {
  
//  if(c<n) {
  //  PORTB = B00100000;
    //val[c] = (ADCH << 8) | ADCL;
   // c++;
//  } else {
  //  PORTB = B00000000;
    //TCCR1B &= (~0) ^ ((1 << CS12));
    //c =0;
 // }
}

ISR(ADC_vect) {
  //Serial.write("a");
  //val[c] =  (ADCH << 8) | ADCL;
 c++;
 //if (c == n) {
   //PORTB = B00000000;
   //ADCSRA &= 0;
 //}
}

void loop(void) {
  // a point object holds x y and z coordinates
//  TSPoint p = ts.getPoint();

  //uint16_t packed[4] = {
    //analogRead(A0),
    //0,//p.x,
    //0,//p.y,
    //0//p.z
  //};

  //uint8_t bytes[10] = {
    //255,
    //255,
    //packed[0] >> 8,
    //packed[0],
    //packed[1] >> 8,
    //packed[1],
    //packed[2] >> 8,
    //packed[2],
    //packed[3] >> 8,
    //packed[3]
  //};

  // we have some minimum pressure we consider 'valid'
  // pressure of 0 means no pressing!
  //if (p.z > ts.pressureThreshhold) {
     //Serial.write(bytes, 10);
     //Serial.println(packed[0]);
     //Serial.flush();
  
  //}
 // for (int i=0; i<n; i++) {
  //  Serial.println(val[i]);
  //}
  cli();
  Serial.println(c);
  Serial.println("finito");
  sei();
  //c=0;
}


