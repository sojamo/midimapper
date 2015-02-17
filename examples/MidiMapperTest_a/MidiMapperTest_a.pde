

import sojamo.midimapper.*;

MidiMapper midi;
AssignedDevice korg;

float n;
Test test;

void setup() {
  /* create a Midi Mapper */
  MidiMapper midi = new MidiMapper(this);
  
   /* list all available Midi devices */
  midi.list();
  
  /* find all devices that contain Korg in their name or info. this differs from OS to OS.*/
  midi.find("Korg"); 
  
  /* a test class, we will assign a midi controller to a memeber of class test below.*/
  test = new Test();
  
  /* connect Midi Mapper to a midi device and assign members of this sketch to midi controls. */
 korg = midi.connect("SLIDER/KNOB");
 korg.assign(16).to("n").assign(17).to(test,"b").assign(18).to("c");

}


void draw() {
println(n, test.b);
}

void c(float theValue) {
  println("c",theValue);
}


class Test {
  float b;
}
