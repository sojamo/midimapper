package sojamo.midimapper;

import processing.core.PApplet;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static sojamo.midimapper.Common.*;

@SuppressWarnings({"unused", "unchecked"})
public class MidiMapper {

    static private final Logger log = Logger.getLogger(MidiMapper.class.getName());
    static public final String VERSION = "0.2.1";
    private final PApplet parent;
    public final static String k_name = "name";
    public final static String k_id = "id";
    public final static String k_in = "in";
    public final static String k_out = "out";
    public final static String k_vendor = "vendor";
    public final static String k_version = "version";
    public final static String k_type = "type";
    public final static String k_status = "status";
    public final static String k_note = "note";
    public final static String k_pressure = "pressure";
    public final static String k_timestamp = "timestamp";
    public final static String k_device = "device";
    public final static String k_mapping = "mapping";

    public MidiMapper(PApplet theObject) {
        parent = theObject;
        welcome();
    }

    private void welcome() {
        log.info(String.format("midimapper, %1s", VERSION));
    }

    public final AssignedDevice test(final AssignedDevice theDevice) {
        return test(theDevice.getName());
    }

    public final AssignedDevice test(final String theName) {
        return connect(theName, new TestReceiver(theName));
    }

    public final AssignedDevice test(final int theId) {
        return connect(getNameFromId(theId), new TestReceiver(getNameFromId(theId)));
    }

    public String getNameFromId(final int theId) {
        return MidiSystem.getMidiDeviceInfo()[theId].getName();
    }

    public final AssignedDevice connect(final int theId, final Receiver... theReceivers) {
        return connect(getNameFromId(theId), theReceivers);
    }

    public final AssignedDevice connect(final Object theObject) {
        if (theObject instanceof Number) {
            return connect(getNameFromId(((Number) theObject).intValue()));
        } else {
            return connect(theObject.toString());
        }
    }

    public final AssignedDevice connect(final int theId) {
        return connect(getNameFromId(theId));
    }

    public final AssignedDevice connect(final String theName) {
        try {
            MidiDevice device;
            device = MidiSystem.getMidiDevice(getMidiDeviceInfo(theName, false));
            device.open();
            return new AssignedDevice(parent, device, theName);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            return new AssignedDevice(parent, null, theName);
        } catch (NullPointerException e) {
            log.info(String.format("No Midi device ( %1s ) is available.", theName));
            return new AssignedDevice(parent, null, theName);
        }
    }


    public final AssignedDevice connect(final Object theObject, final Receiver... theReceivers) {
        return connect(theObject.toString(), theReceivers);
    }

    public final AssignedDevice connect(final String theName, final Receiver... theReceivers) {

        try {
            MidiDevice device = MidiSystem.getMidiDevice(getMidiDeviceInfo(theName, false));
            device.open();
            for (Receiver receiver : theReceivers) {
                Transmitter transmitter = device.getTransmitter();
                transmitter.setReceiver(receiver);
            }
            return new AssignedDevice(parent, device, theName);
        } catch (MidiUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new AssignedDevice(parent, null, theName);
        } catch (NullPointerException e) {
            log.info(String.format("No Midi device ( %1s ) is available.", theName));
            return new AssignedDevice(parent, null, theName);
        }
    }

    /**
     * Disconnecting a midi device does often result in not being able
     * to reopen the same device during a session again.
     *
     * @param theObject
     * @return
     */
    public final MidiMapper disconnect(final Object theObject) {
        if (theObject instanceof Number) {
            return disconnect(getNameFromId(((Number) theObject).intValue()));
        } else {
            return disconnect(theObject.toString());
        }
    }

    public final MidiMapper disonnect(final int theId) {
        return disconnect(getNameFromId(theId));
    }

    public final MidiMapper disconnect(final String theName) {
        try {
            MidiDevice device;
            device = MidiSystem.getMidiDevice(getMidiDeviceInfo(theName, false));
            if (device != null && device.isOpen()) {
                device.close();
            }
            return this;
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            return this;
        } catch (NullPointerException e) {
            log.info(String.format("No Midi device ( %1s ) is available.", theName));
            return this;
        }
    }

