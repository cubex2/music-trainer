package cubex2.musictrainer.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cubex2.musictrainer.R;
import cubex2.musictrainer.Util;

public enum ErrorType
{
    DURATION(R.string.error_type_duration, ErrorType::applyDurationError),
    FREQUENCY(R.string.error_type_frequency, (difficulty, tone, prev, next) -> applyFrequencyError(difficulty, tone)),
    VOLUME(R.string.error_type_volume, (difficulty, tone, prev, next) -> applyVolumeError(difficulty, tone));

    private final ErrorFunction errorFunction;
    public final int displayName;

    ErrorType(int displayName, ErrorFunction errorFunction)
    {
        this.displayName = displayName;
        this.errorFunction = errorFunction;
    }

    public void apply(@NonNull Difficulty difficulty, @NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone)
    {
        errorFunction.apply(difficulty, tone, prevTone, nextTone);
    }

    private static void applyDurationError(@NonNull Difficulty difficulty, @NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone)
    {
        float error = Util.randomSign() * difficulty.getDurationError();
        tone.setDuration(tone.getDuration() + error);

        if (prevTone != null)
        {
            prevTone.setDuration(prevTone.getDuration() - error / 2F);
        }

        if (nextTone != null)
        {
            nextTone.setDuration(nextTone.getDuration() - error / 2F);
        }
    }

    private static void applyFrequencyError(@NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        int error = difficulty.getFrequencyError();
        tone.setTone(Tone.forKeyNumber(tone.getTone().getKeyNumber() + error));
    }

    private static void applyVolumeError(@NonNull Difficulty difficulty, @NonNull PlayableTone tone)
    {
        float error = difficulty.getVolumeError();
        tone.setVolume(tone.getVolume() - error);
    }

    interface ErrorFunction
    {
        void apply(@NonNull Difficulty difficulty, @NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone);
    }
}
