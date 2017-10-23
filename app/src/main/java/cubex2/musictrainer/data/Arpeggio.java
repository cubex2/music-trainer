package cubex2.musictrainer.data;

public class Arpeggio extends ToneSequence
{
    private static final int[] MAJOR_OFFSETS = {4, 3, 5, 4, 3, 5};
    private static final int[] MINOR_OFFSETS = {3, 4, 5, 3, 4, 5};

    private Arpeggio(Iterable<Tone> tones)
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
