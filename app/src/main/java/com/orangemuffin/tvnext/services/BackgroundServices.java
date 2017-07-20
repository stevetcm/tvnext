package com.orangemuffin.tvnext.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.MainActivity;
import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbDetailsService;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/* Created by OrangeMuffin on 3/16/2017 */
public class BackgroundServices extends IntentService {

    private Context context;

    public BackgroundServices() {
        super("BackgroundServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        ImageLoader imageLoader = ImageLoader.getInstance();
        List<TvSeries> data = new ArrayList<>();
        Map<String, String> tvshow_map = new HashMap<String, String>();
        try {
            File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            tvshow_map = (Map<String, String>) inputStream.readObject();
        } catch (Exception e) { }

        SortedSet<String> keys = new TreeSet<>(tvshow_map.keySet());

        //get internal location: data/Series
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);

        //NOTIFICATION SERVICE
        for (String key : keys) {
            String value = tvshow_map.get(key);
            if (!value.equals("")) {
                try {
                    File mypath = new File(directory, value + ".ser");
                    FileInputStream fis = new FileInputStream(mypath);
                    FSTObjectInput in = new FSTObjectInput(fis);
                    TvSeries result = (TvSeries) in.readObject(TvSeries.class);
                    in.close();
                    data.add(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int index = 0;
        List<Episode> dataEpisode = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            List<Season> seasons = data.get(i).getSeasons();
                if (seasons.size() != 0) {
                int lastItem = seasons.size()-1;
                List<Episode> episodes = seasons.get(lastItem).getEpisodes();
                for (int j = 0; j < episodes.size(); j++) {
                    Episode episode = episodes.get(j);
                    if (!episode.getAirdate().equals("unknown")) {
                        if (episode.getDue() == 0) {
                            dataEpisode.add(episode);
                            index = i;
                            break;
                        } else if (episode.getDue() > 0) {
                            break;
                        }
                    }
                }
            }
        }

        if (dataEpisode.size() == 1) {
            String seasonNum = dataEpisode.get(0).getSeasonNum();
            String episodeNum = dataEpisode.get(0).getEpisodeNum();

            String name = dataEpisode.get(0).getSeriesName();
            String episode = episodeIndex(seasonNum, episodeNum) + " - " + dataEpisode.get(0).getName();

            File mypath = new File(directory, data.get(index).getId() + ".jpg");

            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.putExtra("serieName", data.get(index).getName());
            intent2.putExtra("serieId", data.get(index).getId());
            intent2.putExtra("viewing", "no");

            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent2, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setLargeIcon(BitmapFactory.decodeFile(mypath.toString()))
                    .setSmallIcon(R.drawable.icon_transparent)
                    .setTicker("New Episode Airing Today")
                    .setContentTitle(name)
                    .setContentText(episode)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH);

            builder.setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, builder.build());
        } else if (dataEpisode.size() > 1) {
            String seasonNum = dataEpisode.get(0).getSeasonNum();
            String episodeNum = dataEpisode.get(0).getEpisodeNum();
            String serieName = dataEpisode.get(0).getSeriesName();

            String name = String.valueOf(dataEpisode.size()) + " New Episodes";
            String episodes = serieName + " - " + episodeIndex(seasonNum, episodeNum);

            for (int i = 1; i < dataEpisode.size(); i++) {
                seasonNum = dataEpisode.get(i).getSeasonNum();
                episodeNum = dataEpisode.get(i).getEpisodeNum();
                serieName = dataEpisode.get(i).getSeriesName();
                episodes = episodes + "\n" + serieName + " - " + episodeIndex(seasonNum, episodeNum);
            }

            Intent intent2 = new Intent(context, MainActivity.class);

            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent2, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icon_transparent)
                    .setContentTitle(name)
                    .setTicker(name)
                    .setContentText("Click to show upcoming episodes")
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(episodes));

            builder.setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, builder.build());
        }

        //UPDATE SERVICE
        if (isNetworkAvailable()) {
            for (String key : keys) {
                String value = tvshow_map.get(key);
                TvSeries result = new TvSeries();

                try {
                    TheTvdbDetailsService detailsService = new TheTvdbDetailsService();
                    final TvSeries tvSeries = detailsService.getSeriesDetails(value);

                    try {
                        File mypath = new File(directory, value + ".ser");

                        FileInputStream fis = new FileInputStream(mypath);
                        FSTObjectInput in = new FSTObjectInput(fis);
                        result = (TvSeries) in.readObject(TvSeries.class);
                        in.close();
                    } catch (Exception e) { }

                    //only update if content was recently updated
                    if (!tvSeries.getLastUpdated().equals(result.getLastUpdated())) {
                        //need to import saved data from result for tv series
                        tvSeries.setAbsoluteCount(result.getAbsoluteCount());
                        tvSeries.setAbsoluteIndex(result.getAbsoluteIndex());
                        tvSeries.setWatchedVar(result.getWatchedVar());
                        tvSeries.setWatchedIndex(result.getWatchedIndex());

                        //setup numEpisodes for updated data
                        tvSeries.setNumEpisodes();

                        //need to import saved data from result for each seasons
                        for (int i = 0; i < tvSeries.getSeasons().size(); i++) {
                            if (tvSeries.getSeasons().get(i).getSeasonNum()
                                    .equals(result.getSeasons().get(i).getSeasonNum())) {
                                tvSeries.getSeasons().get(i).setWatchedVar
                                        (result.getSeasons().get(i).getWatchedVar());
                            }
                        }

                        //save new serializable tv series data
                        File mypath = new File(directory, tvSeries.getId() + ".ser");
                        if (mypath.exists()) mypath.delete();
                        try {
                            FileOutputStream fos = new FileOutputStream(mypath);
                            FSTObjectOutput out = new FSTObjectOutput(fos);
                            out.writeObject(tvSeries, TvSeries.class);
                            out.close();
                        } catch (Exception e) { }

                        //saving new poster of tv series
                        String urlBase = "http://thetvdb.com/banners/_cache/";
                        String urlPoster = tvSeries.getPoster();
                        if (urlPoster != null && !urlPoster.equals("")) {
                            imageLoader.loadImage(urlBase + urlPoster, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    //save image poster of tv show
                                    File mypath_img = new File(directory, tvSeries.getId() + ".jpg");
                                    if (mypath_img.exists()) mypath_img.delete();
                                    try {
                                        FileOutputStream fos = new FileOutputStream(mypath_img);
                                        loadedImage.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                                        fos.flush();
                                        fos.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) { }
            }
        }
    }

    public String episodeIndex(String season, String episode) {
        if (Integer.parseInt(season) < 10 && Integer.parseInt(episode) < 10) {
            return "S0" + season + "E0" + episode;
        } else if (Integer.parseInt(season) >= 10 && Integer.parseInt(episode) < 10) {
            return"S" + season + "E0" + episode;
        } else if (Integer.parseInt(season) < 10 && Integer.parseInt(episode) >= 10) {
            return"S0" + season + "E" + episode;
        } else if (Integer.parseInt(season) >= 10 && Integer.parseInt(episode) >= 10) {
            return "S" + season + "E" + episode;
        }
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
