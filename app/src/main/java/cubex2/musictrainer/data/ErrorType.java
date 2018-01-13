package cubex2.musictrainer.data;

import android.support.annotation.NonNull;
import cubex2.musictrainer.R;
import cubex2.musictrainer.Util;

public enum ErrorType
{
    DURATION(R.string.error_type_duration, ErrorType::applyDurationError),
    VOLUME(R.string.error_type_volume, ErrorType::applyVolumeError);

    private final ErrorFunction errorFunction;
    public final int displayName;

    ErrorType(int displayName, ErrorFunction errorFunction)
    {
        this.displayName = displayName;
        this.errorFunction = errorFunction;
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
