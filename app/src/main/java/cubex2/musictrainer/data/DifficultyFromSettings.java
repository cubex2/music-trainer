package cubex2.musictrainer.data;

import android.content.Context;
import cubex2.musictrainer.config.Settings;

import java.util.List;

public class DifficultyFromSettings implements Difficulty
{
    private final Context context;

    public DifficultyFromSettings(Context context)
    {
        this.context = context;
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
    public List<Float> getDurationErrors()
    {
        return Settings.getDurationErrors(context);
    }
}
