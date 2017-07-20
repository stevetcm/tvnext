package com.orangemuffin.tvnext.trakt;

import com.orangemuffin.tvnext.models.TvSeries;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/* Created by OrangeMuffin on 3/18/2017 */
public class TraktRatingService extends TraktServiceBase {

    private static final String BASE_URL = "https://api-v2launch.trakt.tv/";

    public static String getShowsURL(String id) {
        StringBuilder url = new StringBuilder();
        url.append(BASE_URL)
                .append("shows")
                .append("/")
                .append(id)
                .append("/ratings");
        return url.toString();
    }

    public TvSeries performTask(String id, TvSeries tvSeries) {
        try {
            InputStream stream = null;

            try {
                stream = downloadUrl(getShowsURL(id));

                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //JSON PARSER
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    double rating = (double) Float.parseFloat(jsonObject.getString("rating"));
                    tvSeries.setTraktRating(String.format("%.3g", rating));
                    tvSeries.setTraktVotes(jsonObject.getString("votes"));
                } catch (Exception e) {
                }
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            return tvSeries;

        } catch (Exception e) {
            return new TvSeries();
        }
    }
}
