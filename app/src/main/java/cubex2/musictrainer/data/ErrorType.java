package cubex2.musictrainer.data;

import cubex2.musictrainer.Util;

public enum ErrorType
{
    DURATION(ErrorType::applyDurationError),
    FREQUENCY(ErrorType::applyFrequencyError),
    VOLUME(ErrorType::applyVolumeError);

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

    public void apply(PlayableTone tone)
    {
        errorFunction.apply(tone);
    }

    private static void applyDurationError(PlayableTone tone)
    {
        float error = Util.randomSign() * Util.randomInRange(MIN_DURATION_ERROR, MAX_DURATION_ERROR);
        tone.setDuration(tone.getDuration() + error);
    }

    private static void applyFrequencyError(PlayableTone tone)
    {
        int error = Util.randomSign() * Util.randomInRange(MIN_FREQUENCY_ERROR, MAX_FREQUENCY_ERROR);
        tone.setTone(Tone.forKeyNumber(tone.getTone().getKeyNumber() + error));
    }

    private static void applyVolumeError(PlayableTone tone)
    {
        float error = Util.randomInRange(MIN_VOLUME_ERROR, MAX_VOLUME_ERROR);
        tone.setVolume(tone.getVolume() - error);
    }

    interface ErrorFunction
    {
        void apply(PlayableTone tone);
    }
}
