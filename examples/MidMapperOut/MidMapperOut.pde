import sojamo.midimapper.*;

MidiMapper midi;
MidiOutMapper out;

float n;

void setup() {
  midi = new MidiMapper(this);
  println(midi.list());
  println(MidiOutMapper.list());
  out = new MidiOutMapper();
  out.assign("/1/fader", 1,1); 
}

void keyPressed() {
  // midi.send(1,1,int(random(127)));
  // midi.send(out, 1,1,int(random(127)));
  println(out.isAvailable());
  out.send("/1/fader", frameCount%127);
}



void draw() {
}
