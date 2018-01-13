package cubex2.musictrainer.data;

import android.support.v4.math.MathUtils;
import cubex2.musictrainer.stats.StatEntry;

import java.util.List;

public class DynamicDifficultyHelper
{
    private static final float DURATION_ERROR_MIN = 0.05f;
    public static final float DURATION_ERROR_MAX = 0.40f;
    private static final float DURATION_ERROR_STEP = 0.05f;

    private static final float VOLUME_ERROR_MIN = 0.25f;
    public static final float VOLUME_ERROR_MAX = 0.50f;
    private static final float VOLUME_ERROR_STEP = 0.05f;

    private static final int FREQUENCY_ERROR_MIN = 1;
    private static final int FREQUENCY_ERROR_MAX = 2;
    private static final int FREQUENCY_ERROR_STEP = 1;

    public static float normalizeDurationError(float error)
    {
        return 1f - (error - DURATION_ERROR_MIN) / (DURATION_ERROR_MAX - DURATION_ERROR_MIN);
    }

    public static float normalizeVolumeError(float error)
    {
        return 1f - (error - VOLUME_ERROR_MIN) / (VOLUME_ERROR_MAX - VOLUME_ERROR_MIN);
    }

    public static float computeNewDurationError(float current, List<StatEntry> entries)
    {
        int change = computeDifficultyChange(entries, ErrorType.DURATION);

        return (float) MathUtils.clamp(current + change * DURATION_ERROR_STEP, DURATION_ERROR_MIN, DURATION_ERROR_MAX);
    }

    public static float computeNewVolumeError(float current, List<StatEntry> entries)
    {
        int change = computeDifficultyChange(entries, ErrorType.VOLUME);

        return (float) MathUtils.clamp(current + change * VOLUME_ERROR_STEP, VOLUME_ERROR_MIN, VOLUME_ERROR_MAX);
    }

    public static int computeNewFrequencyError(int current, List<StatEntry> entries)
    {
        int change = computeDifficultyChange(entries, ErrorType.DURATION);

        return MathUtils.clamp(current + change * FREQUENCY_ERROR_STEP, FREQUENCY_ERROR_MIN, FREQUENCY_ERROR_MAX);
    }

    /**
     * Compute in what direction the difficulty should change. 0 means no change, 1 means easier, -1 harder.
     */
    private static int computeDifficultyChange(List<StatEntry> entries, ErrorType errorType)
    {
        int errors = getNumErrors(entries, errorType);
        if (errors < 5)
            return 0;

        int mistakes = getNumMistakes(entries, errorType);
        float mistakeRatio = mistakes / (float) errors;
        if (mistakeRatio >= 0.333f)
            return 1;
        else if (mistakeRatio < 0.1f)
            return -1;
        else
            return 0;
    }

    private static int getNumErrors(List<StatEntry> entries, ErrorType errorType)
    {
        int n = 0;
        for (StatEntry entry : entries)
        {
            if (entry.hasError(errorType))
                n++;
        }
        return n;
    }

    private static int getNumMistakes(List<StatEntry> entries, ErrorType errorType)
    {
        int n = 0;
        for (StatEntry entry : entries)
        {
            if (entry.hasError(errorType) && entry.hasMistake(errorType))
                n++;
        }
        return n;
    }
}
