package sojamo.midimapper;

import javax.sound.midi.ShortMessage;

public class MidiData {

	private int command;
	private int channel;
	private int data1;
	private int data2;
	private int timeStamp;

	public MidiData( ) {
		command = ShortMessage.CONTROL_CHANGE;
		channel = 0;
		data1 = 0;
		data2 = 0;
		timeStamp = -1;
	}

	public MidiData setCommand( int theCommand ) {
		command = theCommand;
		return this;
	}

	public MidiData setChannel( int theChannel ) {
		channel = theChannel;
		return this;
	}

	public MidiData setData1( int theData1 ) {
		data1 = theData1;
		return this;
	}

	public MidiData setData2( int theData2 ) {
		data2 = theData2;
		return this;
	}

	public MidiData setValue( int theValue ) {
		return setData2( theValue );
	}

	public MidiData setTimeStamp( int theTimeStamp ) {
		timeStamp = theTimeStamp;
		return this;
	}

	public int getCommand( ) {
		return command;
	}

	public int getChannel( ) {
		return channel;
	}

	public int getData1( ) {
		return data1;
	}

	public int getData2( ) {
		return data2;
	}

	public int getValue( ) {
		return getData2( );
	}

	public int getTimeStamp( ) {
		return timeStamp;
	}

}
