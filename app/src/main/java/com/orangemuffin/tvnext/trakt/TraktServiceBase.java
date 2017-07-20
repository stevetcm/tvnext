package com.orangemuffin.tvnext.trakt;

import com.uwetrottmann.trakt5.TraktV2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Response;

/* Created by OrangeMuffin on 1/21/2017 */
public class TraktServiceBase {
    private static final String CLIENT_ID = "PASTE CLIENT ID HERE";
    private static final String CLIENTSECRET = "PASTE CLIENT SECRET HERE";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"; //workaround to redirect to app
    private static final String TRAKT_BASE_URL = "https://api-v2launch.trakt.tv/";
    private static final TraktV2 traktV2 = new TraktV2(CLIENT_ID, CLIENTSECRET, REDIRECT_URI);

    public TraktServiceBase() { }

    protected final TraktV2 getTraktV2() { return traktV2; }

    public <T> T executeCall(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        return null;
    }

    protected InputStream downloadUrl(String urlString) throws IOException {
        URL obj = new URL(urlString);
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
