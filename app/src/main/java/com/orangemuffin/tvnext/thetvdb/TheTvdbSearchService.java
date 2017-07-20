package com.orangemuffin.tvnext.thetvdb;

import com.orangemuffin.tvnext.data.GetSearchSeriesParser;
import com.orangemuffin.tvnext.models.TvSeries;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 1/9/2017 */
public class TheTvdbSearchService extends TheTvdbServiceBase {
    private static final String URL = "http://www.thetvdb.com/api/GetSeries.php?seriesname=";

    public List<TvSeries> performSearch(String query, String language) {
        try {
            InputStream stream = null;
            List<TvSeries> seriesList = null;

            try {
                stream = downloadUrl(URL + query);

                GetSearchSeriesParser parser = new GetSearchSeriesParser();
                seriesList = parser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return seriesList;
        } catch (IOException e) {
            return new ArrayList<TvSeries>();
        } catch (XmlPullParserException e) {
            return new ArrayList<TvSeries>();
        }
    }
}
