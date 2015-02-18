/**
 * MidiMapper Send
 *
 * sends midi messages using the default midi interface assigned 
 * by javax.sound.midi.MidiSystem
 *
 * This method can be used to send midi messages for example to Ableton Live.
 * In Ableton Live make sure your midi ports are set accordingly.
 * For my osx setup I am using an IAC Driver which I have set as Input 
 * inside Ableton. To assign midi messages in Ableton Live, switch to midi map mode 
 * and highlight a controller, then send a midi message from this sketch, then
 * switch Ableton Live back to normal mode.
 *
 * midimapper library by Andreas Schlegel, 2015
 * www.github.com/sojamo/midimapper
 *
 */


import sojamo.midimapper.*;

MidiMapper midi;

void setup() {
  midi = new MidiMapper(this);
}

void draw() {
  
}

void mouseDragged() {
  /* send a midi message through the default midi interface
   * midi.send(theChannel, theData1, theData2)
   * int theChannel corresponds to the channel where this message will be sent to
   * int theData1 corresponds to the id of the midi message
   * int theData2 is an int value between 0-127 
   */
  midi.send(2,1,int(map(mouseX,0,width,0,127)));
}


