package cubex2.musictrainer.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cubex2.musictrainer.R;
import cubex2.musictrainer.Util;
import cubex2.musictrainer.data.DynamicDifficultyHelper;
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

    public static List<Float> getDurationErrors(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_duration_error_key);
        Set<String> values = preferences.getStringSet(key, Collections.emptySet());

        List<Float> durations = new ArrayList<>();
        for (String value : values)
        {
            durations.add(Float.parseFloat(value));
        }

        if (durations.isEmpty())
        {
            float[] defaults = Util.getFloatArray(context, R.array.pref_duration_error_values);
            for (float duration : defaults)
            {
                durations.add(duration);
            }
        }

        return durations;
    }

    public static List<Float> getVolumeErrors(Context context)
    {
        List<Float> errors = new ArrayList<>();
        errors.add(0.25f);
        errors.add(0.30f);
        errors.add(0.35f);
        errors.add(0.40f);
        errors.add(0.45f);
        errors.add(0.50f);
        return errors;
    }

    public static int getMinimumStartToneKey(Context context)
    {
        return 30;
    }

    public static int getMaximumStartToneKey(Context context)
    {
        return 50;
    }

    public static float getDynamicDurationError(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.dynamic_duration_error_key);

        return preferences.getFloat(key, DynamicDifficultyHelper.DURATION_ERROR_MAX);
    }

    public static float getDynamicVolumeError(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.dynamic_volume_error_key);

        return preferences.getFloat(key, DynamicDifficultyHelper.VOLUME_ERROR_MAX);
    }

    public static void setDynamicErrorValues(Context context, float duration, float volume)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String durationKey = context.getString(R.string.dynamic_duration_error_key);
        String volumeKey = context.getString(R.string.dynamic_volume_error_key);

        editor.putFloat(durationKey, duration);
        editor.putFloat(volumeKey, volume);

        editor.commit();
    }
}
