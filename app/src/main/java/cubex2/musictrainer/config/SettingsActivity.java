package cubex2.musictrainer.config;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;
import cubex2.musictrainer.MainActivity;
import cubex2.musictrainer.R;
import cubex2.musictrainer.Util;

import java.util.*;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
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
                preference.setSummary(
                        index >= 0
                        ? listPreference.getEntries()[index]
                        : null);

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

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
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
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane()
    {
        return isXLargeTablet(this);
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName)
    {
        return PreferenceFragment.class.getName().equals(fragmentName)
               || DifficultyPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DifficultyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_difficulty);
            setHasOptionsMenu(true);

            Preference.OnPreferenceChangeListener listener = new ChangeListener();

            initPreference(listener, findPreference(getString(R.string.pref_max_errors_key)));
            initPreference(listener, findPreference(getString(R.string.pref_sequence_types_key)));
            initPreference(listener, findPreference(getString(R.string.pref_error_types_key)));
            initPreference(listener, findPreference(getString(R.string.pref_num_tones_key)));
            initPreference(listener, findPreference(getString(R.string.pref_duration_error_key)));
            initPreference(listener, findPreference(getString(R.string.pref_use_dynamic_difficulty_key)));
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
