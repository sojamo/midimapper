package sojamo.midimapper;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import static sojamo.midimapper.Common.*;

public class MidiReceiver implements Receiver {
    private final Object target;
    private final String member;
    private final MidiNote note;

    public MidiReceiver(MidiNote theMidiNote, Object theTarget, String theMember) {
        note = theMidiNote;
        target = theTarget;
        member = theMember;
    }

    @Override
    public void close() {
    }

    @Override
    public void send(MidiMessage theMessage, long timeStamp) {
        final byte[] b = theMessage.getMessage();
        if (b[1] == note.getId()) {
            invoke(target, member, mapValue(f(b[2]), 0, 127, note.getMin(), note.getMax()));
        }
    }
}
