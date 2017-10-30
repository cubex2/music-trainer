package cubex2.musictrainer.stats;

public class StatEntry
{
    private long timeStamp;
    private boolean wasCorrect;

    public StatEntry(long timeStamp, boolean wasCorrect)
    {
        this.timeStamp = timeStamp;
        this.wasCorrect = wasCorrect;
    }

    private StatEntry()
    {
    }

    public boolean isWasCorrect()
    {
        return wasCorrect;
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
