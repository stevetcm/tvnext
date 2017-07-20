package com.orangemuffin.tvnext.adapters;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.MainActivity;
import com.orangemuffin.tvnext.activities.NotificationActivity;
import com.orangemuffin.tvnext.receivers.AlarmReceiver;
import com.orangemuffin.tvnext.services.AlarmScheduler;

/* Created by OrangeMuffin on 5/18/2017 */
public class NotificationCategoryAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private Context context;
    private String[] notificationCategory;
    private String[] notificationDetails;

    public NotificationCategoryAdapter(String[] notificationCategory, String[] notificationDetails, Context context) {
        this.notificationCategory = notificationCategory;
        this.notificationDetails = notificationDetails;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notificationCategory.length;
    }

    @Override
    public Object getItem(int i) {
        return notificationCategory[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder {
        TextView notificationName;
        TextView notificationDetails;
        Switch notificationSwitch;
        View notificationView;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder = new Holder();
        View rootView = inflater.inflate(R.layout.notificationlist_element, null);
        holder.notificationName = (TextView) rootView.findViewById(R.id.notificationlist_item);
        holder.notificationDetails = (TextView) rootView.findViewById(R.id.notificationlist_details);
        holder.notificationSwitch = (Switch) rootView.findViewById(R.id.notification_switch);
        holder.notificationView = (View) rootView.findViewById(R.id.notification_view);

        holder.notificationName.setText(notificationCategory[i]);
        holder.notificationDetails.setText(notificationDetails[i]);

        final SharedPreferences sp_data = context.getSharedPreferences("PHONEDATA", context.MODE_PRIVATE);
        boolean value = sp_data.getBoolean("NOTIF_STATE", true);

        holder.notificationSwitch.setChecked(value);

        holder.notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sp_data.edit();
                editor.putBoolean("NOTIF_STATE", b);
                editor.apply();

                Intent downloader = new Intent(context, AlarmScheduler.class);

                if (b) {
                    if (!isMyServiceRunning()) {
                        context.startService(downloader);
                    }
                } else {
                    //there must be a service in order to stop it
                    if (!isMyServiceRunning()) {
                        context.startService(downloader);
                    }
                    context.stopService(downloader);
                }

                //Toast.makeText(context, String.valueOf(isMyServiceRunning()), Toast.LENGTH_SHORT).show();
            }
        });

        if (i == 2) {
            holder.notificationName.setVisibility(View.GONE);
            holder.notificationDetails.setPadding(0, (int)dpToPixel(10), 0, 0);
            holder.notificationView.setVisibility(View.GONE);
        }

        if (i != 0) {
            holder.notificationSwitch.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void updateAdapter(int hour, int minute) {
        int hour_temp = hour;
        String ampm = " AM";

        if (hour == 0) { hour_temp = hour_temp + 12; }
        else if (hour > 12) {
            ampm = " PM";
            hour_temp = hour_temp-12;
        }

        String hour_str = String.valueOf(hour_temp);
        String minute_str = String.valueOf(minute);

        if (hour_temp < 10) { hour_str = "0" + hour_str; }
        if (minute < 10) { minute_str = "0" + minute_str; }

        notificationDetails[1] = hour_str + ":" + minute_str + ampm;
        notifyDataSetChanged();
    }

    public float dpToPixel(int dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    //check if AlarmScheduler is running
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AlarmScheduler.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}