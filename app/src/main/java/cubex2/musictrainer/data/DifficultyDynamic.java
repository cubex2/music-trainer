package cubex2.musictrainer.data;

import android.content.Context;
import cubex2.musictrainer.config.Settings;

import java.util.Collections;
import java.util.List;

public class DifficultyDynamic extends DifficultyFromSettings
{
    public DifficultyDynamic(Context context)
    {
        super(context);
    }

    @Override
    public List<Float> getDurationErrors()
    {
        return Collections.singletonList(Settings.getDynamicDurationError(context));
    }

    @Override
    public List<Float> getVolumeErrors()
    {
        return Collections.singletonList(Settings.getDynamicVolumeError(context));
    }

    @Override
    public List<Integer> getFrequencyErrors()
    {
        return Collections.singletonList(Settings.getDynamicFrequencyError(context));
    }
}
