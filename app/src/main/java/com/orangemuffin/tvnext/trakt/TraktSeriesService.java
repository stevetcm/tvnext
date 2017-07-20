package com.orangemuffin.tvnext.trakt;

import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbInfoService;
import com.orangemuffin.tvnext.thetvdb.TheTvdbSeriesService;
import com.uwetrottmann.thetvdb.TheTvdb;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/*  Created by OrangeMuffin on 3/13/2017 */
public class TraktSeriesService extends TraktServiceBase {

    private static final String BASE_URL = "https://api-v2launch.trakt.tv/";

    public TheTvdb theTvdb = new TheTvdb("PASTE API KEY HERE");

    public static String getShowsURL(String category, String page) {
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

    public List<TvSeries> performTask(String category, String page) {
        try {
            InputStream stream = null;

            //must NOT be null to be able to add items
            List<String> ids = new ArrayList<String>();
            List<TvSeries> seriesList = new ArrayList<TvSeries>();

            //additional save in case TheTVDB entry is not available
            List<String> names = new ArrayList<String>();
            List<String> networks = new ArrayList<String>();

            try {
                stream = downloadUrl(getShowsURL(category, page));

                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //JSON PARSER
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
                        TheTvdbSeriesService seriesService = new TheTvdbSeriesService();
                        Series series = seriesService.getSeriesDetails(Integer.parseInt(ids.get(i)), "en");

                        tvSeries.setId(String.valueOf(series.id));
                        tvSeries.setBanner(series.banner);
                        tvSeries.setName(series.seriesName);
                        tvSeries.setNetwork(series.network);
                        tvSeries.setDescription(series.overview);
                    } catch (Exception e) {
                        tvSeries.setTvdbready(false);
                        tvSeries.setId(ids.get(i));
                        tvSeries.setName(names.get(i));
                        tvSeries.setNetwork(networks.get(i));
                        tvSeries.setDescription("Information not available.");
                    }

                    seriesList.add(tvSeries);
                }
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return seriesList;
        } catch (Exception e) {
            return new ArrayList<TvSeries>();
        }
    }
}
