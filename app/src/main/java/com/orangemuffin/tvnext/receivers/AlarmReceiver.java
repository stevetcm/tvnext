package com.orangemuffin.tvnext.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.orangemuffin.tvnext.services.NotificationService;
import com.orangemuffin.tvnext.services.UpdateService;
import com.orangemuffin.tvnext.utils.AlarmUtil;

import java.util.Calendar;

/* Created by OrangeMuffin on 8/7/2017 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int modeID = intent.getIntExtra("MODE_ID", -1);

        if (modeID == 11008) {
            context.startService(new Intent(context, NotificationService.class));

            Calendar calendar = AlarmUtil.setupNextCalendar(9, 15, 1);
            AlarmUtil.setNextAlarm(context, calendar, 11008);
        } else if (modeID == 11010) {
            context.startService(new Intent(context, UpdateService.class));
        }
    }
}
