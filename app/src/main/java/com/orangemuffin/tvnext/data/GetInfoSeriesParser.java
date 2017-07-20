package com.orangemuffin.tvnext.data;

import android.util.Xml;

import com.orangemuffin.tvnext.models.TvSeries;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/* Created by OrangeMuffin on 3/13/2017 */
public class GetInfoSeriesParser extends XmlParser {
    public TvSeries parse(InputStream in) throws XmlPullParserException, IOException {
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

    private TvSeries readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        TvSeries tvSeries = new TvSeries();

        parser.require(XmlPullParser.START_TAG, namespace, "Data");
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();

            if (name.equals("Series")) {
                readSeries(parser, tvSeries);
            } else {
                skip(parser);
            }
        }
        return tvSeries;
    }

    private TvSeries readSeries(XmlPullParser parser, TvSeries tvSeries) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "Series");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("id")) {
                tvSeries.setId(readField("id", parser));
            } else if (name.equals("banner")) {
                tvSeries.setBanner(readField("banner", parser));
            } else if (name.equals("SeriesName")) {
                tvSeries.setName(readField("SeriesName", parser));
            } else if (name.equals("Network")) {
                tvSeries.setNetwork(readField("Network", parser));
            } else if (name.equals("Overview")) {
                tvSeries.setDescription(readField("Overview", parser));
            } else if (name.equals("lastupdated")) {
                tvSeries.setLastUpdated(readField("lastupdated", parser));
            } else {
                skip(parser);
            }
        }
        return tvSeries;
    }
}
