package sojamo.midimapper;

import javax.sound.midi.MidiDevice;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssignedDevice {

    private final MidiDevice device;
    private final Object parent;
    private final String name;
    private final Map<Integer, MidiNote> map;

    AssignedDevice(Object theParent, MidiDevice theDevice, String theName) {
        parent = theParent;
        device = theDevice;
        name = theName;
        map = new LinkedHashMap<>();
    }

    public MidiNote assign(int theNote) {
        MidiNote note = new MidiNote(this, parent, theNote);
        map.put(theNote, note);
        return note;
    }

    public AssignedDevice remove(int theNote) {
        map.remove(theNote);
        return this;
    }

    public AssignedDevice clear() {
        map.clear();
        return this;
    }

    public MidiDevice get() {
        return device;
    }

    public boolean exists() {
        return !device.equals(null);
    }

    public String getName() {
        return name;
    }

    public Map getMapping() {
        return Collections.unmodifiableMap(map);
    }

    public String toString() {
        StringBuilder b = new StringBuilder().
                append("AssignedDevice{").
                append(" name=").append(name).
                append(", device=").append(device.getDeviceInfo().getName()).
                append(", mapping=").append(getMapping().toString()).
                append(" }");
        return b.toString();
    }


}
