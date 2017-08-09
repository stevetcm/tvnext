package com.orangemuffin.tvnext.fanarttv;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* Created by OrangeMuffin on 7/28/2017 */
public class FanartTvServiceBase {
    protected static final String API_KEY = "ebcbed629e57a70f8cfe2748de97b9b9";

    protected static final String SEARCH_LINK = "http://webservice.fanart.tv/v3/tv/";

    protected static InputStream downloadUrl(String id) throws IOException {
        String urlString = SEARCH_LINK + id + "?api_key=" + API_KEY;
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
