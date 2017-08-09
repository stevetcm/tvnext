package com.orangemuffin.tvnext.services;

import android.app.IntentService;
import android.content.Intent;

import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.utils.LocalDataUtil;
import com.orangemuffin.tvnext.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 8/7/2017 */
public class NotificationService extends IntentService  {

    public NotificationService() { super("NotificationService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Episode> episodeList = new ArrayList<>();

        try {
            List<TvSeries> seriesList = LocalDataUtil.getTvSeriesLocal(getApplicationContext());

            for (TvSeries tvSeries : seriesList) {
                seriesLabel: {
                    List<Season> seasons = tvSeries.getSeasons();
                    for (int j = seasons.size()-1; j >= 0; j--) {
                        if (!seasons.get(j).getSeasonNum().equals("0")) {
                            List<Episode> episodes = seasons.get(j).getEpisodes();
                            for (int k = episodes.size() - 1; k >= 0; k--) {
                                Episode episode = episodes.get(k);
                                if (!episode.getAirdate().equals("unknown")) {
                                    if (episode.getDue() == 0) {
                                        episodeList.add(episode);
                                    } else if (episode.getDue() < 0) {
                                        break seriesLabel;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) { }

        if (!episodeList.isEmpty()) {
            NotificationUtil.createAiringNotification(getApplicationContext(), episodeList);
        }
    }
}
