package com.orangemuffin.tvnext.tmdb;

import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.uwetrottmann.tmdb2.entities.FindResults;
import com.uwetrottmann.tmdb2.entities.Image;
import com.uwetrottmann.tmdb2.entities.Images;
import com.uwetrottmann.tmdb2.entities.TvEpisode;
import com.uwetrottmann.tmdb2.entities.TvSeason;
import com.uwetrottmann.tmdb2.enumerations.ExternalSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/* Created by OrangeMuffin on 7/30/2017 */
public class TmdbSeriesService extends TmdbServiceBase {

    public static int getExternalId(String seriesId) throws Exception {
        Call<FindResults> callFind = getTmdb().findService().find(seriesId, ExternalSource.TVDB_ID, null);
        FindResults results = callFind.execute().body();

        return results.tv_results.get(0).id;
    }

    public static Season getSeriesSeasons(int seasonNum, String[] seriesInfo) throws Exception {
        int tmdbId = getExternalId(seriesInfo[0]);
        Call<TvSeason> call = getTmdb().tvSeasonsService().season(tmdbId, seasonNum, null, null);
        return convertSeriesSeason(call.execute().body(), seriesInfo[0], seriesInfo[1], seriesInfo[2]);
    }

    public static List<String> getSeriesBackgrounds(String seriesId) throws Exception {
        int tmdbId = getExternalId(seriesId);
        Call<Images> call = getTmdb().tvService().images(tmdbId, null);
        Images images = call.execute().body();

        List<String> backgrounds = new ArrayList<>();
        int max_size = 7;
        if (images.backdrops.size() < max_size) {
            max_size = images.backdrops.size();
        }
        for (int i = 0; i < max_size; i++) {
            String urlBase = "https://image.tmdb.org/t/p/w780";
            backgrounds.add(urlBase + images.backdrops.get(i).file_path);
        }

        return backgrounds;
    }

    private static Season convertSeriesSeason(TvSeason tvSeason, String seriesId, String seriesName, String seriesPoster) {
        Season season = new Season();
        season.setSeasonNum(String.valueOf(tvSeason.season_number));

        for (TvEpisode tvEpisode : tvSeason.episodes) {
            Episode episode = new Episode();
            episode.setId(String.valueOf(tvEpisode.id));
            episode.setName(tvEpisode.name);
            episode.setEpisodeNum(String.valueOf(tvEpisode.episode_number));

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            String date_temp = dateFormat.format(tvEpisode.air_date);
            episode.setAirdate(date_temp);

            episode.setSeasonNum(String.valueOf(tvEpisode.season_number));
            episode.setSeriesId(seriesId);
            episode.setOverview(tvEpisode.overview);

            episode.setSeriesName(seriesName);
            episode.setSeriesPoster(seriesPoster);
            season.addEpisode(episode);
        }

        return season;
    }
}
