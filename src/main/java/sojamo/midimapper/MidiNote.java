package sojamo.midimapper;

public class MidiNote {

	private final int note;
	private final Object parent;

	public MidiNote( Object theParent , int theNote ) {
		parent = theParent;
		note = theNote;
	}
	
	public MidiReceiver to( Object theTarget , String theMember ) {
		return new MidiReceiver( note , theTarget , theMember );
	}

	public MidiReceiver to( String theMember ) {
		return to( parent , theMember );
	}
}
