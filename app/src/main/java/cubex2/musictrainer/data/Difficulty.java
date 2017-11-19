package cubex2.musictrainer.data;

import android.content.Context;
import cubex2.musictrainer.config.Settings;

import java.util.List;

public interface Difficulty
{
    int getNumTones();

    int getMaxErrors();

    boolean useScales();

    boolean useArpeggios();

    List<ErrorType> getErrorTypes();

    static Difficulty fromSettings(Context context)
    {
        return new Difficulty()
        {
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
        };
    }
}
