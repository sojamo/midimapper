package sojamo.midimapper;

import static sojamo.midimapper.MidiMapper.f;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class MidiReceiver implements Receiver {
	static public boolean DEBUG;
	private final Object target;
	private final String member;
	private final int note;

	public MidiReceiver( int theNote , Object theTarget , String theMember ) {
		note = theNote;
		target = theTarget;
		member = theMember;
	}

	@Override public void close( ) {
	}

	@Override public void send( MidiMessage theMessage , long timeStamp ) {
		final byte[] b = theMessage.getMessage( );
		if ( b[ 1 ] == note ) {
			MidiMapper.invoke( target , member , f( b[ 2 ] ) );
		}
	}
}
