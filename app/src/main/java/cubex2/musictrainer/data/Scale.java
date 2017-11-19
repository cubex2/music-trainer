package cubex2.musictrainer.data;

import cubex2.musictrainer.Util;

public class Scale extends ToneSequence
{
    private static final int[] MAJOR_OFFSETS = {2, 2, 1, 2, 2, 2, 1};
    private static final int[] MINOR_OFFSETS = {2, 1, 2, 2, 1, 2, 2};

    private Scale(Iterable<Tone> tones)
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
