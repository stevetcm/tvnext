package com.orangemuffin.tvnext.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.SettingsActivity;
import com.orangemuffin.tvnext.utils.AlarmUtil;

import java.util.Calendar;

/* Created by OrangeMuffin on 8/9/2017 */
public class FragmentSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference notifScreen = findPreference("notification_screen");
        notifScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingsActivity) getActivity()).switchFragment("notification_screen");
                return false;
            }
        });

        Preference aboutScreen = findPreference("about_screen");
        aboutScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingsActivity) getActivity()).switchFragment("about_screen");
                return false;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("auto_update")) {
            boolean status = sharedPreferences.getBoolean("auto_update", true);
            if (!status) {
                AlarmUtil.cancelAlarm(getActivity(), 11010);
            } else {
                Calendar calendar = AlarmUtil.setupCalendar(6, 15);
                AlarmUtil.setRepeatingAlarm(getActivity(), calendar, 11010);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
