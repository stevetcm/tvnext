package com.orangemuffin.tvnext.data;

import android.util.Xml;

import com.orangemuffin.tvnext.models.Actor;
import com.orangemuffin.tvnext.models.TvSeries;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/* Created by OrangeMuffin on 2/5/2017 */
public class GetActorsDetailsParser extends XmlParser {
    public TvSeries parse(InputStream in, TvSeries tvSeries) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readData(parser, tvSeries);
        } finally {
            in.close();
        }
    }

    private TvSeries readData(XmlPullParser parser, TvSeries tvSeries) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "Actors");

        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();

            if (name.equals("Actor")) {
                readSeries(parser, tvSeries);
            } else {
                skip(parser);
            }
        }
        return tvSeries;
    }

    private TvSeries readSeries(XmlPullParser parser, TvSeries tvSeries) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "Actor");
        Actor actor = new Actor();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("id")) {
                actor.setId(readField("id", parser));
            } else if (name.equals("Image")) {
                String urlBase = "http://thetvdb.com/banners/_cache/";
                actor.setImage(urlBase + readField("Image", parser));
            } else if (name.equals("Name")) {
                actor.setName(readField("Name", parser));
            } else if (name.equals("Role")) {
                actor.setRole(readField("Role", parser));
            } else {
                skip(parser);
            }
        }

        tvSeries.addActor(actor);
        return tvSeries;
    }
}
