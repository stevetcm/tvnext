package com.orangemuffin.tvnext.thetvdb;

import com.uwetrottmann.thetvdb.TheTvdb;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Response;

/* Created by OrangeMuffin on 1/9/2017 */
public class TheTvdbServiceBase {
    protected static final String API_KEY = "PASTE API KEY HERE";

    private static final TheTvdb theTvdb = new TheTvdb(API_KEY);

    public TheTvdbServiceBase() { }

    protected final TheTvdb getTheTvdb() {
        return theTvdb;
    }

    public <T> T executeCall(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        return null;
    }

    protected InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(30000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
