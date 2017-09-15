package app.cap.ajm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.Map;

public class Settings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;
    SwitchPreference switchPreference, switchPreference1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.settingstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Map<String, ?> preferencesMap = sharedPreferences.getAll();
        //** iterate through the preference entries and update their summary if they are an instance of EditTextPreference**//
        for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
            if (preferenceEntry instanceof EditTextPreference) {
                updateSummary((EditTextPreference) preferenceEntry);
            }
        }
    }
    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        //Map<String, ?> preferencesMap = sharedPreferences.getAll();
        //**iterate through the preference entries and update their summary if they are an instance of EditTextPreference**//
        //for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet())
        //{
        //if (preferenceEntry instanceof EditTextPreference ) {

        //updateSummary((EditTextPreference) preferenceEntry);
        //}
        //}
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        updatePreference(preferenceGroup.getPreference(j));
                    }
                } else {
                    updatePreference(preference);
                }
            }
            final Preference editPref = findPreference("weight_value");
            editPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference editTextPreference = (EditTextPreference) preference;
                    String news = newValue.toString();
                    if (!news.equals("")) {
                        Log.i("Setting", "!news.equals");
                        int to = Integer.parseInt(news);
                        if (to < 40 || to > 100 || !isString(news))
                        {
                            Toast.makeText(getActivity(), "몸무게는 40kg~100kg 사이로 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        else
                        {
                            editTextPreference.setSummary(editTextPreference.getText());
                            return true;
                        }
                    }

                    return true;
                }
            });
        }
        private void updatePreference(Preference preference) {
            if (preference == null) {
                return;
            }
            if (preference.getKey().equals("gps_level")) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntry());
                return;
            }
            if (preference.getKey().equals("weight_value")) {
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(editTextPreference.getText());
            }
        }
    }
    private void updateSummary(EditTextPreference preference) {
        preference.setSummary(preference.getText());
    }
    public static boolean isString(String s)
    {
        try{
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException e)
        {
            return false;
        }
    }
}


