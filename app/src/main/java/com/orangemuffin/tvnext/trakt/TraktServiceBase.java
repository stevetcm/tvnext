package com.orangemuffin.tvnext.trakt;

import com.uwetrottmann.trakt5.TraktV2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Response;

/* Created by OrangeMuffin on 7/28/2017 */
public class TraktServiceBase {
    protected static final String CLIENT_ID = "TRAKT_CLIENT_ID_HERE";
    protected static final String CLIENTSECRET = "TRAKT_CLIENT_SECRET_HERE";
    protected static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"; //workaround to redirect to app
    protected static final String BASE_URL = "https://api-v2launch.trakt.tv/";

    private static final TraktV2 trakt = new TraktV2(CLIENT_ID, CLIENTSECRET, REDIRECT_URI);

    protected static final TraktV2 getTrakt() {
        return trakt;
    }

    public static <T> T executeCall(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        return null;
    }

    protected static InputStream downloadUrl(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("trakt-api-version", "2");
        conn.setRequestProperty("trakt-api-key", CLIENT_ID);

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(30000 /* milliseconds */);

        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            return conn.getInputStream();
        }

        return conn.getErrorStream();
    }
}
