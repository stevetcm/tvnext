package com.orangemuffin.tvnext.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.orangemuffin.tvnext.services.BackgroundServices;

/* Created by OrangeMuffin on 2/25/2017 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dailyUpdater = new Intent(context, BackgroundServices.class);
        context.startService(dailyUpdater);
        Log.d("AlarmReceiver", "Calling onReceive - Alarm Services");
    }
}
