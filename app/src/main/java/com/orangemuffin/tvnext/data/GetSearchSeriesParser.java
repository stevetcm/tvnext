package com.orangemuffin.tvnext.data;

import android.util.Xml;

import com.orangemuffin.tvnext.models.TvSeries;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 1/9/2017 */
public class GetSearchSeriesParser extends XmlParser {
    public List<TvSeries> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readData(parser);
        } finally {
            in.close();
        }
    }

    private List<TvSeries> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<TvSeries> entries = new ArrayList<TvSeries>();

        parser.require(XmlPullParser.START_TAG, namespace, "Data");
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();

            if (name.equals("Series")) {
                entries.add(readSeries(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private TvSeries readSeries(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "Series");
        TvSeries series = new TvSeries();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                series.setId(readField("id", parser));
            } else if (name.equals("banner")) {
                series.setBanner(readField("banner", parser));
            } else if (name.equals("SeriesName")) {
                series.setName(readField("SeriesName", parser));
            } else if (name.equals("Network")) {
                series.setNetwork(readField("Network", parser));
            } else if (name.equals("Overview")) {
                series.setDescription(readField("Overview", parser));
            } else {
                skip(parser);
            }
        }
        return series;
    }
}
