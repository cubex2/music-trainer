package cubex2.musictrainer.data;

public class PlayableTone
{
    private Tone tone;
    private float duration;
    private float volume;

    public PlayableTone(Tone tone, float duration, float volume)
    {
        this.tone = tone;
        this.duration = duration;
        this.volume = volume;
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

    public float getVolume()
    {
        return volume;
    }

    void setVolume(float volume)
    {
        this.volume = volume;
    }
}
