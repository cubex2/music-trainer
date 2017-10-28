package cubex2.musictrainer.stats;

public class StatEntry
{
    private long timeStamp;

    public StatEntry(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    private StatEntry()
    {
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
