package cubex2.musictrainer.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToneTest
{
    @Test
    public void pianoFrequency() throws Exception
    {
        assertEquals(440D, Tone.pianoFrequency(49), 0.01D);
        assertEquals(1479.98D, Tone.pianoFrequency(70), 0.01D);
        assertEquals(27.5D, Tone.pianoFrequency(1), 0.01D);
    }

    @Test
    public void ocatve() throws Exception
    {
        assertEquals(0, Tone.octave(-8));
        assertEquals(0, Tone.octave(3));
        assertEquals(1, Tone.octave(4));
        assertEquals(1, Tone.octave(15));
        assertEquals(7, Tone.octave(87));
        assertEquals(8, Tone.octave(88));
    }

    @Test
    public void numberForOcatve() throws Exception
    {
        assertEquals(2, Tone.numberForOctave(0));
        assertEquals(2, Tone.numberForOctave(5));
        assertEquals(0, Tone.numberForOctave(3));
        assertEquals(5, Tone.numberForOctave(8));
    }

    @Test
    public void ocatveOffset() throws Exception
    {
        assertEquals(0, Tone.octaveOffset(-8));
        assertEquals(11, Tone.octaveOffset(3));
        assertEquals(0, Tone.octaveOffset(4));
        assertEquals(3, Tone.octaveOffset(79));
    }

    @Test
    public void makeUppercase() throws Exception
    {
        assertEquals("A", Tone.makeUppercase("a"));
        assertEquals("Cis/Des", Tone.makeUppercase("cis/des"));
    }

    @Test
    public void insertNumber() throws Exception
    {
        assertEquals("a1", Tone.insertNumber("a", 1));
        assertEquals("cis2/des2", Tone.insertNumber("cis/des", 2));
    }

    @Test
    public void keyName() throws Exception
    {
        assertEquals("C2", Tone.keyName(-8));
        assertEquals("Gis2/As2", Tone.keyName(0));
        assertEquals("c1", Tone.keyName(40));
        assertEquals("ais4/b4", Tone.keyName(86));
        assertEquals("H", Tone.keyName(27));
    }

    @Test
    public void fileName() throws Exception
    {
        assertEquals("c0", Tone.fileName(-8));
        assertEquals("ab0", Tone.fileName(0));
        assertEquals("c4", Tone.fileName(40));
        assertEquals("bb7", Tone.fileName(86));
        assertEquals("b2", Tone.fileName(27));
    }

    @Test
    public void noteLine() throws Exception
    {
        assertEquals(0, Tone.toneLine(1));
        assertEquals(1, Tone.toneLine(2));
        assertEquals(1, Tone.toneLine(3));
        assertEquals(23, Tone.toneLine(40));
        assertEquals(24, Tone.toneLine(41));
    }
}