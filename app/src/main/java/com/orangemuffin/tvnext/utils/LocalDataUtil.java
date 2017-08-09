package com.orangemuffin.tvnext.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;
import com.squareup.picasso.Picasso;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/* Created by OrangeMuffin on 7/30/2017 */
public class LocalDataUtil {

    public static Map<String, String> getTvSeriesMap(Context context) {
        try {
            File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            return (Map<String, String>) inputStream.readObject();
        } catch (Exception e) { }
        return new HashMap<>();
    }

    public static void addToTvSeriesMap(Context context, String seriesName, String seriesId) {
        try {
            Map<String, String> tvshow_map = getTvSeriesMap(context);

            File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            tvshow_map.put(seriesName, seriesId);
            outputStream.writeObject(tvshow_map);
            outputStream.flush(); outputStream.close();
        } catch (Exception e) { }
    }

    public static void removeFromTvSeriesMap(Context context, String seriesName) {
        try {
            File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            Map<String, String> tvshow_map = (Map<String, String>) inputStream.readObject();

            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            tvshow_map.remove(seriesName);
            outputStream.writeObject(tvshow_map);
            outputStream.flush(); outputStream.close();
        } catch (Exception e) { }
    }

    public static List<TvSeries> getTvSeriesLocal(Context context) throws Exception {
        Map<String, String> tvshow_map = getTvSeriesMap(context);

        List<TvSeries> data = new ArrayList<>();

        SortedSet<String> keys = new TreeSet<>(tvshow_map.keySet());
        for (String key : keys) {
            String value = tvshow_map.get(key);
            if (!value.equals("")) {
                try {
                    ContextWrapper cw = new ContextWrapper(context);
                    final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                    File mypath = new File(directory, value + ".ser");

                    FileInputStream fis = new FileInputStream(mypath);
                    FSTObjectInput in = new FSTObjectInput(fis);
                    TvSeries result = (TvSeries) in.readObject(TvSeries.class);
                    in.close();
                    data.add(result);
                } catch (Exception e) { }
            }
        }

        return data;
    }

    public static void saveTvSeries(Context context, TvSeries tvSeries) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
        File mypath = new File(directory, tvSeries.getId() + ".ser");
        if (mypath.exists()) mypath.delete();

        try {
            FileOutputStream fos = new FileOutputStream(mypath);
            FSTObjectOutput out = new FSTObjectOutput(fos);
            out.writeObject(tvSeries, TvSeries.class);
            out.close(); fos.close();
        } catch (Exception e) {}
    }

    public static void updateTvSeries(Context context, TvSeries oldSeries, TvSeries newSeries) {
        newSeries.setWatchedIndex(oldSeries.getWatchedIndex());

        for (Season newSeason : newSeries.getSeasons()) {
            for (Season oldSeason : oldSeries.getSeasons()) {
                if (oldSeason.getSeasonNum().equals(newSeason.getSeasonNum())) {
                    newSeason.setWatchedIndex(oldSeason.getWatchedIndex());
                    break;
                }
            }
        }

        saveTvSeries(context, newSeries);
    }

    public static void saveTvPoster(Context context, final String seriesId, String urlPoster) {
        ImageLoader imageLoader = ImageLoader.getInstance();

        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
        if (urlPoster != null) {
            imageLoader.loadImage(urlPoster, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //save image poster of tv show
                    File new_mypath_img = new File(directory, seriesId + ".jpg");
                    if (new_mypath_img.exists()) new_mypath_img.delete();
                    try {
                        FileOutputStream newly_fos = new FileOutputStream(new_mypath_img);
                        loadedImage.compress(Bitmap.CompressFormat.JPEG, 75, newly_fos);
                        newly_fos.flush(); newly_fos.close();
                    } catch (Exception e) { }
                }
            });
        }
    }

    public static void removeTvSeriesData(Context context, String seriesId) {
        //get internal location data/series
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);

        //remove serializable tv show data
        File mypath = new File(directory, seriesId + ".ser");
        if (mypath.exists()) mypath.delete();

        //remove image poster of tv show
        File mypath_img = new File(directory, seriesId + ".jpg");
        if (mypath_img.exists()) mypath_img.delete();
        Picasso.with(context).invalidate(mypath_img);
    }
}
