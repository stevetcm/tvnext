package com.orangemuffin.tvnext.datafetch;

import com.orangemuffin.tvnext.models.Actor;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbSeriesService;
import com.orangemuffin.tvnext.tmdb.TmdbSeriesService;
import com.orangemuffin.tvnext.trakt.TraktSeriesService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* Created by OrangeMuffin on 7/29/2017 */
public class TvSeriesDetailsFetch {

    public static TvSeries fetchData(String seriesId) {
        TvSeries tvSeries = new TvSeries();
        String s = null;

        try {
            tvSeries = TheTvdbSeriesService.getSeriesDetails(seriesId, "en");

            try {
                tvSeries.setPoster(TheTvdbSeriesService.getSeriesPoster(seriesId));
            } catch (Exception e) { }

            try {
                tvSeries.setSeasons(TraktSeriesService.getSeriesSeasons(tvSeries.getImdbID(), seriesId, tvSeries.getName(), tvSeries.getPoster()));
            } catch (Exception e) { }


            try {
                tvSeries.setBackgrounds(TmdbSeriesService.getSeriesBackgrounds(seriesId));
            } catch (Exception e) { }

            try {
                List<Actor> actorList = TheTvdbSeriesService.getSeriesActor(seriesId);
                Collections.sort(actorList, new Comparator<Actor>() {
                    @Override
                    public int compare(Actor actor, Actor actor2) {
                        return actor.getSortOrder() - actor2.getSortOrder();
                    }
                });
                tvSeries.setActors(actorList);
            } catch (Exception e) { }

            try {
                tvSeries.setTraktRating(TraktSeriesService.getSeriesRating(tvSeries.getImdbID()));
            } catch (Exception e) { }

            try {
                tvSeries.setLastUpdated(TraktSeriesService.getSeriesLastUpdate(tvSeries.getImdbID()));
            } catch (Exception e) { }
        } catch (Exception e) {
            tvSeries.setName(null);
            tvSeries.setGenre(s);
            return tvSeries;
        }

        return tvSeries;
    }
}
