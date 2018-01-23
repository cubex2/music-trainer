package cubex2.musictrainer.data;

import android.support.v4.math.MathUtils;
import cubex2.musictrainer.stats.StatEntry;

import java.util.List;

public class DynamicDifficultyHelper
{
    public static float normalizeDurationError(float error)
    {
        float min = ErrorType.DURATION.errorForIndex(ErrorType.DURATION.numErrorValues - 1);
        float max = ErrorType.DURATION.errorForIndex(0);
        return 1f - (error - min) / (max - min);
    }

    public static float normalizeVolumeError(float error)
    {
        float min = ErrorType.VOLUME.errorForIndex(ErrorType.VOLUME.numErrorValues - 1);
        float max = ErrorType.VOLUME.errorForIndex(0);
        return 1f - (error - min) / (max - min);
    }

    public static int computeNewDurationError(int current, List<StatEntry> entries)
    {
        int change = computeDifficultyChange(entries, ErrorType.DURATION);

        return MathUtils.clamp(current + change, 0, ErrorType.DURATION.numErrorValues - 1);
    }

    public static int computeNewVolumeError(int current, List<StatEntry> entries)
    {
        int change = computeDifficultyChange(entries, ErrorType.VOLUME);

        return MathUtils.clamp(current + change, 0, ErrorType.VOLUME.numErrorValues - 1);
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
            return -1;
        else if (mistakeRatio < 0.1f)
            return 1;
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
