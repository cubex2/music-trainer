package cubex2.musictrainer.data;

import cubex2.musictrainer.Util;

public class Arpeggio extends ToneSequence
{
    private static final int[] MAJOR_OFFSETS = {4, 3, 5, 4, 3, 5, 4};
    private static final int[] MINOR_OFFSETS = {3, 4, 5, 3, 4, 5, 3};

    private Arpeggio(Iterable<Tone> tones)
    {
        super(tones);
    }

    public static ToneSequence major(Tone startTone, int numTones)
    {
        return fromOffsets(startTone, numTones, MAJOR_OFFSETS);
    }

    public static ToneSequence minor(Tone startTone, int numTones)
    {
        return fromOffsets(startTone, numTones, MINOR_OFFSETS);
    }

    public static ToneSequence random(Tone startTone, int numTones)
    {
        if (Util.randomBoolean())
            return major(startTone, numTones);
        else
            return minor(startTone, numTones);
    }
}
