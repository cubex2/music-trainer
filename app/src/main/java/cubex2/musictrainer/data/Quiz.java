package cubex2.musictrainer.data;

import cubex2.musictrainer.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Quiz
{
    private static final float TONE_DURATION = 0.5f;
    private static final float TONE_VOLUME = 1f;

    private final Set<Integer> errorIndices;
    private final List<ErrorType> activeErrors;
    private PlayableTone[] tones;

    public Quiz(ToneSequence sequence, int maxErrors, List<ErrorType> activeErrors)
    {
        this.activeErrors = activeErrors;
        int numErrors = Util.randomInRange(1, maxErrors);

        errorIndices = computeErrorIndices(numErrors, 0, sequence.getTones().size() - 1);

        initErrors(sequence);
    }

    private void initErrors(ToneSequence sequence)
    {
        int numTones = sequence.getTones().size();
        tones = new PlayableTone[numTones];
        for (int i = 0; i < tones.length; i++)
        {
            tones[i] = new PlayableTone(sequence.getTones().get(i), TONE_DURATION, TONE_VOLUME);
        }

        applyErrors(errorIndices);
    }

    public PlayableTone[] getTones()
    {
        return tones;
    }

    public int getNumTones()
    {
        return tones.length;
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
            PlayableTone tone = tones[index];
            PlayableTone prevTone = index == 0 ? null : tones[index - 1];
            PlayableTone nextTone = index == tones.length - 1 ? null : tones[index + 1];
            ErrorType error = Util.randomElement(activeErrors);
            error.apply(tone, prevTone, nextTone);
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
