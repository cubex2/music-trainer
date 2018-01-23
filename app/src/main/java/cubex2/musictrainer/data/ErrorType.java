package cubex2.musictrainer.data;

import android.support.annotation.NonNull;
import cubex2.musictrainer.R;
import cubex2.musictrainer.Util;

public enum ErrorType
{
    DURATION(R.string.error_type_duration, ErrorType::applyDurationError, 0.05f, 0.05f, 8),
    VOLUME(R.string.error_type_volume, ErrorType::applyVolumeError, 0.15f, 0.05f, 8);

    private final ErrorFunction errorFunction;
    public final int displayName;
    private final float minError;
    private final float maxError;
    private final float errorStep;
    public final int numErrorValues;

    ErrorType(int displayName, ErrorFunction errorFunction, float minError, float errorStep, int numErrorValues)
    {
        this.displayName = displayName;
        this.errorFunction = errorFunction;
        this.minError = minError;
        this.maxError = minError + (numErrorValues - 1) * errorStep;
        this.errorStep = errorStep;
        this.numErrorValues = numErrorValues;
    }

    public float errorForIndex(int index)
    {
        if (index < 0 || index >= numErrorValues)
            return maxError;

        return maxError - (index * errorStep);
    }

    public void apply(@NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        errorFunction.apply(difficulty, tone);
    }

    private static void applyDurationError(@NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        float error = Util.randomSign() * difficulty.getDurationError();
        tone.setDuration(tone.getDuration() + error);
    }

    private static void applyVolumeError(@NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        float error = difficulty.getVolumeError();
        tone.setVolume(tone.getVolume() - error);
    }

    interface ErrorFunction
    {
        void apply(@NonNull Difficulty difficulty, @NonNull PlayableTone tone);
    }
}
