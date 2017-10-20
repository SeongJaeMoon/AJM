package app.cap.ajm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;


public class Settings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;
    public static final String KEY_GPS_VALUE = "gps_level";
    public static final String KEY_WEIGHT_VALUE="weight_value";

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

    }
    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        //Map<String, ?> preferencesMap = sharedPreferences.getAll();
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
            final EditTextPreference editPref = (EditTextPreference)findPreference(KEY_WEIGHT_VALUE);
            editPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String news = newValue.toString();
                    if (!news.equals("")) {
                        int to = Integer.parseInt(news);
                        if (to < 10 || to > 120 || !isString(news))
                        {
                            Toast.makeText(getActivity(), getString(R.string.weight_val), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        else
                        {
                            editPref.setSummary(String.valueOf(to));
                            return true;
                        }
                    }
                    return true;
                }
            });
            final ListPreference listPref = (ListPreference)findPreference(KEY_GPS_VALUE);
            listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = newValue.toString();
                    if (!value.equals("")) {
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            switch (value) {
                                case "high":
                                    listPref.setSummary("높은 GPS 수신감도");
                                    break;
                                case "middle":
                                    listPref.setSummary("중간 GPS 수신감도");
                                    break;
                                case "low":
                                    listPref.setSummary("낮은 GPS 수신감도");
                                    break;
                                case "default":
                                    listPref.setSummary("기본 GPS 수신감도");
                                    break;
                            }
                        } else if (Locale.getDefault().getLanguage().equals("en")) {
                            switch (value) {
                                case "high":
                                    listPref.setSummary("High GPS sensitivity");
                                    break;
                                case "middle":
                                    listPref.setSummary("Middle GPS sensitivity");
                                    break;
                                case "low":
                                    listPref.setSummary("Low GPS sensitivity");
                                    break;
                                case "default":
                                    listPref.setSummary("Default GPS sensitivity");
                                    break;
                            }
                        } else if (Locale.getDefault().getLanguage().equals("zh")) {
                            switch (value) {
                                case "high":
                                    listPref.setSummary("高GPS灵敏度");
                                    break;
                                case "middle":
                                    listPref.setSummary("中GPS灵敏度");
                                    break;
                                case "low":
                                    listPref.setSummary("低GPS灵敏度");
                                    break;
                                case "default":
                                    listPref.setSummary("默认GPS灵敏度");
                                    break;
                            }
                        }else if (Locale.getDefault().getLanguage().equals("ja")){
                            switch (value) {
                                case "high":
                                    listPref.setSummary("高いGPS感度");
                                    break;
                                case "middle":
                                    listPref.setSummary("中程度のGPS感度");
                                    break;
                                case "low":
                                    listPref.setSummary("低いGPS感度");
                                    break;
                                case "default":
                                    listPref.setSummary("デフォルトのGPS感度");
                                    break;
                            }
                        }else{
                            listPref.setSummary(value);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        private void updatePreference(Preference preference)  {
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


