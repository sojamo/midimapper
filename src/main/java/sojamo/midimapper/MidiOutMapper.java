package sojamo.midimapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiOutMapper {

	private Receiver receiver;
	private final Map< String , MidiData > map = new HashMap< String , MidiData >( );;

	public MidiOutMapper( ) {
		try {
			receiver = MidiSystem.getReceiver( );
		} catch ( MidiUnavailableException e ) {
			System.err.println( "Midi Device unavailable." + e );
		}
	}

	public MidiOutMapper( String theName ) {
		/* some odd behavior happening here, this works from time to time, use on your own
		 * risk, confusion and frustration. it is recommended to call constructor MidiOutMapper()
		 * to assign the midi output to the default MidiOut port defined by the MidiSystem. */
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo( );

		for ( MidiDevice.Info info : infos ) {
			try {
				if ( MidiSystem.getMidiDevice( info ).toString( ).contains( "MidiOutDevice" ) ) {
					if ( info.getName( ).equalsIgnoreCase( theName ) ) {
						receiver = MidiSystem.getMidiDevice( info ).getReceiver( );
						// System.out.println( "Got a receiver, " + info.getName( ) + " / " + info.getDescription( ) );
						break;
					}
				}
			} catch ( MidiUnavailableException e ) {
				e.printStackTrace( );
			}
		}
	}

	static public List list( ) {
		List< MidiDevice.Info > list = new ArrayList< MidiDevice.Info >( );
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo( );
		for ( MidiDevice.Info info : infos ) {
			try {
				System.out.println( info + " " + MidiSystem.getMidiDevice( info ).getMaxReceivers( ) + "/" + MidiSystem.getMidiDevice( info ).getMaxTransmitters( ) );
			} catch ( MidiUnavailableException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace( );
			}
			list.add( info );
		}
		return list;
	}

	public Receiver getReceiver( ) {
		return receiver;
	}

	public boolean isAvailable( ) {
		return receiver != null;
	}

	public MidiOutMapper send( int theCommand , int theChannel , int theData1 , int theData2 , int theTimeStamp ) {
		ShortMessage msg = new ShortMessage( );
		try {
			msg.setMessage( theCommand , theChannel , theData1 , theData2 );
			getReceiver( ).send( msg , theTimeStamp );
		} catch ( InvalidMidiDataException e ) {
			System.err.println( "Invalid Midi Device." );
		} catch ( NullPointerException e ) {
			System.err.println( "Invalid Midi Device." );
		}
		return this;
	}

	public MidiOutMapper send( int theChannel , int theData1 , int theData2 ) {
		return send( ShortMessage.CONTROL_CHANGE , theChannel , theData1 , theData2 , -1 );
	}

	public MidiOutMapper trigger( String theIndex ) {
		send( theIndex , 0 , -1 );
		return this;
	}

	public MidiOutMapper send( String theIndex , int theValue ) {
		send( theIndex , theValue , -1 );
		return this;
	}

	public MidiOutMapper send( String theIndex , int theValue , int theTimeStamp ) {
		MidiData data = map.get( theIndex );
		if ( data != null ) {
			data.setData2( theValue );
			send( data.getCommand( ) , data.getChannel( ) , data.getData1( ) , data.getData2( ) , data.getTimeStamp( ) );
		}
		return this;
	}

	public MidiOutMapper assign( String theIndex , MidiData theData ) {
		map.put( theIndex , theData );
		return this;
	}

	public MidiOutMapper assign( String theIndex , int theChannel , int theData1 ) {
		MidiData data = new MidiData( ).setChannel( theChannel ).setData1( theData1 );
		map.put( theIndex , data );
		return this;
	}

}
