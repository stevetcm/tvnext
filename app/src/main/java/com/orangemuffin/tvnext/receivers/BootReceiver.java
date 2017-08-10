package com.orangemuffin.tvnext.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orangemuffin.tvnext.utils.AlarmUtil;

import java.util.Calendar;

/* Created by OrangeMuffin on 8/9/2017 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            //setting up alarm for notification services
            if (sharedPreferences.getBoolean("notifications", true)) {
                Calendar notificationCalendar = AlarmUtil.setupCalendar(9, 15);
                AlarmUtil.setExactAlarm(context, notificationCalendar, 11008);
            }

            //setting up alarm for update services
            if (sharedPreferences.getBoolean("auto_update", true)) {
                Calendar updateCalendar = AlarmUtil.setupCalendar(6, 15);
                AlarmUtil.setRepeatingAlarm(context, updateCalendar, 11010);
            }
        }
    }
}
