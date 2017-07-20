package com.orangemuffin.tvnext.thetvdb;

import com.uwetrottmann.thetvdb.entities.Actor;
import com.uwetrottmann.thetvdb.entities.ActorsResponse;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResultsResponse;

import java.util.List;

import retrofit2.Call;

/* Created by OrangeMuffin on 5/26/2017 */
public class TheTvdbSeriesService extends TheTvdbServiceBase {

    public Series getSeriesDetails(int seriesId, String language) throws Exception {
        Call<SeriesResponse> call = getTheTvdb().series().series(seriesId, language);
        return call.execute().body().data;
    }

    public List<Series> getSeriesSearch(String name, String language) throws Exception {
        Call<SeriesResultsResponse> call = getTheTvdb().search().series(name, null, null, language);
        return call.execute().body().data;
    }

    public List<Actor> getSeriesActor(int seriesId) throws Exception {
        ActorsResponse response = executeCall(getTheTvdb().series().actors(seriesId));
        return response.data;
    }
}
