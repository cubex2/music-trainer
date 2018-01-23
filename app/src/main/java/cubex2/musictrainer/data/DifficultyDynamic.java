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
    public int getDurationErrorIndex()
    {
        return Settings.getDynamicDurationErrorIndex(context);
    }

    @Override
    public int getVolumeErrorIndex()
    {
        return Settings.getDynamicVolumeErrorIndex(context);
    }
}
