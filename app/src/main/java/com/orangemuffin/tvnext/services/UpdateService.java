package com.orangemuffin.tvnext.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.orangemuffin.tvnext.datafetch.TvSeriesDetailsFetch;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.trakt.TraktSeriesService;
import com.orangemuffin.tvnext.utils.LocalDataUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Created by OrangeMuffin on 8/5/2017 */
public class UpdateService extends IntentService {

    public UpdateService() { super("UpdateService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            List<TvSeries> seriesList = LocalDataUtil.getTvSeriesLocal(getApplicationContext());
            for (TvSeries tvSeries : seriesList) {
                //if tv series data needs to be updated
                if (!tvSeries.getLastUpdated().equals(TraktSeriesService.getSeriesLastUpdate(tvSeries.getImdbID()))) {
                    //fetch newest tv series data
                    TvSeries newSeries = TvSeriesDetailsFetch.fetchData(tvSeries.getId());

                    //update current tv series data
                    LocalDataUtil.updateTvSeries(getApplicationContext(), tvSeries, newSeries);

                    //broadcast refresh intent to update tv series
                    Intent broadcastIntent = new Intent("UPDATE_REFRESH");
                    broadcastIntent.putExtra("seriesId", tvSeries.getId());
                    broadcastIntent.putExtra("seriesName", tvSeries.getName());
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
                }
            }
        } catch (Exception e) { }

        //broadcast refresh intent to update tv series
        Intent broadcastIntent = new Intent("UPDATE_FINISHED");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
    }
}
