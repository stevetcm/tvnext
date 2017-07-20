package com.orangemuffin.tvnext.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orangemuffin.tvnext.services.AlarmScheduler;

/* Created by OrangeMuffin on 2/25/2017 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent serviceIntent = new Intent(context, AlarmScheduler.class);
            context.startService(serviceIntent);
        }
    }
}
