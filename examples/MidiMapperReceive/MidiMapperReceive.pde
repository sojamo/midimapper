/**
 * MidiMapper Receive
 *
 * This sketch shows the basic prinicples of how to access a midi device and 
 * how to assign midi events to individual members of the sketch. 
 * A member here is a variable or function.
 *
 * midimapper library by Andreas Schlegel, 2015
 * www.github.com/sojamo/midimapper
 *
 */


import sojamo.midimapper.*;

MidiMapper midi;
AssignedDevice korg;

float a;

void setup() {
  /* create a Midi Mapper */
  MidiMapper midi = new MidiMapper(this);

  /* list all available Midi devices */
  println(midi.list());
  
  /* midi.list() returns a list of maps
   * each map corresonds to a midi device and contains information about the device
   */

  /* find all devices that contain Korg in their name or info. this differs from OS to OS.*/
  println(midi.find("Korg"));
  
  /* midi.find(String) returns a list of search results; each result is of Map
   * each Map corresonds to a midi device and contains information about the device,
   * below we will use the name of a device (here I am using a Korg nanoKontrol2),
   * instead you can also use the id of a device (see Map). Ids come in handy when
   * you connect more than one of the same device.
   */
   
  
  /* connect Midi Mapper to a midi device and assign members of this sketch to midi events. */
  korg = midi.connect("SLIDER/KNOB");
  
  /* A midi device can be connected by name or by id; the id of a device 
   *can be found inside midi.list() 
   */
  
  /* lets assign member a and b of this sketch each to a midi control, here note 16 and 17 */
  korg.assign(16).to("a").assign(17).to("b");
  
  /* use midi.test() to print each midi event of a device into the console */
  midi.test(korg);
}


void draw() {
  println("a:",a);
}

void b(float theValue) {
  println("b:", theValue);
}


