package com.orangemuffin.tvnext.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.MainActivity;
import com.orangemuffin.tvnext.models.Episode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/* Created by OrangeMuffin on 8/7/2017 */
public class NotificationUtil {

    public static void notificationTest(Context context) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_transparent_white)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(today)
                .setTicker("Notification Test")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0l}); //endless effort to disable vibrate;

        long[] patternSound = {0, 300, 0};
        mBuilder.setVibrate(patternSound);

        mBuilder.setLights(Color.BLUE, 700, 1500);

        //enabling heads-up notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        //set up how to deal with on click operation
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        mBuilder.setContentIntent(pendingIntent);

        //build and trigger notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(11008, mBuilder.build());
    }

    public static void createAiringNotification(Context context, List<Episode> episodeList) {
        Episode episode = episodeList.get(0);

        String seasonNum = episode.getSeasonNum();
        String episodeNum = episode.getEpisodeNum();
        String seriesName = episode.getSeriesName();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        if (episodeList.size() == 1) {
            String title = episode.getSeriesName();
            String text = StringFormatUtil.numDisplay(seasonNum, episodeNum) + " - " + episode.getName();

            //get internal location: data/Series
            ContextWrapper cw = new ContextWrapper(context);
            final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
            File mypath = new File(directory, episode.getSeriesId() + ".jpg");

            mBuilder.setLargeIcon(BitmapFactory.decodeFile(mypath.toString()))
                    .setSmallIcon(R.drawable.ic_transparent_white)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setTicker("New Episode Airing Today")
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0l}); //endless effort to disable vibrate;
        } else {
            String title = String.valueOf(episodeList.size()) + "New Episodes";
            String text = seriesName + " - " + StringFormatUtil.numDisplay(seasonNum, episodeNum);

            for (int i = 1; i < episodeList.size(); i++) {
                Episode temp = episodeList.get(i);
                text = text + "\n" + temp.getSeriesName() + " - " + StringFormatUtil.numDisplay(temp.getSeasonNum(), temp.getEpisodeNum());
            }

            mBuilder.setSmallIcon(R.drawable.ic_transparent_white)
                    .setContentTitle(title)
                    .setContentText("Click to show upcoming episodes")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setTicker(title)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0l}); //endless effort to disable vibrate;
        }

        long[] pattern = {0, 300, 0};
        mBuilder.setVibrate(pattern);

        mBuilder.setLights(Color.BLUE, 700, 1500);

        //enabling heads-up notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        //set up how to deal with on click operation
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        mBuilder.setContentIntent(pendingIntent);

        //build and trigger notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(11008, mBuilder.build());
    }
}
