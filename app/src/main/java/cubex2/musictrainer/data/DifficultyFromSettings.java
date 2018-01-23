package cubex2.musictrainer.data;

import android.content.Context;
import cubex2.musictrainer.config.Settings;

import java.util.List;

public class DifficultyFromSettings implements Difficulty
{
    protected final Context context;

    private final int durationErrorIndex;
    private final int volumeErrorIndex;

    public DifficultyFromSettings(Context context)
    {
        this.context = context;

        durationErrorIndex = Settings.getDurationErrorIndex(context);
        volumeErrorIndex = Settings.getVolumeErrorIndex(context);
    }

    @Override
    public int getNumTones()
    {
        return Settings.getNumTones(context);
    }

    @Override
    public int getMaxErrors()
    {
        return Settings.getMaxErrors(context);
    }

    @Override
    public boolean useScales()
    {
        return Settings.useScales(context);
    }

    @Override
    public boolean useArpeggios()
    {
        return Settings.useArpeggios(context);
    }

    @Override
    public List<ErrorType> getErrorTypes()
    {
        return Settings.getActiveErrors(context);
    }

    @Override
    public int getDurationErrorIndex()
    {
        return durationErrorIndex;
    }

    @Override
    public int getVolumeErrorIndex()
    {
        return volumeErrorIndex;
    }

    @Override
    public float getDurationError()
    {
        return ErrorType.DURATION.errorForIndex(getDurationErrorIndex());
    }

    @Override
    public float getVolumeError()
    {
        return ErrorType.VOLUME.errorForIndex(getVolumeErrorIndex());
    }
}
