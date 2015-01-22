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

void setup(void) {
  Serial.begin(115200);
}

void loop(void) {
  // a point object holds x y and z coordinates
  TSPoint p = ts.getPoint();

  uint16_t packed[4] = {
    analogRead(A0) & mask,
    p.x & mask,
    p.y & mask,
    p.z & mask
  };

  uint8_t bytes[10] = {
    255,
    255
    packed[0] >> 8,
    packed[0],
    packed[1] >> 8,
    packed[1],
    packed[2] >> 8,
    packed[2],
    packed[3] >> 8,
    packed[3]
  };

  // we have some minimum pressure we consider 'valid'
  // pressure of 0 means no pressing!
  //if (p.z > ts.pressureThreshhold) {
     Serial.print(bytes, 10);
     Serial.flush();
  //}
}
