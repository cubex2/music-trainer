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

    protected static ToneSequence fromOffsets(Tone startTone, int[] semiToneOffsets)
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

        return new ToneSequence(tones);
    }
}
