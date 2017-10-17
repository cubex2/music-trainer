package cubex2.musictrainer;

import java.util.HashMap;
import java.util.Map;

public class Tone
{
    private static final int TONES_PER_OCTAVE = 12;
    private static final Map<Integer, Tone> tones = new HashMap<>();

    public static Tone forKeyNumber(int keyNumber)
    {
        Tone tone = tones.get(keyNumber);
        if (tone == null)
        {
            tone = new Tone(keyNumber);
            tones.put(keyNumber, tone);
        }

        return tone;
    }

    private final int keyNumber;
    private final double frequency;
    private final String keyName;

    private Tone(int keyNumber)
    {
        this.keyNumber = keyNumber;
        this.frequency = pianoFrequency(keyNumber);
        this.keyName = keyName(keyNumber);
    }

    public int getKeyNumber()
    {
        return keyNumber;
    }

    public double getFrequency()
    {
        return frequency;
    }

    public String getName()
    {
        return keyName;
    }

    static double pianoFrequency(int keyNumber)
    {
        return Math.pow(2, (keyNumber - 49) / (double) TONES_PER_OCTAVE) * 440;
    }

    static String keyName(int keyNumber)
    {
        final String[] names = new String[]
                {
                        "c", "cis/des", "d", "dis/es", "e", "f", "fis/ges", "g", "gis/as", "a", "ais/b", "h",
                };

        int octave = octave(keyNumber);
        int octaveOffset = octaveOffset(keyNumber);

        String name = names[octaveOffset];

        boolean uppercase = octave <= 2;
        int number = numberForOctave(octave);

        if (uppercase)
        {
            name = makeUppercase(name);
        }

        if (number > 0)
        {
            name = insertNumber(name, number);
        }

        return name;
    }

    static String insertNumber(String name, int number)
    {
        String[] parts = name.split("/");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++)
        {
            String part = parts[i];

            if (i > 0)
                sb.append('/');

            sb.append(part);
            sb.append(number);
        }

        return sb.toString();
    }

    static String makeUppercase(String name)
    {
        String[] parts = name.split("/");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++)
        {
            String part = parts[i];

            if (i > 0)
                sb.append('/');

            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1));
        }

        return sb.toString();
    }

    static int numberForOctave(int octave)
    {
        return Math.min(Math.abs(2 - octave), Math.abs(3 - octave));
    }

    static int octave(int keyNumber)
    {
        return (keyNumber + 8) / TONES_PER_OCTAVE;
    }

    static int octaveOffset(int keyNumber)
    {
        return (keyNumber + 8) % TONES_PER_OCTAVE;
    }
}
