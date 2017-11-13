package cubex2.musictrainer.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cubex2.musictrainer.Util;

public enum ErrorType
{
    DURATION(ErrorType::applyDurationError),
    FREQUENCY((tone, prev, next) -> applyFrequencyError(tone)),
    VOLUME((tone, prev, next) -> applyVolumeError(tone));

    private static final float MIN_DURATION_ERROR = 0.1f;
    private static final float MAX_DURATION_ERROR = 0.25f;
    private static final int MIN_FREQUENCY_ERROR = 1;
    private static final int MAX_FREQUENCY_ERROR = 2;
    private static final float MIN_VOLUME_ERROR = 0.25f;
    private static final float MAX_VOLUME_ERROR = 0.5f;

    private final ErrorFunction errorFunction;

    ErrorType(ErrorFunction errorFunction)
    {
        this.errorFunction = errorFunction;
    }

    public void apply(@NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone)
    {
        errorFunction.apply(tone, prevTone, nextTone);
    }

    private static void applyDurationError(@NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone)
    {
        float error = Util.randomSign() * Util.randomInRange(MIN_DURATION_ERROR, MAX_DURATION_ERROR);
        tone.setDuration(tone.getDuration() + error);

        if (prevTone != null && nextTone != null)
        {
            prevTone.setDuration(prevTone.getDuration() - error / 2F);
            nextTone.setDuration(nextTone.getDuration() - error / 2F);
        } else if (prevTone != null)
        {
            prevTone.setDuration(prevTone.getDuration() - error);
        } else if (nextTone != null)
        {
            nextTone.setDuration(nextTone.getDuration() - error);
        }
    }

    private static void applyFrequencyError(@NonNull PlayableTone tone)
    {
        int error = Util.randomSign() * Util.randomInRange(MIN_FREQUENCY_ERROR, MAX_FREQUENCY_ERROR);
        tone.setTone(Tone.forKeyNumber(tone.getTone().getKeyNumber() + error));
    }

    private static void applyVolumeError(@NonNull PlayableTone tone)
    {
        float error = Util.randomInRange(MIN_VOLUME_ERROR, MAX_VOLUME_ERROR);
        tone.setVolume(tone.getVolume() - error);
    }

    interface ErrorFunction
    {
        void apply(@NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone);
    }
}
