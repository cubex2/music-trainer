package cubex2.musictrainer.stats;

import cubex2.musictrainer.data.ErrorType;

public class StatEntry
{
    private long timeStamp;
    private boolean wasCorrect;
    private float durationError;
    private float volumeError;
    private int frequencyError;
    private boolean hasDurationError;
    private boolean hasVolumeError;
    private boolean hasFrequencyError;
    private boolean hasDurationMistake;
    private boolean hasVolumeMistake;
    private boolean hasFrequencyMistake;

    public StatEntry(long timeStamp, boolean wasCorrect,
                     float durationError, float volumeError, int frequencyError,
                     boolean hasDurationError, boolean hasVolumeError, boolean hasFrequencyError,
                     boolean hasDurationMistake, boolean hasVolumeMistake, boolean hasFrequencyMistake)
    {
        this.timeStamp = timeStamp;
        this.wasCorrect = wasCorrect;

        this.durationError = durationError;
        this.volumeError = volumeError;
        this.frequencyError = frequencyError;

        this.hasDurationError = hasDurationError;
        this.hasVolumeError = hasVolumeError;
        this.hasFrequencyError = hasFrequencyError;

        this.hasDurationMistake = hasDurationMistake;
        this.hasVolumeMistake = hasVolumeMistake;
        this.hasFrequencyMistake = hasFrequencyMistake;
    }

    private StatEntry()
    {
    }

    public boolean hasError(ErrorType errorType)
    {
        switch (errorType)
        {
            case DURATION:
                return hasDurationError;
            case VOLUME:
                return hasVolumeError;
            case FREQUENCY:
                return hasFrequencyError;
            default:
                return false;
        }
    }

    public boolean hasMistake(ErrorType errorType)
    {
        switch (errorType)
        {
            case DURATION:
                return hasDurationMistake;
            case VOLUME:
                return hasVolumeMistake;
            case FREQUENCY:
                return hasFrequencyMistake;
            default:
                return false;
        }
    }

    public float getError(ErrorType errorType)
    {
        switch (errorType)
        {
            case DURATION:
                return durationError;
            case VOLUME:
                return volumeError;
            case FREQUENCY:
                return frequencyError;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public boolean isWasCorrect()
    {
        return wasCorrect;
    }

    public float getDurationError()
    {
        return durationError;
    }

    public float getVolumeError()
    {
        return volumeError;
    }

    public int getFrequencyError()
    {
        return frequencyError;
    }

    public boolean hasDurationError()
    {
        return hasDurationError;
    }

    public boolean hasVolumeError()
    {
        return hasVolumeError;
    }

    public boolean hasFrequencyError()
    {
        return hasFrequencyError;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatEntry entry = (StatEntry) o;

        return timeStamp == entry.timeStamp;
    }

    @Override
    public int hashCode()
    {
        return (int) (timeStamp ^ (timeStamp >>> 32));
    }
}
