package com.orangemuffin.tvnext.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.services.NotificationService;
import com.orangemuffin.tvnext.utils.AlarmUtil;

import java.util.Calendar;

/* Created by OrangeMuffin on 8/10/2017 */
public class FragmentNotifications extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notifications);
        updatePreferenceSummary();

        Preference notifTest = findPreference("notification_test");
        notifTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().startService(new Intent(getActivity(), NotificationService.class));
                return false;
            }
        });
    }

    public void updatePreferenceSummary() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        boolean status = sharedPreferences.getBoolean("notifications", true);
        if (!status) {
            findPreference("checkBoxVibrate").setEnabled(false);
            findPreference("notificationSound").setEnabled(false);
            findPreference("checkBoxLED").setEnabled(false);
        } else {
            findPreference("checkBoxVibrate").setEnabled(true);
            findPreference("notificationSound").setEnabled(true);
            findPreference("checkBoxLED").setEnabled(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferenceSummary();

        if (key.equals("notifications")) {
            boolean status = sharedPreferences.getBoolean("notifications", true);
            if (!status) {
                AlarmUtil.cancelAlarm(getActivity(), 11008);
            } else {
                Calendar calendar = AlarmUtil.setupCalendar(9, 15);
                AlarmUtil.setExactAlarm(getActivity(), calendar, 11008);
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
