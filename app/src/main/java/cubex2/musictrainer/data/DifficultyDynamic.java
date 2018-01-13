package cubex2.musictrainer.data;

import android.content.Context;
import cubex2.musictrainer.config.Settings;

public class DifficultyDynamic extends DifficultyFromSettings
{
    public DifficultyDynamic(Context context)
    {
        super(context);
    }

    @Override
    public float getDurationError()
    {
        return Settings.getDynamicDurationError(context);
    }

    @Override
    public float getVolumeError()
    {
        return Settings.getDynamicVolumeError(context);
    }
}
