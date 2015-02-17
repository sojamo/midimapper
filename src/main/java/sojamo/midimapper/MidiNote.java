package sojamo.midimapper;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

public class MidiNote {

	private final AssignedDevice device;
	private final int note;
	private final Object parent;

	public MidiNote( AssignedDevice theDevice , Object theParent , int theNote ) {
		device = theDevice;
		parent = theParent;
		note = theNote;
	}

	public AssignedDevice to( Object theTarget , String theMember ) {
		return to( new MidiReceiver( note , theTarget , theMember ) );
	}
	
	public AssignedDevice to( String theMember ) {
		return to( parent , theMember );
	}

	public AssignedDevice to( MidiReceiver theReceiver ) {
		try {
			Transmitter conTrans;
			conTrans = device.get( ).getTransmitter( );
			conTrans.setReceiver( theReceiver );
		} catch ( MidiUnavailableException e ) {
			e.printStackTrace( );
		} catch ( NullPointerException e ) {}
		return device;
	}
}
