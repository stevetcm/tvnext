package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.NotificationCategoryAdapter;
import com.orangemuffin.tvnext.services.AlarmScheduler;

/* Created by OrangeMuffin on 5/9/2017 */
public class NotificationActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private NotificationCategoryAdapter adapter;
    private ListView notification_list;

    private String[] notificationCategory = {"Notifications & Auto Update", "When to notify & update"};
    private String[] notificationDetails = {"Notify about new episodes and update data", ""};

    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notification_list = (ListView) findViewById(R.id.notification_list);

        final SharedPreferences sp_data = this.getSharedPreferences("PHONEDATA", this.MODE_PRIVATE);
        final int hour_saved = sp_data.getInt("NOTIF_TIME_HOUR", 9);
        final int minute_saved = sp_data.getInt("NOTIF_TIME_MINUTE", 15);

        int hour_temp = hour_saved;
        String ampm = " AM";

        if (hour_saved == 0) { hour_temp = hour_temp + 12; }
        else if (hour_saved == 12) { ampm = "PM"; }
        else if (hour_saved > 12) {
            ampm = " PM";
            hour_temp = hour_temp - 12;
        }

        String hour_str = String.valueOf(hour_temp);
        String minute_str = String.valueOf(minute_saved);


        if (hour_temp < 10) { hour_str = "0" + hour_str; }
        if (minute_saved < 10) { minute_str = "0" + minute_str; }

        notificationDetails[1] = hour_str + ":" + minute_str + ampm;

        notification_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int hour_saved = sp_data.getInt("NOTIF_TIME_HOUR", 9);
                final int minute_saved = sp_data.getInt("NOTIF_TIME_MINUTE", 15);

                timePickerDialog = new TimePickerDialog(NotificationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        if (Build.VERSION.SDK_INT >= 23 ) {
                            timePicker.setHour(hour_saved);
                            timePicker.setMinute(minute_saved);
                        } else {
                            timePicker.setCurrentHour(hour_saved);
                            timePicker.setCurrentMinute(minute_saved);
                        }

                        //save the time settings
                        SharedPreferences.Editor editor = sp_data.edit();
                        editor.putInt("NOTIF_TIME_HOUR", hour);
                        editor.putInt("NOTIF_TIME_MINUTE", minute);
                        editor.putBoolean("NOTIF_UPDATE", true);
                        editor.apply();

                        //overwrite background services alarm if running
                        if (isMyServiceRunning()) {
                            Intent downloader = new Intent(getApplicationContext(), AlarmScheduler.class);
                            getApplicationContext().startService(downloader);
                        }

                        adapter.updateAdapter(hour, minute);
                    }
                }, hour_saved, minute_saved, false);
                timePickerDialog.setTitle("");
                timePickerDialog.show();
            }
        });

        adapter = new NotificationCategoryAdapter(notificationCategory, notificationDetails, getApplicationContext());
        notification_list.setAdapter(adapter);
    }

    //check if AlarmScheduler is running
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AlarmScheduler.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
    public void finish() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        super.finish();
    }
}
