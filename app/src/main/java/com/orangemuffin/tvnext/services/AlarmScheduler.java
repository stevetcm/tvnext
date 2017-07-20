package com.orangemuffin.tvnext.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.orangemuffin.tvnext.receivers.AlarmReceiver;

import java.util.Calendar;
import java.util.TimeZone;

/* Created by OrangeMuffin on 2/26/2017 */
public class AlarmScheduler extends Service {
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Starting Service", Toast.LENGTH_LONG).show();
        Intent notification = new Intent(this, AlarmReceiver.class);
        boolean isAlarmUp = (PendingIntent.getBroadcast(this, 10031, notification, PendingIntent.FLAG_NO_CREATE) != null);

        final SharedPreferences sp_data = this.getSharedPreferences("PHONEDATA", this.MODE_PRIVATE);
        boolean update_timer = sp_data.getBoolean("NOTIF_UPDATE", false);

        if (!isAlarmUp || update_timer) {
            //Toast.makeText(this, "Setting Alarm Up", Toast.LENGTH_SHORT).show();
            Calendar updateTime = Calendar.getInstance();

            final int hour = sp_data.getInt("NOTIF_TIME_HOUR", 9);
            final int minute = sp_data.getInt("NOTIF_TIME_MINUTE", 15);

            updateTime.set(Calendar.HOUR_OF_DAY, hour);
            updateTime.set(Calendar.MINUTE, minute);

            Intent downloader = new Intent(this, AlarmReceiver.class);
            PendingIntent recurringDownload = PendingIntent.getBroadcast(this, 10031, downloader, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, recurringDownload);

            SharedPreferences.Editor editor = sp_data.edit();
            editor.putBoolean("NOTIF_UPDATE", false);
            editor.apply();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopSelf();

        Intent downloader = new Intent(this, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(this, 10031, downloader, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(recurringDownload);
        recurringDownload.cancel();

        //Toast.makeText(getApplicationContext(), "Destroying AlarmScheduler", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