	/* theData1 corresponds to the id of the midi message, theData2 is a midi value between 0-127 */

    public MidiMapper send(final int theChannel,
                           final int theData1,
                           final int theData2) {
        Receiver receiver = null;
        try {
            receiver = MidiSystem.getReceiver();
        } catch (MidiUnavailableException e) {
            System.err.println("Midi Device unavailable." + e);
        }
        int command = ShortMessage.CONTROL_CHANGE;
        int timeStamp = -1;
        return send(receiver, command, theChannel, theData1, theData2, timeStamp);
    }

    public MidiMapper send(final MidiOutMapper theMidiOutMapper,
                           final int theChannel,
                           final int theData1,
                           final int theData2) {
        int command = ShortMessage.CONTROL_CHANGE;
        int timeStamp = -1;
        if (theMidiOutMapper.getReceiver() != null) {
            send(theMidiOutMapper.getReceiver(), command, theChannel, theData1, theData2, timeStamp);
        }
        return this;
    }

    public MidiMapper send(final Receiver theReceiver,
                           final int theCommand,
                           final int theChannel,
                           final int theData1,
                           final int theData2,
                           final int theTimeStamp) {
        ShortMessage msg = new ShortMessage();
        try {
            msg.setMessage(theCommand, theChannel, theData1, theData2);
            theReceiver.send(msg, theTimeStamp);
        } catch (InvalidMidiDataException | NullPointerException e) {
            System.err.println(e.getMessage());
        }
        return this;
    }

    static public ArrayList list() {
        return find("");
    }

