package com.orangemuffin.tvnext.tmdb;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/* Created by OrangeMuffin on 2/13/2017 */
public class TmdbFindExtService extends TmdbServiceBase {

    private static final String BASE_URL = "https://api.themoviedb.org/3/find/";

    public TmdbFindExtService() {}

    public static String getTvShowUrl(String id) {
        StringBuilder url = new StringBuilder();
        url.append(BASE_URL)
                .append(id)
                .append("?api_key="+API_KEY)
                .append("&external_source=tvdb_id");
        return url.toString();
    }

    public String performTask(String id) {
        try {
            InputStream stream = null;

            String tmdbID = null;

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
                    JSONArray tv_results = results.getJSONArray("tv_results");
                    tmdbID = tv_results.getJSONObject(0).getString("id");
                    return tmdbID;
                } catch (Exception e) { }
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return tmdbID;
        } catch (Exception e) {
            return null;
        }
    }
}