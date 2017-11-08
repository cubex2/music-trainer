package cubex2.musictrainer.data;

public class PlayableTone
{
    private Tone tone;
    private float duration;

    public PlayableTone(Tone tone, float duration)
    {
        this.tone = tone;
        this.duration = duration;
    }

    public Tone getTone()
    {
        return tone;
    }

    void setTone(Tone tone)
    {
        this.tone = tone;
    }

    public float getDuration()
    {
        return duration;
    }

    void setDuration(float duration)
    {
        this.duration = duration;
    }
}
