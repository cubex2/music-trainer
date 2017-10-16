package cubex2.musictrainer;

import java.util.ArrayList;
import java.util.List;

public class SoundGenerator
{
    private static final float RAMP_DURATION = 0.05f; // seconds

    private final int sampleRate;

    private double[] sample;
    private byte[] sound;

    private int sampleIdx = 0;
    private int soundIdx = 0;

    private List<Tone> tones = new ArrayList<>();

    public SoundGenerator(int sampleRate)
    {
        this.sampleRate = sampleRate;
    }

    public SoundGenerator addTone(int frequency, float duration)
    {
        tones.add(new Tone(frequency, duration));
        return this;
    }

    public byte[] generate()
    {
        int numSamples = calculateNumSamples();
        sample = new double[numSamples];
        sound = new byte[2 * numSamples];

        sampleIdx = 0;
        soundIdx = 0;

        for (Tone tone : tones)
        {
            generateTone(tone.frequency, tone.duration);
        }

        return sound;
    }

    private int calculateNumSamples()
    {
        int samples = 0;

        for (Tone tone : tones)
        {
            samples += (int) (tone.duration * sampleRate);
        }

        return samples + (int)(sampleRate * RAMP_DURATION);
    }

    private void generateTone(int frequency, float duration)
    {
        final int samples = (int) (duration * sampleRate);
        final int firstSample = sampleIdx;

        for (int i = 0; i < samples; i++)
        {
            sample[sampleIdx] = Math.sin((2 * Math.PI - 0.001) * sampleIdx / (sampleRate / frequency));
            sampleIdx++;
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int ramp = (int) (sampleRate * RAMP_DURATION);

        for (int i = 0; i < ramp; i++)
        {
            int idx = firstSample + i;
            // scale to maximum amplitude
            final short val = (short) ((sample[idx] * 32767) * i / ramp);
            // in 16 bit wav PCM, first byte is the low order byte
            sound[soundIdx++] = (byte) (val & 0x00ff);
            sound[soundIdx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (int i = ramp; i < samples - ramp; i++)
        {
            int idx = firstSample + i;
            // scale to maximum amplitude
            final short val = (short) ((sample[idx] * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            sound[soundIdx++] = (byte) (val & 0x00ff);
            sound[soundIdx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (int i = samples - ramp; i < samples; i++)
        {
            int idx = firstSample + i;
            // scale to maximum amplitude
            final short val = (short) ((sample[idx] * 32767) * (samples - i) / ramp);
            // in 16 bit wav PCM, first byte is the low order byte
            sound[soundIdx++] = (byte) (val & 0x00ff);
            sound[soundIdx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    private static class Tone
    {
        final int frequency;
        final float duration;

        Tone(int frequency, float duration)
        {
            this.frequency = frequency;
            this.duration = duration;
        }
    }
}
