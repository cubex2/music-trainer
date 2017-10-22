package cubex2.musictrainer.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cubex2.musictrainer.R;

public class Settings
{
    public static int getMaxErrors(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_max_errors_key);
        return Integer.valueOf(preferences.getString(key, "1"));
    }

    public static boolean useScales(Context context)
    {
        return true;
    }

    public static boolean useArpeggios(Context context)
    {
        return true;
    }
}
