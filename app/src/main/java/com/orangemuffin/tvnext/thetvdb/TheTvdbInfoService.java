package com.orangemuffin.tvnext.thetvdb;

import com.orangemuffin.tvnext.data.GetInfoSeriesParser;
import com.orangemuffin.tvnext.models.TvSeries;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/* Created by OrangeMuffin on 3/13/2017 */
public class TheTvdbInfoService extends TheTvdbServiceBase {
    private static final String URL = "http://thetvdb.com/api/" + API_KEY + "/series/";

    public TvSeries getSeriesInfo(String seriesId) {
        try {
            InputStream stream = null;
            TvSeries tvSeries;
            try {
                stream = downloadUrl(URL + seriesId + "/all/en.xml");

                GetInfoSeriesParser parser = new GetInfoSeriesParser();
                tvSeries = parser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return tvSeries;
        } catch (IOException e) {
            return new TvSeries();
        }catch (XmlPullParserException e) {
            return new TvSeries();
        }
    }
}
