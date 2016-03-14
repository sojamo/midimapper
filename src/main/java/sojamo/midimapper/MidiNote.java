package sojamo.midimapper;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiNote {

    private final AssignedDevice device;
    private final int id;
    private final Object parent;
    private final String delimiter = "/";
    private final static Logger log = Logger.getLogger(MidiNote.class.getName());
    private float min = 0;
    private float max = 127;
    private String member = "?";

    public MidiNote(final AssignedDevice theDevice, final Object theParent, final int theId) {
        device = theDevice;
        parent = theParent;
        id = theId;
        log.setLevel(Level.WARNING);
    }

    public AssignedDevice to(final Object theTarget, final String theMember) {
        member = theMember;
        return to(new MidiReceiver(this, theTarget, member));
    }

    public AssignedDevice to(final String theMember) {
        member = theMember;
        return evaluatePath(member);
    }

    public AssignedDevice to(final String theMember, final float theMin, final float theMax) {
        member = theMember;
        setRange(theMin, theMax);
        return evaluatePath(member);
    }

    public AssignedDevice to(final MidiReceiver theReceiver) {
        member = theReceiver.toString();
        try {
            Transmitter conTrans;
            conTrans = device.get().getTransmitter();
            conTrans.setReceiver(theReceiver);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        return device;
    }

    private AssignedDevice evaluatePath(final String thePath) {

        LinkedList<String> path = new LinkedList(Arrays.asList(thePath.split(delimiter)));
        if (path.size() < 2) {
            return to(parent, thePath);
        }

        Object member = parent;

        final String invoke = path.pollLast();
        /* TODO check if invoke is numeric, if so, we should be operating on a list */

        for (String s : path) {
            Object o = evaluateMember(member, s);
            if (o instanceof Field) {
                log.info("Field:" + ((Field) o).getType());
                try {
                    member = ((Field) o).get(member);
                    log.info("field-member:" + member);
                } catch (IllegalArgumentException e) {
                    log.warning(e.getMessage());
                } catch (IllegalAccessException e) {
                    log.warning(e.getMessage());
                }
            } else if (o instanceof Method) {
                log.info("Method:" + ((Method) o).getReturnType());
            } else {
                if (member instanceof List) {
                    int index = Common.i(s, -1);
					/* TODO check for valid index, list not empty, IndexOutOfBoundsException, nested list */
                    List l = ((List) member);
                    member = l.get(index);
                } else if (member instanceof Map) {
					/* TODO check if this is ever reached. */
                    member = ((Map) member).get(s);

                } else {
					/* Something else */
                    log.info(s + " " + member.getClass());
                }
            }
        }
        return to(new MidiReceiver(this, member, invoke));
    }


    private final Object evaluateMember(final Object theObject, final String theName) {
        Class<?> c = theObject.getClass();
        while (c != null) {
            try {
                final Field field = c.getDeclaredField(theName);
                field.setAccessible(true);
                return field;
            } catch (Exception e) {
                try {
                    final Method method = c.getMethod(theName, new Class<?>[]{});
                    return method;
                } catch (SecurityException | NoSuchMethodException e1) {
                    log.info(e.getMessage());
                }
            }
            c = c.getSuperclass();
        }
        return null;
    }

    public MidiNote setRange(final float theMin, final float theMax) {
        min = theMin;
        max = theMax;
        return this;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public int getId() {
        return id;
    }


    public String toString() {
        StringBuilder b = new StringBuilder().
                append("MidiNote{").
                append(" id=").append(getId()).
                append(", min=").append(getMin()).
                append(", max=").append(getMax()).
                append(", member=").append(member).
                append(" }");
        return b.toString();
    }
}
