import sojamo.midimapper.*;
MidiMapper midi;
float a;
float b;
void setup() {
  midi = new MidiMapper(this);
  midi.load("midi.json");
}

void draw() {
  background(frameCount%255);
  fill(255);
  pushMatrix();
  translate(width/2, height/4);
  rotate(map(a, 0, 127, -PI, PI));
  translate(-20, -20);
  rect(0, 0, 40, 40);
  popMatrix();

  pushMatrix();
  translate(width/2, height/1.5);
  rotate(map(b, 0, 127, -PI, PI));
  translate(-20, -20);
  rect(0, 0, 40, 40);
  popMatrix();
}