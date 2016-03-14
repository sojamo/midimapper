import sojamo.midimapper.*;

MidiMapper midi;
MidiOutMapper out;

void setup() {
  size(800, 400);
  midi = new MidiMapper(this);

  out = new MidiOutMapper();
  // assign an index to a midi channel and note
  String index = "/1/fader"; 
  int channel = 1;
  int note = 1;
  out.assign( index, channel, note);
}

void keyPressed() {
  
  // println(out.isAvailable());
  // send a midi control-change previously assigned to an index  
  out.send("/1/fader", frameCount%127);
}



void draw() {
}

