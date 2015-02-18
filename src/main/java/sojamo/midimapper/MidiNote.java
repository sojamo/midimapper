package sojamo.midimapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

public class MidiNote {

	private final AssignedDevice device;
	private final int note;
	private final Object parent;
	private final static Logger log = Logger.getLogger( MidiNote.class.getName( ) );

	public MidiNote( AssignedDevice theDevice , Object theParent , int theNote ) {
		device = theDevice;
		parent = theParent;
		note = theNote;
		log.setLevel( Level.WARNING );
	}

	public AssignedDevice to( Object theTarget , String theMember ) {
		return to( new MidiReceiver( note , theTarget , theMember ) );
	}

	public AssignedDevice to( String thePath ) {
		return evaluatePath( thePath );
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

	private AssignedDevice evaluatePath( String thePath ) {

		LinkedList< String > path = new LinkedList( Arrays.asList( thePath.split( " " ) ) );
		if ( path.size( ) < 2 ) {
			return to( parent , thePath );
		}

		Object member = parent;

		final String invoke = path.pollLast( );
		/* TODO check if invoke is numeric, if so, we should be operating on a list */

		for ( String s : path ) {
			Object o = evaluateMember( member , s );
			if ( o instanceof Field ) {
				log.info( "Field:" + ( ( Field ) o ).getType( ) );
				try {
					member = ( ( Field ) o ).get( member );
					log.info( "field-member:" + member );
				} catch ( IllegalArgumentException e ) {
					log.warning( e.getMessage( ) );
				} catch ( IllegalAccessException e ) {
					log.warning( e.getMessage( ) );
				}
			} else if ( o instanceof Method ) {
				log.info( "Method:" + ( ( Method ) o ).getReturnType( ) );
			} else {
				if ( member instanceof List ) {
					int index = MidiMapper.i( s , -1 );
					/* TODO check for valid index, list not empty, IndexOutOfBoundsException, nested list */
					List l = ( ( List ) member );
					log.info( "List:" + l );
					member = l.get( index );
				} else if ( member instanceof Map ) {
					/* TODO check if this is ever reached. */
					System.out.println( "map" + member );
					member = ( ( Map ) member ).get( s );

				} else {
					/* Something else */
					log.info( s + " " + member.getClass( ) );
				}
			}
		}
		return to( new MidiReceiver( note , member , invoke ) );
	}

	private final Object evaluateMember( final Object theObject , final String theName ) {
		Class< ? > c = theObject.getClass( );
		while ( c != null ) {
			try {
				final Field field = c.getDeclaredField( theName );
				field.setAccessible( true );
				return field;
			} catch ( Exception e ) {
				try {
					final Method method = c.getMethod( theName , new Class< ? >[] { } );
					return method;
				} catch ( SecurityException e1 ) {
					log.info( e.getMessage( ) );
				} catch ( NoSuchMethodException e1 ) {
					log.info( e.getMessage( ) );
				}
			}
			c = c.getSuperclass( );
		}
		return null;
	}

}
