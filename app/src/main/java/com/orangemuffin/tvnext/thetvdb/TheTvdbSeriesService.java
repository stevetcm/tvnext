package com.orangemuffin.tvnext.thetvdb;

import com.orangemuffin.tvnext.fanarttv.FanartTvImagesService;
import com.orangemuffin.tvnext.models.TvSeries;
import com.uwetrottmann.thetvdb.entities.Actor;
import com.uwetrottmann.thetvdb.entities.ActorsResponse;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesImageQueryResult;
import com.uwetrottmann.thetvdb.entities.SeriesImageQueryResultResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResultsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/* Created by OrangeMuffin on 7/28/2017 */
public class TheTvdbSeriesService extends TheTvdbServiceBase {

    public static TvSeries getSeriesDetails(String seriesId, String language) throws Exception {
        Call<SeriesResponse> call = getTheTvdb().series().series(Integer.parseInt(seriesId), language);
        return convertSeriesModel(call.execute().body().data);
    }

    public static List<TvSeries> getSeriesSearch(String name, String language) throws Exception {
        Call<SeriesResultsResponse> call = getTheTvdb().search().series(name, null, null, language);
        List<Series> searchList = call.execute().body().data;
        List<TvSeries> seriesList = new ArrayList<>();
        for (Series series : searchList) {
            seriesList.add(convertSeriesModel(series));
        }
        return seriesList;
    }

    public static String getSeriesPoster(String seriesId) throws  Exception {
        String posterType = "poster";
        Call<SeriesImageQueryResultResponse> call_poster = getTheTvdb().series().imagesQuery
                (Integer.parseInt(seriesId), posterType, null, null, null);
        SeriesImageQueryResultResponse results = call_poster.execute().body();
        double poster_rating = 0;
        int poster_index = 0;
        for (int z = 0; z < results.data.size(); z++) {
            SeriesImageQueryResult poster = results.data.get(z);
            if (poster_rating < poster.ratingsInfo.average) {
                poster_rating = poster.ratingsInfo.average;
                poster_index = z;
            }
        }

        String urlBase = "http://thetvdb.com/banners/_cache/";
        return urlBase + results.data.get(poster_index).fileName;
    }

    public static List<com.orangemuffin.tvnext.models.Actor> getSeriesActor(String seriesId) throws Exception {
        List<com.orangemuffin.tvnext.models.Actor> actorList = new ArrayList<>();
        ActorsResponse response = executeCall(getTheTvdb().series().actors(Integer.parseInt(seriesId)));
        for (Actor actor : response.data) {
            actorList.add(convertActorModel(actor));
        }
        return actorList;
    }

    private static TvSeries convertSeriesModel(Series series) {
        TvSeries result = new TvSeries();
        result.setId(String.valueOf(series.id));
        result.setName(series.seriesName);
        result.setNetwork(series.network);

        String urlBase = "http://thetvdb.com/banners/";
        result.setBanner(urlBase + series.banner);

        result.setOverview(series.overview);
        result.setImdbID(series.imdbId);
        result.setAirDay(series.airsDayOfWeek);

        String genre_full = "";
        for (String genre : series.genre) {
            genre_full = genre_full + "|" + genre;
        }
        if (genre_full != null) {
            genre_full = genre_full + "|";
        }
        result.setGenre(genre_full);

        return result;
    }

    private static com.orangemuffin.tvnext.models.Actor convertActorModel(Actor actor) {
        com.orangemuffin.tvnext.models.Actor temp = new com.orangemuffin.tvnext.models.Actor();
        temp.setId(String.valueOf(actor));
        String urlBase = "http://thetvdb.com/banners/_cache/";
        temp.setImage(urlBase + actor.image);
        temp.setName(actor.name);
        temp.setRole(actor.role);
        temp.setSortOrder(actor.sortOrder);

        return temp;
    }
}
