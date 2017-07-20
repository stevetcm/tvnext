package com.orangemuffin.tvnext.tmdb;

import com.uwetrottmann.tmdb2.Tmdb;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* Created by OrangeMuffin on 1/31/2017 */
public class TmdbServiceBase {
    protected static final String API_KEY = "PASTE API KEY HERE";

    private static final Tmdb tmdb = new Tmdb(API_KEY);

    public TmdbServiceBase() { }

    protected final Tmdb getTmdb() { return tmdb; }

    protected InputStream downloadUrl(String urlString) throws IOException {
        URL obj = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(30000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            return conn.getInputStream();
        }

        return conn.getErrorStream();
    }
}
