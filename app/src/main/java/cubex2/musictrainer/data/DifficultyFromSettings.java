package cubex2.musictrainer.data;

import android.content.Context;
import cubex2.musictrainer.Util;
import cubex2.musictrainer.config.Settings;

import java.util.List;

public class DifficultyFromSettings implements Difficulty
{
    protected final Context context;

    private final float durationError;
    private final float volumeError;

    public DifficultyFromSettings(Context context)
    {
        this.context = context;

        durationError = Util.randomElement(Settings.getDurationErrors(context));
        volumeError = Util.randomElement(Settings.getVolumeErrors(context));
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
    public float getDurationError()
    {
        return durationError;
    }

    @Override
    public float getVolumeError()
    {
        return volumeError;
    }
}
