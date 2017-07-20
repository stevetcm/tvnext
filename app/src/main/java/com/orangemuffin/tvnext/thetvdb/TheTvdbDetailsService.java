package com.orangemuffin.tvnext.thetvdb;

import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.tmdb.TmdbFanartService;
import com.orangemuffin.tvnext.tmdb.TmdbFindExtService;
import com.orangemuffin.tvnext.trakt.TraktRatingService;
import com.uwetrottmann.thetvdb.entities.Actor;
import com.uwetrottmann.thetvdb.entities.ActorsResponse;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesImageQueryResult;
import com.uwetrottmann.thetvdb.entities.SeriesImageQueryResultResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.TvEpisode;
import com.uwetrottmann.tmdb2.entities.TvSeason;

import java.text.SimpleDateFormat;

import retrofit2.Call;

/* Created by OrangeMuffin on 3/14/2017 */
public class TheTvdbDetailsService extends TheTvdbServiceBase {
    private static final String URL = "http://thetvdb.com/api/" + API_KEY + "/series/";

    public TvSeries getSeriesDetails(String seriesId) {
        TvSeries tvSeries = new TvSeries();
        try {
            Call<SeriesResponse> call_details = getTheTvdb().series().series(Integer.parseInt(seriesId), "en");
            Series series = call_details.execute().body().data;

            tvSeries.setId(String.valueOf(series.id));
            tvSeries.setName(series.seriesName);
            tvSeries.setNetwork(series.network);
            tvSeries.setDescription(series.overview);
            tvSeries.setBanner(series.banner);
            tvSeries.setImdbID(series.imdbId);
            tvSeries.setAirDay(series.airsDayOfWeek);

            String genre_full = "";
            for (String genre : series.genre) {
                genre_full = genre_full + "|" + genre;
            }
            if (genre_full != null) {
                genre_full = genre_full + "|";
            }
            tvSeries.setGenre(genre_full);

            tvSeries.setLastUpdated(String.valueOf(series.lastUpdated));

            if (!series.firstAired.equals("")) {
                tvSeries.setYear(series.firstAired.substring(0, 4));
            }

            try {
                String posterType = "poster";
                Call<SeriesImageQueryResultResponse> call_poster = getTheTvdb().series().imagesQuery(Integer.parseInt(seriesId),
                        posterType, null, null, null);
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
                tvSeries.setPoster(results.data.get(poster_index).fileName);
            } catch (Exception e) { }


            TmdbFindExtService findExtService = new TmdbFindExtService();
            String tmdbID = findExtService.performTask(tvSeries.getId());

            Tmdb tmdb = new Tmdb("bef32a8bb1b778e10d53b177ee7f8d11");

            try {
                Call<TvSeason> call_season_zero = tmdb.tvSeasonsService().season(Integer.parseInt(tmdbID), 0, null, null);
                TvSeason season_zero = call_season_zero.execute().body();
                for (TvEpisode episode : season_zero.episodes) {
                    com.orangemuffin.tvnext.models.Episode temp = new com.orangemuffin.tvnext.models.Episode();
                    temp.setId(String.valueOf(episode.id));
                    temp.setName(episode.name);
                    temp.setEpisodeNum(String.valueOf(episode.episode_number));

                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                    String date_temp = dateFormat.format(episode.air_date);

                    temp.setAirdate(date_temp);
                    temp.setSeasonNum(String.valueOf(episode.season_number));
                    temp.setSeriesId(seriesId);
                    temp.setOverview(episode.overview);
                    tvSeries.modifySeasons(String.valueOf(episode.season_number), temp);
                }
            } catch (Exception e) { }

            try {
                for (int i = 1; i < 100; i++) {
                    Call<TvSeason> call_season = tmdb.tvSeasonsService().season(Integer.parseInt(tmdbID), i, null, null);
                    TvSeason season = call_season.execute().body();
                    for (TvEpisode episode : season.episodes) {
                        com.orangemuffin.tvnext.models.Episode temp = new com.orangemuffin.tvnext.models.Episode();
                        temp.setId(String.valueOf(episode.id));
                        temp.setName(episode.name);
                        temp.setEpisodeNum(String.valueOf(episode.episode_number));

                        String pattern = "yyyy-MM-dd";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                        String date_temp = dateFormat.format(episode.air_date);

                        temp.setAirdate(date_temp);
                        temp.setSeasonNum(String.valueOf(episode.season_number));
                        temp.setSeriesId(seriesId);
                        temp.setOverview(episode.overview);
                        tvSeries.modifySeasons(String.valueOf(episode.season_number), temp);
                    }
                }
            } catch (Exception e) { }

            try {
                ActorsResponse response_actors = executeCall(getTheTvdb().series().actors(Integer.parseInt(seriesId)));
                for (Actor actor : response_actors.data) {
                    com.orangemuffin.tvnext.models.Actor current = new com.orangemuffin.tvnext.models.Actor();
                    current.setId(String.valueOf(actor.id));
                    String urlBase = "http://thetvdb.com/banners/_cache/";
                    current.setImage(urlBase + actor.image);
                    current.setName(actor.name);
                    current.setRole(actor.role);
                    current.setSortOrder(actor.sortOrder);
                    tvSeries.addActor(current);
                }
            } catch (Exception e) { }

            TraktRatingService traktRatingService = new TraktRatingService();
            tvSeries = traktRatingService.performTask(tvSeries.getImdbID(), tvSeries);

            TmdbFanartService fanartService = new TmdbFanartService();
            tvSeries = fanartService.performTvShowTask(tmdbID, tvSeries);
            return tvSeries;
        } catch (Exception e) {
            //if any exception got through just return what was currently retrieved
            return tvSeries;
        }
    }
}
