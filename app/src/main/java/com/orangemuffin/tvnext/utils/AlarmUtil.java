package com.orangemuffin.tvnext.utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.orangemuffin.tvnext.receivers.AlarmReceiver;

import java.util.Calendar;

/* Created by OrangeMuffin on 8/7/2017 */
public class AlarmUtil {

    @TargetApi(23)
    public static void setExactAlarm(Context context, Calendar calendar, int mode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //create pending intent with a specific mode id to be executed
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("MODE_ID", mode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void setNextAlarm(Context context, Calendar calendar, int mode) {
        setExactAlarm(context, calendar, mode);
    }

    public static void setRepeatingAlarm(Context context, Calendar calendar, int mode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //create pending intent with a specific mode id to be executed
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("MODE_ID", mode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void cancelAlarm(Context context, int mode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static boolean isAlarmUp(Context context, int mode) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        return (PendingIntent.getBroadcast(context, mode, intent, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static Calendar setupCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar;
    }

    public static Calendar setupNextCalendar(int hour, int minute, int repeat) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DATE, repeat);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar;
    }
}
