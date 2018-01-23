package cubex2.musictrainer.config;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import cubex2.musictrainer.MainActivity;
import cubex2.musictrainer.R;
import cubex2.musictrainer.Util;

import java.util.*;

public class SettingsActivity extends AppCompatActivity
{
    private static class ChangeListener implements Preference.OnPreferenceChangeListener
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue = value.toString();

            if (preference instanceof ListPreference)
            {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                String summary = index >= 0
                                       ? String.valueOf(listPreference.getEntries()[index])
                                       : null;
                if (summary != null && summary.endsWith("%"))
                    summary += "%";

                preference.setSummary(summary);

            } else if (preference instanceof MultiSelectListPreference)
            {
                MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
                Set<String> setValue = (Set<String>) value;

                removeInvalidValues(setValue, listPreference.getEntryValues());

                if (setValue.isEmpty())
                {
                    Context context = preference.getContext();
                    Toast toast = Toast.makeText(context, context.getString(R.string.multi_select_invalid_selection), Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }

                List<String> displayValues = new ArrayList<>(setValue.size());
                for (String s : setValue)
                {
                    int index = listPreference.findIndexOfValue(s);
                    if (index >= 0)
                    {
                        displayValues.add(listPreference.getEntries()[index].toString());
                    }
                }
                Collections.sort(displayValues);

                preference.setSummary(Util.join(displayValues, ", "));

            } else if (!(preference instanceof CheckBoxPreference))
            {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    }

    private static void removeInvalidValues(Set<String> values, CharSequence[] validValues)
    {
        outer:
        for (Iterator<String> iterator = values.iterator(); iterator.hasNext(); )
        {
            String value = iterator.next();
            for (CharSequence validValue : validValues)
            {
                if (validValue.equals(value))
                    continue outer;
            }

            iterator.remove();
        }
    }

    private static void initPreference(Preference.OnPreferenceChangeListener listener, Preference preference)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(listener);

        Object value;
        if (preference instanceof MultiSelectListPreference)
        {
            value = preferences.getStringSet(preference.getKey(), new HashSet<>());
        } else if (preference instanceof CheckBoxPreference)
        {
            value = preferences.getBoolean(preference.getKey(), true);
        } else
        {
            value = preferences.getString(preference.getKey(), "");
        }

        // Trigger the listener immediately with the preference's
        // current value.
        listener.onPreferenceChange(preference, value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new DifficultyPreferenceFragment())
                            .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DifficultyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_difficulty);

            Preference.OnPreferenceChangeListener listener = new ChangeListener();

            initPreference(listener, findPreference(getString(R.string.pref_max_errors_key)));
            initPreference(listener, findPreference(getString(R.string.pref_sequence_types_key)));
            initPreference(listener, findPreference(getString(R.string.pref_error_types_key)));
            initPreference(listener, findPreference(getString(R.string.pref_num_tones_key)));
            initPreference(listener, findPreference(getString(R.string.pref_duration_error_key)));
            initPreference(listener, findPreference(getString(R.string.pref_volume_error_key)));
            initPreference(listener, findPreference(getString(R.string.pref_use_dynamic_difficulty_key)));
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
