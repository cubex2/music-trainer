package cubex2.musictrainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Quiz
{
    private static final float TONE_DURATION = 0.5f;
    private static final float MIN_DURATION_ERROR = 0.1f;
    private static final float MAX_DURATION_ERROR = 0.25f;

    private final ToneSequence toneSequence;
    private final Set<Integer> errorIndices;
    private final float[] durations;

    public Quiz(ToneSequence sequence, int maxErrors)
    {
        int numErrors = Util.randomInRange(1, maxErrors);

        toneSequence = sequence;
        errorIndices = computeErrorIndices(numErrors, 0, toneSequence.getTones().size() - 1);
        durations = createDurations(toneSequence.getTones().size());
    }

    public int getNumTones()
    {
        return toneSequence.getTones().size();
    }

    public void addTones(SoundGenerator generator)
    {
        int i = 0;
        for (Tone tone : toneSequence.getTones())
        {
            generator.addTone(tone.getFrequency(), durations[i++]);
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

    private float[] createDurations(int num)
    {
        float[] result = new float[num];
        Arrays.fill(result, TONE_DURATION);

        result = applyErrors(result, errorIndices);

        return result;
    }

    private float[] applyErrors(float[] durations, Set<Integer> errorIndices)
    {
        for (Integer index : errorIndices)
        {
            float error = Util.randomInRange(MIN_DURATION_ERROR, MAX_DURATION_ERROR);
            if (Util.RANDOM.nextBoolean())
                durations[index] += error;
            else
                durations[index] -= error;
        }

        return durations;
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
