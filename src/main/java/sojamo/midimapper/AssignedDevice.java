package sojamo.midimapper;

import javax.sound.midi.MidiDevice;

public class AssignedDevice {

	private final MidiDevice device;
	private final Object parent;

	AssignedDevice( Object theParent , MidiDevice theDevice ) {
		parent = theParent;
		device = theDevice;
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
	
}
