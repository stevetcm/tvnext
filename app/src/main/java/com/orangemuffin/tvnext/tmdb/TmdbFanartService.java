package com.orangemuffin.tvnext.tmdb;

import com.orangemuffin.tvnext.models.TvSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*  Created by OrangeMuffin on 2/11/2017 */
public class TmdbFanartService extends TmdbServiceBase {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    public TmdbFanartService() {}

    public static String getTvShowUrl(String id) {
        StringBuilder url = new StringBuilder();
        url.append(BASE_URL)
                .append("tv/")
                .append(id)
                .append("/images?")
                .append("api_key="+API_KEY)
                .append("&language=en,null");
        return url.toString();
    }

    public TvSeries performTvShowTask(String id, TvSeries tvSeries) {
        try {
            InputStream stream = null;

            //must NOT be null to be able to add items
            List<String> fanartList = new ArrayList<>();

            try {
                stream = downloadUrl(getTvShowUrl(id));

                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                try {
                    JSONObject results = new JSONObject(response.toString());
                    JSONArray backdrops = results.getJSONArray("backdrops");
                    int count = 7;
                    if (backdrops.length() < 7) {
                        count = backdrops.length();
                    }
                    for (int i = 0; i < count; i++) {
                        String urlBase = "https://image.tmdb.org/t/p/w780";
                        tvSeries.addFanart(urlBase + backdrops.getJSONObject(i).getString("file_path"));
                    }
                    return tvSeries;
                } catch (Exception e) { }
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return tvSeries;
        } catch (Exception e) {
            return tvSeries;
        }
    }
}
