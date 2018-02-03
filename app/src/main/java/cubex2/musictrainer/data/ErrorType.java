package cubex2.musictrainer.data;

import android.support.annotation.NonNull;
import cubex2.musictrainer.R;

public enum ErrorType
{
    DURATION(R.string.error_type_duration_positive, R.string.error_type_duration_negative, ErrorType::applyDurationError, 0.01f, 0.01f, 10),
    VOLUME(R.string.error_type_volume_negative, R.string.error_type_volume_negative, ErrorType::applyVolumeError, 0.05f, 0.05f, 10);

    private final ErrorFunction errorFunction;
    private final int displayNamePositive;
    private final int displayNameNegative;
    private final float minError;
    private final float maxError;
    private final float errorStep;
    public final int numErrorValues;

    ErrorType(int displayNamePositive, int displayNameNegative, ErrorFunction errorFunction, float minError, float errorStep, int numErrorValues)
    {
        this.displayNamePositive = displayNamePositive;
        this.displayNameNegative = displayNameNegative;
        this.errorFunction = errorFunction;
        this.minError = minError;
        this.maxError = minError + (numErrorValues - 1) * errorStep;
        this.errorStep = errorStep;
        this.numErrorValues = numErrorValues;
    }

    public int getDisplayName(int sign)
    {
        if (sign >= 0)
            return displayNamePositive;
        else
            return displayNameNegative;
    }

    public float errorForIndex(int index)
    {
        if (index < 0 || index >= numErrorValues)
            return maxError;

        return maxError - (index * errorStep);
    }

    public void apply(int sign, @NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        errorFunction.apply(sign, difficulty, tone);
    }

    private static void applyDurationError(int sign, @NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        float error = sign * difficulty.getDurationError();
        tone.setDuration(tone.getDuration() + error);
    }

    private static void applyVolumeError(int sign, @NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        float error = difficulty.getVolumeError();
        tone.setVolume(tone.getVolume() - error);
    }

    interface ErrorFunction
    {
        void apply(int sign, @NonNull Difficulty difficulty, @NonNull PlayableTone tone);
    }
}
