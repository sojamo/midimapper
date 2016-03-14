package sojamo.midimapper;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiOutMapper {

    private Receiver receiver;
    private final Map<String, MidiData> map = new HashMap<>();
    ;

    public MidiOutMapper() {
        try {
            receiver = MidiSystem.getReceiver();
        } catch (MidiUnavailableException e) {
            System.err.println("Midi Device unavailable." + e);
        }
    }

    static void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (Exception e) {

        }
    }

    public MidiOutMapper(String theName) {
        /* some odd behavior happening here, this works from time to time, use at your own
		 * risk, confusion and frustration. it is recommended to call constructor MidiOutMapper()
		 * to assign the midi output to the default MidiOut port defined by the MidiSystem.
		 * 
		 * Update: no that we open the port, double check if the above still applies or if we are good.
		 * also see https://github.com/rngtng/launchpad/blob/master/src/com/rngtng/launchpad/LMidiCodes.java
		 * for launchpad i/o */

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info : infos) {
            try {
                if (MidiSystem.getMidiDevice(info).toString().contains("MidiOutDevice")) {
                    if (info.getName().equalsIgnoreCase(theName)) {
                        MidiSystem.getMidiDevice(info).open();
                        receiver = MidiSystem.getMidiDevice(info).getReceiver();
                        System.out.println("opening receiver, " + info.getName() + " / " + info.getDescription());
                        break;
                    }
                }
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    static public List list() {
        List<MidiDevice.Info> list = new ArrayList<MidiDevice.Info>();
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            list.add(info);
        }
        return list;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public boolean isAvailable() {
        return receiver != null;
    }

    public MidiOutMapper send(int theCommand, int theChannel, int theData1, int theData2) {
        return send(theCommand, theChannel, theData1, theData2);
    }

    public MidiOutMapper send(int theCommand, int theChannel, int theData1, int theData2, int theTimeStamp) {
        ShortMessage msg = new ShortMessage();
        try {
            msg.setMessage(theCommand, theChannel, theData1, theData2);
            getReceiver().send(msg, theTimeStamp);
        } catch (InvalidMidiDataException | NullPointerException e) {
            System.err.println(e.getMessage());
        }
        return this;
    }

    public MidiOutMapper noteOn(int theChannel, int theData1, int theData2) {
        return send(ShortMessage.NOTE_ON, theChannel, theData1, theData2, 0);
    }

    public MidiOutMapper noteOff(int theChannel, int theData1, int theData2) {
        return send(ShortMessage.NOTE_OFF, theChannel, theData1, theData2, 0);
    }

    public MidiOutMapper send(int theChannel, int theData1, int theData2) {
        return send(ShortMessage.CONTROL_CHANGE, theChannel, theData1, theData2, 0);
    }

    public MidiOutMapper trigger(String theIndex) {
        send(theIndex, 0, 0);
        return this;
    }

    public MidiOutMapper send(String theIndex, int theValue) {
        send(theIndex, theValue, 0);
        return this;
    }

    public MidiOutMapper send(ShortMessage m) {
        receiver.send(m, 0);
        return this;
    }

    public MidiOutMapper send(String theIndex, int theValue, int theTimeStamp) {
        MidiData data = map.get(theIndex);
        if (data != null) {
            data.setData2(theValue);
            send(data.getCommand(), data.getChannel(), data.getData1(), data.getData2(), data.getTimeStamp());
        }
        return this;
    }

    public MidiOutMapper assign(String theIndex, MidiData theData) {
        map.put(theIndex, theData);
        return this;
    }

    public MidiOutMapper assign(String theIndex, int theChannel, int theData1) {
        MidiData data = new MidiData().setChannel(theChannel).setData1(theData1);
        map.put(theIndex, data);
        return this;
    }

    public static void main(String... args) {
        MidiOutMapper out = new MidiOutMapper("Launchpad Mini");

    }
}
