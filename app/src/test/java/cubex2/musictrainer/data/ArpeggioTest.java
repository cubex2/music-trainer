package cubex2.musictrainer.data;

import cubex2.musictrainer.data.Arpeggio;
import cubex2.musictrainer.data.Tone;
import cubex2.musictrainer.data.ToneSequence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArpeggioTest
{
    @Test
    public void major() throws Exception
    {
        ToneSequence scale = Arpeggio.major(Tone.forKeyNumber(40)); // c major

        List<Tone> tones = scale.getTones();

        assertEquals(7, tones.size());
        assertEquals(40, tones.get(0).getKeyNumber());
        assertEquals(44, tones.get(1).getKeyNumber());
        assertEquals(47, tones.get(2).getKeyNumber());
        assertEquals(52, tones.get(3).getKeyNumber());
        assertEquals(56, tones.get(4).getKeyNumber());
        assertEquals(59, tones.get(5).getKeyNumber());
        assertEquals(64, tones.get(6).getKeyNumber());
    }

    @Test
    public void minor() throws Exception
    {
        ToneSequence scale = Arpeggio.minor(Tone.forKeyNumber(40)); // c minor

        List<Tone> tones = scale.getTones();

        assertEquals(7, tones.size());
        assertEquals(40, tones.get(0).getKeyNumber());
        assertEquals(43, tones.get(1).getKeyNumber());
        assertEquals(47, tones.get(2).getKeyNumber());
        assertEquals(52, tones.get(3).getKeyNumber());
        assertEquals(55, tones.get(4).getKeyNumber());
        assertEquals(59, tones.get(5).getKeyNumber());
        assertEquals(64, tones.get(6).getKeyNumber());
    }
}