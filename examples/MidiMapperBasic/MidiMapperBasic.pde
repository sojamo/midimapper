import sojamo.midimapper.*;
MidiMapper midi;
float a;

void setup() {
  midi = new MidiMapper(this);
  midi.connect("SLIDER/KNOB").assign(16).to("a");
}

void draw() {
  background(0);
  fill(255);
  translate(width/2, height/2);
  rotate(map(a, 0, 127, -PI, PI));
  translate(-20, -20);
  rect(0, 0, 40, 40);
}

