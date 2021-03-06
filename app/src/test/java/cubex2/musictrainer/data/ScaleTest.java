package cubex2.musictrainer.data;

import cubex2.musictrainer.data.Scale;
import cubex2.musictrainer.data.Tone;
import cubex2.musictrainer.data.ToneSequence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScaleTest
{
    @Test
    public void major() throws Exception
    {
        ToneSequence scale = Scale.major(Tone.forKeyNumber(40),8); // c major

        List<Tone> tones = scale.getTones();

        assertEquals(8, tones.size());
        assertEquals(40, tones.get(0).getKeyNumber());
        assertEquals(42, tones.get(1).getKeyNumber());
        assertEquals(44, tones.get(2).getKeyNumber());
        assertEquals(45, tones.get(3).getKeyNumber());
        assertEquals(47, tones.get(4).getKeyNumber());
        assertEquals(49, tones.get(5).getKeyNumber());
        assertEquals(51, tones.get(6).getKeyNumber());
        assertEquals(52, tones.get(7).getKeyNumber());
    }

     @Test
    public void minor() throws Exception
    {
        ToneSequence scale = Scale.minor(Tone.forKeyNumber(40),8); // c major

        List<Tone> tones = scale.getTones();

        assertEquals(8, tones.size());
        assertEquals(40, tones.get(0).getKeyNumber());
        assertEquals(42, tones.get(1).getKeyNumber());
        assertEquals(43, tones.get(2).getKeyNumber());
        assertEquals(45, tones.get(3).getKeyNumber());
        assertEquals(47, tones.get(4).getKeyNumber());
        assertEquals(48, tones.get(5).getKeyNumber());
        assertEquals(50, tones.get(6).getKeyNumber());
        assertEquals(52, tones.get(7).getKeyNumber());
    }

}