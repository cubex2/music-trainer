package cubex2.musictrainer.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cubex2.musictrainer.R;
import cubex2.musictrainer.data.ErrorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static int getNumTones(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_num_tones_key);
        return Integer.valueOf(preferences.getString(key, "8"));
    }

    public static List<ErrorType> getActiveErrors(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_error_types_key);
        Set<String> values = preferences.getStringSet(key, Collections.emptySet());

        List<ErrorType> list = new ArrayList<>();

        if (values.contains("duration"))
            list.add(ErrorType.DURATION);
        if (values.contains("volume"))
            list.add(ErrorType.VOLUME);

        if (list.isEmpty())
        {
            Collections.addAll(list, ErrorType.DURATION, ErrorType.VOLUME);
        }

        return list;
    }

    public static boolean useDynamicDifficulty(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_use_dynamic_difficulty_key);

        return preferences.getBoolean(key, true);
    }

    public static int getDurationErrorIndex(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_duration_error_key);

        return Integer.valueOf(preferences.getString(key, "0"));
    }

    public static void setDurationError(Context context, int value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String key = context.getString(R.string.pref_duration_error_key);

        editor.putString(key, String.valueOf(value));

        editor.commit();
    }

    public static int getVolumeErrorIndex(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_volume_error_key);

        return Integer.valueOf(preferences.getString(key, "0"));
    }

    public static void setVolumeError(Context context, int value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String key = context.getString(R.string.pref_volume_error_key);

        editor.putString(key, String.valueOf(value));

        editor.commit();
    }

    public static int getMinimumStartToneKey(Context context)
    {
        return 30;
    }

    public static int getMaximumStartToneKey(Context context)
    {
        return 50;
    }

    public static int getDynamicDurationErrorIndex(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.dynamic_duration_error_key);

        return preferences.getInt(key, ErrorType.DURATION.numErrorValues - 1);
    }

    public static int getDynamicVolumeErrorIndex(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.dynamic_volume_error_key);

        return preferences.getInt(key, ErrorType.VOLUME.numErrorValues - 1);
    }

    public static void setDynamicErrorValues(Context context, int duration, int volume)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String durationKey = context.getString(R.string.dynamic_duration_error_key);
        String volumeKey = context.getString(R.string.dynamic_volume_error_key);

        editor.putInt(durationKey, duration);
        editor.putInt(volumeKey, volume);

        editor.commit();
    }
}
