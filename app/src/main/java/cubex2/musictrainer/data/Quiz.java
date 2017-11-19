package cubex2.musictrainer.data;

import cubex2.musictrainer.Util;

import java.util.*;

public class Quiz
{
    private static final float TONE_DURATION = 0.5f;
    private static final float TONE_VOLUME = 1f;

    private final Map<Integer, ErrorType> errorIndices;
    private final List<ErrorType> activeErrors;
    private final ToneSequence sequence;
    private PlayableTone[] tones;

    public Quiz(ToneSequence sequence, int maxErrors, List<ErrorType> activeErrors)
    {
        this.sequence = sequence;
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

    public ToneSequence getSequence()
    {
        return sequence;
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
            if (!errorIndices.containsKey(tone))
                incorrectlySelected.add(tone);
        }

        for (Integer index : errorIndices.keySet())
        {
            if (!selectedTones.contains(index))
                incorrectlyNotSelected.add(index);
        }

        return new Report(incorrectlySelected,
                          incorrectlyNotSelected,
                          errorIndices);
    }

    private void applyErrors(Map<Integer, ErrorType> errorIndices)
    {
        for (Map.Entry<Integer, ErrorType> entry : errorIndices.entrySet())
        {
            int index = entry.getKey();
            ErrorType error = entry.getValue();

            PlayableTone tone = tones[index];
            PlayableTone prevTone = index == 0 ? null : tones[index - 1];
            PlayableTone nextTone = index == tones.length - 1 ? null : tones[index + 1];
            error.apply(tone, prevTone, nextTone);
        }
    }

    private Map<Integer, ErrorType> computeErrorIndices(int num, int minIndex, int maxIndex)
    {
        Map<Integer, ErrorType> indices = new HashMap<>();

        while (indices.size() < num)
        {
            int index = Util.randomInRange(minIndex, maxIndex);
            ErrorType error = Util.randomElement(activeErrors);
            indices.put(index, error);
        }

        return indices;
    }

    public static Quiz fromDifficulty(Difficulty difficulty, int minTone, int maxTone)
    {
        ToneSequence sequence;
        int maxErrors = difficulty.getMaxErrors();
        int numTones = difficulty.getNumTones();
        boolean scales = difficulty.useScales();
        boolean arpeggios = difficulty.useArpeggios();
        List<ErrorType> activeErrors = difficulty.getErrorTypes();

        int startTone = Util.randomInRange(minTone, maxTone);

        if (scales && (!arpeggios || Util.randomBoolean()))
        {
            sequence = Scale.random(Tone.forKeyNumber(startTone), numTones);
        } else
        {
            sequence = Arpeggio.random(Tone.forKeyNumber(startTone), numTones);
        }

        return new Quiz(sequence, maxErrors, activeErrors);
    }

    public static class Report
    {
        public final Map<Integer, ErrorType> allErrors;
        public final Set<Integer> errors = new HashSet<>();

        public Report(Set<Integer> incorrectlySelected, Set<Integer> incorrectlyNotSelected, Map<Integer, ErrorType> errors)
        {
            this.errors.addAll(incorrectlySelected);
            this.errors.addAll(incorrectlyNotSelected);
            this.allErrors = Collections.unmodifiableMap(errors);
        }

        public boolean hasMistakes()
        {
            return errors.size() > 0;
        }
    }
}
