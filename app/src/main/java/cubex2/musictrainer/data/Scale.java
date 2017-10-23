package cubex2.musictrainer.data;

public class Scale extends ToneSequence
{
    private static final int[] MAJOR_OFFSETS = {2, 2, 1, 2, 2, 2, 1};
    private static final int[] MINOR_OFFSETS = {2, 1, 2, 2, 1, 2, 2};

    private Scale(Iterable<Tone> tones)
    {
        super(tones);
    }

    public static ToneSequence major(Tone startTone)
    {
        return fromOffsets(startTone, MAJOR_OFFSETS);
    }

    public static ToneSequence minor(Tone startTone)
    {
        return fromOffsets(startTone, MINOR_OFFSETS);
    }
}
