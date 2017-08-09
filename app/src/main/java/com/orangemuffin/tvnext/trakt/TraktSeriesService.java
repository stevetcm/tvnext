package com.orangemuffin.tvnext.trakt;

import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbSeriesService;
import com.orangemuffin.tvnext.utils.DateAndTimeUtil;
import com.uwetrottmann.trakt5.entities.Ratings;
import com.uwetrottmann.trakt5.entities.Show;
import com.uwetrottmann.trakt5.enums.Extended;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* Created by OrangeMuffin on 7/28/2017 */
public class TraktSeriesService extends TraktServiceBase {

    private static String getCategoryShowsURL(String category, String page) {
        StringBuilder url = new StringBuilder();
        url.append(BASE_URL)
                .append("shows")
                .append("/")
                .append(category)
                .append("?page=" + page)
                .append("&limit=6")
                .append("&extended=full");
        return url.toString();
    }

    private static String getShowRatingURL(String id) {
        StringBuilder url = new StringBuilder();
        url.append(BASE_URL)
                .append("shows")
                .append("/")
                .append(id)
                .append("/ratings");
        return url.toString();
    }

    public static List<TvSeries> performCategoryTask(String category, String page) {
        try {
            InputStream stream = null;

            //must NOT be null to be able to add items
            List<String> ids = new ArrayList<String>();
            List<TvSeries> seriesList = new ArrayList<TvSeries>();

            //additional save in case TheTVDB entry is not available
            List<String> names = new ArrayList<String>();
            List<String> networks = new ArrayList<String>();

            try {
                stream = downloadUrl(getCategoryShowsURL(category, page));

                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //JSON Parser
                try {
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject show = jsonArray.getJSONObject(i).getJSONObject("show");

                        String id = show.getJSONObject("ids").getString("tvdb");
                        ids.add(id);
                        String name = show.getString("title");
                        names.add(name);
                        String network = show.getString("network");
                        networks.add(network);
                    }
                } catch (Exception e) { //slight text margin with different query
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {

                        String id = jsonArray.getJSONObject(i).getJSONObject("ids").getString("tvdb");
                        ids.add(id);
                        String name = jsonArray.getJSONObject(i).getString("title");
                        names.add(name);
                        String network = jsonArray.getJSONObject(i).getString("network");
                        networks.add(network);
                    }
                }

                for (int i = 0; i < ids.size(); i++) {
                    TvSeries tvSeries = new TvSeries();
                    try {
                        tvSeries = TheTvdbSeriesService.getSeriesDetails(ids.get(i), "en");
                    } catch (Exception e) {
                        //tvSeries.setTvdbready(false);
                        tvSeries.setId(ids.get(i));
                        tvSeries.setName(names.get(i));
                        tvSeries.setNetwork(networks.get(i));
                        tvSeries.setOverview("Information not available.");
                    }
                    seriesList.add(tvSeries);
                }

            }  finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return seriesList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static String getSeriesLastUpdate(String imdbId) throws Exception {
        Show show = executeCall(getTrakt().shows().summary(imdbId, Extended.FULL));
        try {
            String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            String convert = DateAndTimeUtil.setToLocalTimeZone(pattern, String.valueOf(show.updated_at));
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date temp = sdf.parse(convert);
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date_temp = sdf.format(temp);
            return date_temp;
        } catch (Exception e) { }
        return null;
    }

    public static String getSeriesRating(String imdbId) throws Exception {
        Ratings ratings = executeCall(getTrakt().shows().ratings(imdbId));
        return String.format("%.3g", ratings.rating) + "/10.0 (" + ratings.votes + " votes)";
    }

    public static List<Season> getSeriesSeasons(String imdbId, String seriesId, String seriesName, String seriesPoster) throws Exception {
        List<Season> seasonList = new ArrayList<>();
        int absoluteCount = 1;

        List<com.uwetrottmann.trakt5.entities.Season> seasons = executeCall(getTrakt().seasons().summary(imdbId, Extended.FULLEPISODES));
        for (com.uwetrottmann.trakt5.entities.Season season : seasons) {
            Season tempSeason = new Season();
            tempSeason.setSeasonNum(String.valueOf(season.number));

            for (com.uwetrottmann.trakt5.entities.Episode episode : season.episodes) {
                Episode tempEpisode = new Episode();

                if (episode.title == null && episode.first_aired == null) {
                    continue;
                }

                if (episode.title == null) {
                    tempEpisode.setName("TBA");
                } else {
                    tempEpisode.setName(episode.title);
                }
                tempEpisode.setEpisodeNum(String.valueOf(episode.number));
                tempEpisode.setSeasonNum(String.valueOf(episode.season));
                tempEpisode.setOverview(episode.overview);

                tempEpisode.setId(String.valueOf(episode.ids.tvdb));
                tempEpisode.setSeriesId(seriesId);
                tempEpisode.setSeriesName(seriesName);
                tempEpisode.setSeriesPoster(seriesPoster);

                if (episode.season == 0) {
                    tempEpisode.setAbsoluteNum(0);
                } else {
                    tempEpisode.setAbsoluteNum(absoluteCount);
                    absoluteCount++;
                }

                try {
                    String pattern = "yyyy-MM-dd'T'HH:mm'Z'";
                    String convert = DateAndTimeUtil.setToLocalTimeZone(pattern, String.valueOf(episode.first_aired));
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    Date temp = sdf.parse(convert);
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String date_temp = sdf.format(temp);
                    tempEpisode.setAirdate(date_temp);
                } catch (Exception e) {
                    tempEpisode.setAirdate(null);
                }

                tempSeason.addEpisode(tempEpisode);
            }

            seasonList.add(tempSeason);
        }

        return seasonList;
    }
}
