package cubex2.musictrainer.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cubex2.musictrainer.R;

import java.util.Collections;
import java.util.Set;

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_sequence_types_key);
        Set<String> values = preferences.getStringSet(key, Collections.emptySet());

        return values.contains("scale");
    }

    public static boolean useArpeggios(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_sequence_types_key);
        Set<String> values = preferences.getStringSet(key, Collections.emptySet());

        return values.contains("arpeggio");
    }

    public static int getMinimumStartToneKey(Context context)
    {
        return 10;
    }

    public static int getMaximumStartToneKey(Context context)
    {
        return 70;
    }
}
