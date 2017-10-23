package cubex2.musictrainer.data;

import cubex2.musictrainer.SoundGenerator;
import cubex2.musictrainer.Util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Quiz
{
    private static final float TONE_DURATION = 0.5f;
    private static final float MIN_DURATION_ERROR = 0.1f;
    private static final float MAX_DURATION_ERROR = 0.25f;
    private static final int MIN_FREQUENCY_ERROR = 1;
    private static final int MAX_FREQUENCY_ERROR = 2;

    private final boolean useDurationErrors;
    private final boolean useFrequencyErrors;
    private final Set<Integer> errorIndices;
    private Tone[] tones;
    private float[] durations;

    public Quiz(ToneSequence sequence, int maxErrors, boolean useDurationErrors, boolean useFrequencyErrors)
    {
        this.useDurationErrors = useDurationErrors;
        this.useFrequencyErrors = useFrequencyErrors;
        int numErrors = Util.randomInRange(1, maxErrors);

        errorIndices = computeErrorIndices(numErrors, 0, sequence.getTones().size() - 1);

        initErrors(sequence);
    }

    private void initErrors(ToneSequence sequence)
    {
        int numTones = sequence.getTones().size();
        tones = sequence.getTones().toArray(new Tone[numTones]);
        durations = new float[numTones];

        Arrays.fill(durations, TONE_DURATION);

        applyErrors(errorIndices);
    }

    public int getNumTones()
    {
        return tones.length;
    }

    public void addTones(SoundGenerator generator)
    {
        for (int i = 0; i < tones.length; i++)
        {
            Tone tone = tones[i];
            generator.addTone(tone.getFrequency(), durations[i]);
        }
    }

    /**
     * Checks if the given tones are the ones that should have been selected. Returns the indices of the tones that are
     * selected wrongly or are not selected when they should have.
     */
    public Report createReport(Set<Integer> selectedTones)
    {
        Set<Integer> incorrectlySelected = new HashSet<>();
        Set<Integer> incorrectlyNotSelected = new HashSet<>();

        for (Integer tone : selectedTones)
        {
            if (!errorIndices.contains(tone))
                incorrectlySelected.add(tone);
        }

        for (Integer index : errorIndices)
        {
            if (!selectedTones.contains(index))
                incorrectlyNotSelected.add(index);
        }

        return new Report(Util.toSortedArray(incorrectlySelected),
                          Util.toSortedArray(incorrectlyNotSelected),
                          Util.toSortedArray(errorIndices));
    }

    private void applyErrors(Set<Integer> errorIndices)
    {
        for (Integer index : errorIndices)
        {
            if (useDurationErrors && (!useFrequencyErrors || Util.randomBoolean()))
            {
                float error = Util.randomSign() * Util.randomInRange(MIN_DURATION_ERROR, MAX_DURATION_ERROR);
                durations[index] += error;
            } else
            {
                int error = Util.randomSign() * Util.randomInRange(MIN_FREQUENCY_ERROR, MAX_FREQUENCY_ERROR);
                tones[index] = Tone.forKeyNumber(tones[index].getKeyNumber() + error);
            }
        }
    }

    private Set<Integer> computeErrorIndices(int num, int minIndex, int maxIndex)
    {
        Set<Integer> indices = new HashSet<>();

        while (indices.size() < num)
        {
            indices.add(Util.randomInRange(minIndex, maxIndex));
        }

        return indices;
    }

    public static class Report
    {
        public final int[] incorrectlySelected;
        public final int[] incorrectlyNotSelected;
        public final int[] allErrors;

        public Report(int[] incorrectlySelected, int[] incorrectlyNotSelected, int[] allErrors)
        {
            this.incorrectlySelected = incorrectlySelected;
            this.incorrectlyNotSelected = incorrectlyNotSelected;
            this.allErrors = allErrors;
        }

        public boolean hasMistakes()
        {
            return incorrectlySelected.length > 0 || incorrectlyNotSelected.length > 0;
        }
    }
}
