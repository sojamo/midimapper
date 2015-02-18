package sojamo.midimapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class MidiMapper {

	static private final Logger log = Logger.getLogger( MidiMapper.class.getName( ) );
	static public final String VERSION = "0.1.2-alpha";
	private final Object parent;

	public MidiMapper( Object theObject ) {
		parent = theObject;
		welcome( );
	}

	private final void welcome( ) {
		log.info( String.format( "midimap, %1s" , VERSION ) );
	}

	public final AssignedDevice test( AssignedDevice theDevice ) {
		return test( theDevice.getName( ) );
	}

	public final AssignedDevice test( String theName ) {
		return connect( theName , new TestReceiver( theName ) );
	}

	public final AssignedDevice test( int theId ) {
		return connect( getNameFromId( theId ) , new TestReceiver( getNameFromId( theId ) ) );
	}

	public String getNameFromId( int theId ) {
		return MidiSystem.getMidiDeviceInfo( )[ theId ].getName( );
	}

	public final AssignedDevice connect( int theId , Receiver ... theReceivers ) {
		return connect( getNameFromId( theId ) , theReceivers );
	}

	public final AssignedDevice connect( int theId ) {
		return connect( getNameFromId( theId ) );
	}

	public final AssignedDevice connect( String theName ) {
		try {
			MidiDevice device;
			device = MidiSystem.getMidiDevice( getMidiDeviceInfo( theName , false ) );
			device.open( );
			return new AssignedDevice( parent , device , theName );
		} catch ( MidiUnavailableException e ) {
			e.printStackTrace( );
			return new AssignedDevice( parent , null , theName );
		} catch ( NullPointerException e ) {
			log.info( String.format( "No Midi device ( %1s ) is available." , theName ) );
			return new AssignedDevice( parent , null , theName );
		}
	}

	public final AssignedDevice connect( String theName , Receiver ... theReceivers ) {

		try {
			MidiDevice device = MidiSystem.getMidiDevice( getMidiDeviceInfo( theName , false ) );
			device.open( );

			for ( Receiver receiver : theReceivers ) {
				Transmitter conTrans = device.getTransmitter( );
				conTrans.setReceiver( receiver );
			}
			return new AssignedDevice( parent , device , theName );
		} catch ( MidiUnavailableException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace( );
			return new AssignedDevice( parent , null , theName );
		} catch ( NullPointerException e ) {
			log.info( String.format( "No Midi device ( %1s ) is available." , theName ) );
			return new AssignedDevice( parent , null , theName );
		}
	}

	/* theData1 corresponds to the id of the midi message, theData2 is a midi value between 0-127 */

	public MidiMapper send( int theChannel , int theData1 , int theData2 ) {
		Receiver receiver = null;
		try {
			receiver = MidiSystem.getReceiver( );
		} catch ( MidiUnavailableException e ) {
			System.err.println( "Midi Device unavailable." );
		}
		int command = ShortMessage.CONTROL_CHANGE;
		int timeStamp = -1;
		return send( receiver , command , theChannel , theData1 , theData2 , timeStamp );
	}

	public MidiMapper send( Receiver theReceiver , int theCommand , int theChannel , int theData1 , int theData2 , int theTimeStamp ) {
		ShortMessage msg = new ShortMessage( );
		try {
			msg.setMessage( theCommand , theChannel , theData1 , theData2 );
			theReceiver.send( msg , theTimeStamp );
		} catch ( InvalidMidiDataException e ) {
			System.err.println( "Invalid Midi Device." );
		} catch ( NullPointerException e ) {
			System.err.println( "Invalid Midi Device." );
		}
		return this;
	}

	static public ArrayList list( ) {
		return find( "" );
	}

	static public ArrayList find( final String thePattern ) {
		MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo( );
		ArrayList found = new ArrayList( );
		for ( int i = 0 ; i < info.length ; i++ ) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice( info[ i ] );
				boolean in = ( device.getMaxTransmitters( ) != 0 );
				boolean out = ( device.getMaxReceivers( ) != 0 );
				String pattern = "(?i)^.*?(" + thePattern + ").*$";
				boolean a = info[ i ].getName( ).matches( pattern );
				boolean b = info[ i ].getVendor( ).matches( pattern );
				boolean c = info[ i ].getDescription( ).matches( pattern );
				if ( thePattern.length( ) == 0 || ( a || b || c ) ) {
					found.add( toMap( "id" , i , "name" , info[ i ].getName( ) , "in?" , in , "out?" , out , "vendor" , info[ i ].getVendor( ) , "version" , info[ i ].getVersion( ) , "description" , info[ i ].getDescription( ) ) );
				}
			} catch ( MidiUnavailableException e ) {
				e.printStackTrace( );
			}
		}
		return found;
	}

	static public Object invoke( final Object theObject , final String theMember , final Object ... theParams ) {

		Class[] cs = new Class[ theParams.length ];

		for ( int i = 0 ; i < theParams.length ; i++ ) {
			Class c = theParams[ i ].getClass( );
			cs[ i ] = classmap.containsKey( c ) ? classmap.get( c ) : c;
		}
		try {
			final Field f = theObject.getClass( ).getDeclaredField( theMember );
			/* TODO check super */
			f.setAccessible( true );
			Object o = theParams[ 0 ];
			Class cf = o.getClass( );
			if ( cf.equals( Integer.class ) ) {
				f.setInt( theObject , i( o ) );
			} else if ( cf.equals( Float.class ) ) {
				f.setFloat( theObject , f( o ) );
			} else if ( cf.equals( Long.class ) ) {
				f.setLong( theObject , l( o ) );
			} else if ( cf.equals( Double.class ) ) {
				f.setDouble( theObject , d( o ) );
			} else if ( cf.equals( Boolean.class ) ) {
				f.setBoolean( theObject , b( o ) );
			} else if ( cf.equals( Character.class ) ) {
				f.setChar( theObject , ( char ) i( o ) );
			} else {
				f.set( theObject , o );
			}
		} catch ( NoSuchFieldException e1 ) {
			try {
				final Method m = theObject.getClass( ).getDeclaredMethod( theMember , cs );
				/* TODO check super */
				m.setAccessible( true );
				try {
					return m.invoke( theObject , theParams );
				} catch ( IllegalArgumentException e ) {
					System.err.println( e );
				} catch ( IllegalAccessException e ) {
					System.err.println( e );
				} catch ( InvocationTargetException e ) {
					System.err.println( e );
				}

			} catch ( SecurityException e ) {
				System.err.println( e );
			} catch ( NoSuchMethodException e ) {
				System.err.println( e );
			}
		} catch ( IllegalArgumentException e ) {
			System.err.println( e );
		} catch ( IllegalAccessException e ) {
			System.err.println( e );
		}
		return null;
	}

	static public int i( final Object o , final int theDefault ) {
		return ( o instanceof Number ) ? ( ( Number ) o ).intValue( ) : ( o instanceof String ) ? i( s( o ) ) : theDefault;
	}

	static public String s( final Object o ) {
		return ( o != null ) ? o.toString( ) : "";
	}

	static public int i( final String o , final int theDefault ) {
		return isNumeric( o ) ? Integer.parseInt( o ) : theDefault;
	}

	static public boolean b( Object o ) {
		return ( o instanceof Boolean ) ? ( ( Boolean ) o ).booleanValue( ) : ( o instanceof Number ) ? ( ( Number ) o ).intValue( ) == 0 ? false : true : false;
	}

	static public long l( Object o ) {
		return ( o instanceof Number ) ? ( ( Number ) o ).longValue( ) : Long.MIN_VALUE;
	}

	static public double d( Object o ) {
		return ( o instanceof Number ) ? ( ( Number ) o ).doubleValue( ) : Double.MIN_VALUE;
	}

	static public int i( Object o ) {
		return ( o instanceof Number ) ? ( ( Number ) o ).intValue( ) : Integer.MIN_VALUE;
	}

	static public int i( String o ) {
		return isNumeric( o ) ? Integer.parseInt( o ) : Integer.MIN_VALUE;
	}

	static public float f( Object o ) {
		return ( o instanceof Number ) ? ( ( Number ) o ).floatValue( ) : Float.MIN_VALUE;
	}

	static public float f( String o ) {
		return isNumeric( o ) ? Float.parseFloat( o ) : Integer.MIN_VALUE;
	}

	static Map< Class< ? > , Class< ? > > classmap = new HashMap( ) {
		{
			put( Integer.class , int.class );
			put( Float.class , float.class );
			put( Double.class , double.class );
			put( Boolean.class , boolean.class );
			put( Character.class , char.class );
			put( Long.class , long.class );
		}
	};

	static public boolean isNumeric( Object o ) {
		return isNumeric( o.toString( ) );
	}

	static public boolean isNumeric( String str ) {
		return str.matches( "(-|\\+)?\\d+(\\.\\d+)?" );
	}

	static public Map toMap( final Object ... args ) {
		Map m = new LinkedHashMap( );
		if ( args.length % 2 == 0 ) {
			for ( int i = 0 ; i < args.length ; i += 2 ) {
				m.put( args[ i ] , args[ i + 1 ] );
			}
		}
		return m;
	}

	static public ByteBuffer clone( ByteBuffer original ) {
		ByteBuffer clone = ByteBuffer.allocate( original.capacity( ) );
		clone.clear( );
		original.rewind( );
		clone.put( original );
		original.rewind( );
		clone.flip( );
		return clone;
	}

	static public void listDevicesAndExit( boolean bForInput , boolean bForOutput ) {
		listDevicesAndExit( bForInput , bForOutput , false );
	}

	static public void listDevicesAndExit( boolean bForInput , boolean bForOutput , boolean bVerbose ) {
		if ( bForInput && !bForOutput ) {
			out( "Available MIDI IN Devices:" );
		} else if ( !bForInput && bForOutput ) {
			out( "Available MIDI OUT Devices:" );
		} else {
			out( "Available MIDI Devices:" );
		}

		MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo( );
		for ( int i = 0 ; i < aInfos.length ; i++ ) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice( aInfos[ i ] );
				boolean bAllowsInput = ( device.getMaxTransmitters( ) != 0 );
				boolean bAllowsOutput = ( device.getMaxReceivers( ) != 0 );
				if ( ( bAllowsInput && bForInput ) || ( bAllowsOutput && bForOutput ) ) {
					if ( bVerbose ) {
						out( "" + i + "  " + ( bAllowsInput ? "IN " : "   " ) + ( bAllowsOutput ? "OUT " : "    " ) + aInfos[ i ].getName( ) + ", " + aInfos[ i ].getVendor( ) + ", " + aInfos[ i ].getVersion( ) + ", " + aInfos[ i ].getDescription( ) );
					} else {
						out( "" + i + "  " + aInfos[ i ].getName( ) );
					}
				}
			} catch ( MidiUnavailableException e ) {
				// device is obviously not available...
				// out(e);
			}
		}
		if ( aInfos.length == 0 ) {
			out( "[No devices available]" );
		}
		System.exit( 0 );
	}

	/**
	 * Retrieve a MidiDevice.Info for a given name.
	 * 
	 * This method tries to return a MidiDevice.Info whose name
	 * matches the passed name. If no matching MidiDevice.Info is
	 * found, null is returned. If bForOutput is true, then only
	 * output devices are searched, otherwise only input devices.
	 * 
	 * @param strDeviceName the name of the device for which an info
	 *            object should be retrieved.
	 * @param bForOutput If true, only output devices are
	 *            considered. If false, only input devices are considered.
	 * @return A MidiDevice.Info object matching the passed device
	 *         name or null if none could be found.
	 */
	public static MidiDevice.Info getMidiDeviceInfo( String strDeviceName , boolean bForOutput ) {
		MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo( );
		for ( int i = 0 ; i < aInfos.length ; i++ ) {
			if ( aInfos[ i ].getName( ).equals( strDeviceName ) ) {
				try {
					MidiDevice device = MidiSystem.getMidiDevice( aInfos[ i ] );
					boolean bAllowsInput = ( device.getMaxTransmitters( ) != 0 );
					boolean bAllowsOutput = ( device.getMaxReceivers( ) != 0 );
					if ( ( bAllowsOutput && bForOutput ) || ( bAllowsInput && !bForOutput ) ) {
						return aInfos[ i ];
					}
				} catch ( MidiUnavailableException e ) {
					// TODO:
				}
			}
		}
		return null;
	}

	/**
	 * Retrieve a MidiDevice.Info by index number.
	 * This method returns a MidiDevice.Info whose index
	 * is specified as parameter. This index matches the
	 * number printed in the listDevicesAndExit method.
	 * If index is too small or too big, null is returned.
	 * 
	 * @param index the index of the device to be retrieved
	 * @return A MidiDevice.Info object of the specified index
	 *         or null if none could be found.
	 */
	public static MidiDevice.Info getMidiDeviceInfo( int index ) {
		MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo( );
		if ( ( index < 0 ) || ( index >= aInfos.length ) ) {
			return null;
		}
		return aInfos[ index ];
	}

	private static void out( String strMessage ) {
		System.out.println( strMessage );
	}

	private class TestReceiver implements Receiver {

		private final String name;
		private Map< Byte , String > commandMap = new HashMap< Byte , String >( );

		public TestReceiver( String theName ) {
			name = theName;
			commandMap.put( ( byte ) -112 , "Note On" );
			commandMap.put( ( byte ) -128 , "Note Off" );
			commandMap.put( ( byte ) -48 , "Channel Pressure" );
			commandMap.put( ( byte ) -80 , "Continuous Controller" );
		}

		public void send( MidiMessage msg , long timeStamp ) {

			byte[] b = msg.getMessage( );
			Map result = new LinkedHashMap( );
			
			result.put( "name" , name );
			result.put( "type" , commandMap.get( b[ 0 ] ) );
			result.put( "status" , msg.getStatus( ) );

			if ( b[ 0 ] != -48 ) {
				result.put( "note" , b[ 1 ] );
				result.put( "pressure" , b[ 2 ] );
			} else {
				result.put( "pressure" , b[ 1 ] );
			}
			result.put( "time-stamp" , timeStamp );

			System.out.println( result.toString( ) );
		}

		public void close( ) {
		}
	}
}