    static public ArrayList find(final String thePattern) {
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        ArrayList found = new ArrayList();
        for (int i = 0; i < info.length; i++) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(info[i]);
                boolean in = (device.getMaxTransmitters() != 0);
                boolean out = (device.getMaxReceivers() != 0);
                String pattern = "(?i)^.*?(" + thePattern + ").*$";
                boolean a = info[i].getName().matches(pattern);
                boolean b = info[i].getVendor().matches(pattern);
                boolean c = info[i].getDescription().matches(pattern);
                if (thePattern.length() == 0 || (a || b || c)) {
                    found.add(toMap(k_id, i, k_name, info[i].getName(), k_in, in, k_out, out, k_vendor, info[i].getVendor(), k_version, info[i].getVersion(), "description", info[i].getDescription()));
                }
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }
        return found;
    }

    public boolean exists(final String theDevice) {
        return find(theDevice).isEmpty() ? false : true;
    }

    public boolean exists(final List theList) {
        for (Object o : theList) {
            if (o instanceof Number) {
                int id = ((Number) o).intValue();
                if (!find(getNameFromId(id)).isEmpty()) {
                    return true;
                }
            } else if (o instanceof String) {
                if (!find(o.toString()).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * @param theInput
     * @param theOutput
     * @see <a href="http://www.jsresources.org/examples/MidiCommon.java.html">MidiCommon.java</a>
     */
    static public void listDevicesAndExit(final boolean theInput, final boolean theOutput) {
        listDevicesAndExit(theInput, theOutput, false);
    }

    /**
     * @param theInput
     * @param theOutput
     * @param theVerbose
     * @see <a href="http://www.jsresources.org/examples/MidiCommon.java.html">MidiCommon.java</a>
     */
    static public void listDevicesAndExit(final boolean theInput, final boolean theOutput, final boolean theVerbose) {
        if (theInput && !theOutput) {
            println("Available MIDI IN Devices:");
        } else if (!theInput && theOutput) {
            println("Available MIDI OUT Devices:");
        } else {
            println("Available MIDI Devices:");
        }

        MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < aInfos.length; i++) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
                boolean allowsInput = (device.getMaxTransmitters() != 0);
                boolean allowsOutput = (device.getMaxReceivers() != 0);
                if ((allowsInput && theInput) || (allowsOutput && theOutput)) {
                    if (theVerbose) {
                        println("" + i + "  " + (allowsInput ? "IN " : "   ") + (allowsOutput ? "OUT " : "    ") + aInfos[i].getName() + ", " + aInfos[i].getVendor() + ", " + aInfos[i].getVersion() + ", " + aInfos[i].getDescription());
                    } else {
                        println("" + i + "  " + aInfos[i].getName());
                    }
                }
            } catch (MidiUnavailableException e) {
            }
        }
        if (aInfos.length == 0) {
            println("[No devices available]");
        }
    }

    /**
     * Retrieve a MidiDevice.Info for a given name.
     * <p>
     * This method tries to return a MidiDevice.Info whose name
     * matches the passed name. If no matching MidiDevice.Info is
     * found, null is returned. If bForOutput is true, then only
     * output devices are searched, otherwise only input devices.
     * <p>
     *
     * @param theDeviceName the name of the device for which an info
     *                      object should be retrieved.
     * @param theOutput     If true, only output devices are
     *                      considered. If false, only input devices are considered.
     * @return A MidiDevice.Info object matching the passed device
     * name or null if none could be found.
     * @see <a href="http://www.jsresources.org/examples/MidiCommon.java.html">MidiCommon.java</a>
     */
    public static MidiDevice.Info getMidiDeviceInfo(final String theDeviceName, final boolean theOutput) {
        MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < aInfos.length; i++) {
            if (aInfos[i].getName().equals(theDeviceName)) {
                try {
                    MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
                    boolean allowsInput = (device.getMaxTransmitters() != 0);
                    boolean allowsOutput = (device.getMaxReceivers() != 0);
                    if ((allowsOutput && theOutput) || (allowsInput && !theOutput)) {
                        return aInfos[i];
                    }
                } catch (MidiUnavailableException e) {
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
     * <p>
     *
     * @param theIndex the index of the device to be retrieved
     * @return A MidiDevice.Info object of the specified index
     * or null if none could be found.
     * @see <a href="http://www.jsresources.org/examples/MidiCommon.java.html">MidiCommon.java</a>
     */
    public static MidiDevice.Info getMidiDeviceInfo(final int theIndex) {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        if ((theIndex < 0) || (theIndex >= infos.length)) {
            return null;
        }
        return infos[theIndex];
    }


    public AssignedDevice load(final String theFilename) {
        Object o = json(parent.join(parent.loadStrings(theFilename), "\n"));
        try {
            if (o instanceof List) {
                List l = (List) o;
                for (Object o1 : l) {
                    if (o1 instanceof Map) {
                        Map o2 = ((Map) o1);
                        String device = s(o2.get(k_device));
                        Map mapping = (Map) o2.get(k_mapping);
                        AssignedDevice assigned = connect(device, new Proxy());
                        for (Object k : mapping.keySet()) {
                            int note = i(mapping.get(k));
                            String member = s(k);
                            assigned.assign(note).to(member);
                        }
                        return assigned;
                    }
                }
            }
        } catch (Exception e) {
            println("Failed to load JSON", theFilename, "please check that the JSON formatting is correct.");
        }
        return null;
    }

    public MidiMapper save() {
        return this;
    }

    public MidiMapper saveAs(final String thePath) {

        return this;
    }


    private class Proxy implements Receiver {


        public Proxy() {
        }

        public void send(final MidiMessage msg, final long timeStamp) {
        }

        public void close() {
        }

    }


    private class TestReceiver implements Receiver {

        private final String name;
        private Map<Byte, String> commandMap = new LinkedHashMap<>();

        public TestReceiver(String theName) {
            name = theName;
            commandMap.put((byte) -112, "Note On");
            commandMap.put((byte) -128, "Note Off");
            commandMap.put((byte) -48, "Channel Pressure");
            commandMap.put((byte) -80, "Continuous Controller");
        }

        public void send(final MidiMessage msg, final long timeStamp) {

            byte[] b = msg.getMessage();
            Map result = new LinkedHashMap();

            result.put(k_name, name);
            result.put(k_type, commandMap.get(b[0]));
            result.put(k_status, msg.getStatus());

            if (b[0] != -48) {
                result.put(k_note, b[1]);
                result.put(k_pressure, b[2]);
            } else {
                result.put(k_pressure, b[1]);
            }
            result.put(k_timestamp, timeStamp);

            System.out.println(result.toString());
        }

        public void close() {
        }

    }


}
