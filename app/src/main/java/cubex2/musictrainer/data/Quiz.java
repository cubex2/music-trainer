package cubex2.musictrainer.data;

import android.util.Pair;
import cubex2.musictrainer.Util;

import java.util.*;

public class Quiz
{
    private static final float TONE_VOLUME = 1f;

    public final Difficulty difficulty;
    private final Map<Integer, Pair<ErrorType, Integer>> errors;
    private final List<ErrorType> activeErrors;
    private final ToneSequence sequence;
    private PlayableTone[] tones;

    public Quiz(ToneSequence sequence, Difficulty difficulty)
    {
        this.sequence = sequence;
        this.difficulty = difficulty;
        this.activeErrors = difficulty.getErrorTypes();
        int numErrors = Util.randomInRange(1, difficulty.getMaxErrors());

        errors = computeErrors(numErrors, 0, sequence.getTones().size() - 1);

        initErrors(sequence, difficulty.getToneDuration());
    }

    private void initErrors(ToneSequence sequence, float toneDuration)
    {
        int numTones = sequence.getTones().size();
        tones = new PlayableTone[numTones];
        for (int i = 0; i < tones.length; i++)
        {
            tones[i] = new PlayableTone(sequence.getTones().get(i), toneDuration, TONE_VOLUME);
        }

        applyErrors(errors);
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
            if (!errors.containsKey(tone))
                incorrectlySelected.add(tone);
        }

        for (Integer index : errors.keySet())
        {
            if (!selectedTones.contains(index))
                incorrectlyNotSelected.add(index);
        }

        return new Report(incorrectlySelected,
                          incorrectlyNotSelected,
                          errors);
    }

    private void applyErrors(Map<Integer, Pair<ErrorType, Integer>> errorIndices)
    {
        for (Map.Entry<Integer, Pair<ErrorType, Integer>> entry : errorIndices.entrySet())
        {
            int index = entry.getKey();
            ErrorType error = entry.getValue().first;
            int sign = entry.getValue().second;

            PlayableTone tone = tones[index];
            error.apply(sign, difficulty, tone);
        }
    }

    private Map<Integer, Pair<ErrorType, Integer>> computeErrors(int num, int minIndex, int maxIndex)
    {
        Map<Integer, Pair<ErrorType, Integer>> indices = new HashMap<>();

        while (indices.size() < num)
        {
            int index = Util.randomInRange(minIndex, maxIndex);
            ErrorType error = Util.randomElement(activeErrors);
            int sign = error == ErrorType.VOLUME ? 1 : Util.randomSign();
            indices.put(index, Pair.create(error, sign));
        }

        return indices;
    }

    private Map<Integer, Integer> computeErrorSigns()
    {
        Map<Integer, Integer> signs = new HashMap<>();

        for (Integer index : errors.keySet())
        {
            signs.put(index, Util.randomSign());
        }

        return signs;
    }

    public static Quiz fromDifficulty(Difficulty difficulty, int minTone, int maxTone)
    {
        ToneSequence sequence;
        int numTones = difficulty.getNumTones();
        boolean scales = difficulty.useScales();
        boolean arpeggios = difficulty.useArpeggios();

        int startTone = Util.randomInRange(minTone, maxTone);

        if (scales && (!arpeggios || Util.randomBoolean()))
        {
            sequence = Scale.random(Tone.forKeyNumber(startTone), numTones);
        } else
        {
            sequence = Arpeggio.random(Tone.forKeyNumber(startTone), numTones);
        }

        return new Quiz(sequence, difficulty);
    }

    public static class Report
    {
        public final Map<Integer, Pair<ErrorType, Integer>> allErrors;
        public final Set<Integer> errors = new HashSet<>();

        public Report(Set<Integer> incorrectlySelected, Set<Integer> incorrectlyNotSelected, Map<Integer, Pair<ErrorType, Integer>> errors)
        {
            this.errors.addAll(incorrectlySelected);
            this.errors.addAll(incorrectlyNotSelected);
            this.allErrors = Collections.unmodifiableMap(errors);
        }

        public boolean hasMistakes()
        {
            return errors.size() > 0;
        }

        public boolean hasMistake(ErrorType errorType)
        {
            for (Integer index : errors)
            {
                if (allErrors.containsKey(index) && allErrors.get(index).first == errorType)
                    return true;
            }

            return false;
        }

        public boolean hasError(ErrorType errorType)
        {
            for (Pair<ErrorType, Integer> error : allErrors.values())
            {
                if (error.first == errorType)
                    return true;
            }

            return false;
        }
    }
}
