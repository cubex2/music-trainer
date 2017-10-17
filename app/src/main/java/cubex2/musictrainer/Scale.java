package cubex2.musictrainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Scale
{
    private static final int[] MAJOR_OFFSETS = {2, 2, 1, 2, 2, 2, 1};
    private static final int[] MINOR_OFFSETS = {2, 1, 2, 2, 1, 2, 2};

    private final List<Tone> tones = new ArrayList<>();

    private Scale(Iterable<Tone> tones)
    {
        for (Tone tone : tones)
        {
            this.tones.add(tone);
        }
    }

    public List<Tone> getTones()
    {
        return Collections.unmodifiableList(tones);
    }

    private static Scale fromOffsets(Tone startTone, int[] semiToneOffsets)
    {
        List<Tone> tones = new LinkedList<>();

        tones.add(startTone);
        Tone prevTone = startTone;
        for (int toneOffset : semiToneOffsets)
        {
            Tone tone = Tone.forKeyNumber(prevTone.getKeyNumber() + toneOffset);
            tones.add(tone);

            prevTone = tone;
        }

        return new Scale(tones);
    }

    public static Scale major(Tone startTone)
    {
        return fromOffsets(startTone, MAJOR_OFFSETS);
    }

    public static Scale minor(Tone startTone)
    {
        return fromOffsets(startTone, MINOR_OFFSETS);
    }
}
