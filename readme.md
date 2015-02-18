# MidiMapper

MidiMapper is a processing library that allows to maps midi devices and their events to members of a sketch such as variables and functions. Currently this library is in development stage.

## The most basic example
Connect to a midi device and assign a note to a variable. Controller changes will be automatically assigned to the value of the variable.

```java

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
```


_18 February 2015_