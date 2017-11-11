package cubex2.musictrainer.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ToneSequence
{
    private final List<Tone> tones = new ArrayList<>();

    public ToneSequence(Iterable<Tone> tones)
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

    protected static ToneSequence fromOffsets(Tone startTone, int numTones, int[] semiToneOffsets)
    {
        List<Tone> tones = new LinkedList<>();

        tones.add(startTone);
        Tone prevTone = startTone;
        for (int i = 0; i < numTones-1; i++)
        {
            Tone tone = Tone.forKeyNumber(prevTone.getKeyNumber() + semiToneOffsets[i]);
            tones.add(tone);

            prevTone = tone;
        }

        return new ToneSequence(tones);
    }
}
