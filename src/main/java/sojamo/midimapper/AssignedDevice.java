package sojamo.midimapper;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class AssignedDevice {

	private final MidiDevice device;
	private final Object parent;

	AssignedDevice( Object theParent , MidiDevice theDevice ) {
		parent = theParent;
		device = theDevice;
		//		for ( Receiver receiver : theReceivers ) {
		// 			Transmitter conTrans = device.getTransmitter( );
		//			conTrans.setReceiver( receiver );
		//		}
	}
	
	public MidiNote assign( int theNote ) {
		return new MidiNote( parent , theNote );
	}
}
