package cubex2.musictrainer.stats;

import cubex2.musictrainer.data.ErrorType;

public class StatEntry
{
    private long timeStamp;
    private boolean wasCorrect;
    private float durationError;
    private float volumeError;
    private boolean hasDurationError;
    private boolean hasVolumeError;
    private boolean hasDurationMistake;
    private boolean hasVolumeMistake;

    public StatEntry(long timeStamp, boolean wasCorrect,
                     float durationError, float volumeError,
                     boolean hasDurationError, boolean hasVolumeError,
                     boolean hasDurationMistake, boolean hasVolumeMistake)
    {
        this.timeStamp = timeStamp;
        this.wasCorrect = wasCorrect;

        this.durationError = durationError;
        this.volumeError = volumeError;

        this.hasDurationError = hasDurationError;
        this.hasVolumeError = hasVolumeError;

        this.hasDurationMistake = hasDurationMistake;
        this.hasVolumeMistake = hasVolumeMistake;
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

    public boolean hasDurationError()
    {
        return hasDurationError;
    }

    public boolean hasVolumeError()
    {
        return hasVolumeError;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    @Override
    public String toString()
    {
        return timeStamp +
               "," + wasCorrect +
               "," + durationError +
               "," + volumeError +
               "," + hasDurationError +
               "," + hasVolumeError +
               "," + hasDurationMistake +
               "," + hasVolumeMistake;
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
