package sojamo.midimapper;

import static sojamo.midimapper.MidiMapper.f;
import static sojamo.midimapper.MidiMapper.i;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class MidiReceiver implements Receiver {
	static public boolean DEBUG;
	private final Object target;
	private final String member;
	private final int note;

	Map< Byte , String > commandMap = new HashMap< Byte , String >( );
	{
		commandMap.put( ( byte ) -112 , "Note On" );
		commandMap.put( ( byte ) -128 , "Note Off" );
		commandMap.put( ( byte ) -48 , "Channel Pressure" );
		commandMap.put( ( byte ) -80 , "Continuous Controller" );
	}

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
			if ( DEBUG ) {
				if ( b[ 0 ] != -48 ) {
					// System.out.println("Message length: " +
					// msg.getLength());
					System.out.println( "Note command: " + commandMap.get( b[ 0 ] ) );
					System.out.println( "Which note: " + b[ 1 ] );
					System.out.println( "Note pressure: " + b[ 2 ] );
					System.out.println( "---------------------" );
				} else {
					// System.out.println("Message length: " +
					// msg.getLength());
					System.out.println( "Note command: " + commandMap.get( b[ 0 ] ) );
					System.out.println( "Note Pressure: " + b[ 1 ] );
					System.out.println( "---------------------" );
				}
			}
			MidiMapper.invoke( target , member , f( b[ 2 ] ) );
		}
	}
}
