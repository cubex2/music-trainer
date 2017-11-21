package cubex2.musictrainer.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cubex2.musictrainer.R;
import cubex2.musictrainer.Util;

import java.util.List;

public enum ErrorType
{
    DURATION(R.string.error_type_duration, ErrorType::applyDurationError),
    FREQUENCY(R.string.error_type_frequency, (durErrors, tone, prev, next) -> applyFrequencyError(tone)),
    VOLUME(R.string.error_type_volume, (durErrors, tone, prev, next) -> applyVolumeError(tone));

    private static final int MIN_FREQUENCY_ERROR = 1;
    private static final int MAX_FREQUENCY_ERROR = 2;
    private static final float MIN_VOLUME_ERROR = 0.25f;
    private static final float MAX_VOLUME_ERROR = 0.5f;

    private final ErrorFunction errorFunction;
    public final int displayName;

    ErrorType(int displayName, ErrorFunction errorFunction)
    {
        this.displayName = displayName;
        this.errorFunction = errorFunction;
    }

    public void apply(@NonNull List<Float> durationErrors, @NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone)
    {
        errorFunction.apply(durationErrors, tone, prevTone, nextTone);
    }

    private static void applyDurationError(@NonNull List<Float> durationErrors, @NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone)
    {
        float error = Util.randomSign() * Util.randomElement(durationErrors);
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
        void apply(@NonNull List<Float> durationErrors, @NonNull PlayableTone tone, @Nullable PlayableTone prevTone, @Nullable PlayableTone nextTone);
    }
}
