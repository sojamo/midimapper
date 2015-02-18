package sojamo.midimapper;

import javax.sound.midi.MidiDevice;

public class AssignedDevice {

	private final MidiDevice device;
	private final Object parent;
	private final String name;
	
	AssignedDevice( Object theParent , MidiDevice theDevice, String theName ) {
		parent = theParent;
		device = theDevice;
		name = theName;
	}

	public MidiNote assign( int theNote ) {
		return new MidiNote( this , parent , theNote );
	}

	public MidiDevice get( ) {
		return device;
	}

	public boolean exists( ) {
		return !device.equals( null );
	}
	
	public String getName() {
		return name;
	}
	
}
