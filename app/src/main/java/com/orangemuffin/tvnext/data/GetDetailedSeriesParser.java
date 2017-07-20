package com.orangemuffin.tvnext.data;

import android.util.Xml;

import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.TvSeries;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/* Created by OrangeMuffin on 3/14/2017 */
public class GetDetailedSeriesParser extends XmlParser {
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
            } else if (name.equals("Episode")) {
                readEpisode(parser, tvSeries);
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
            } else if (name.equals("poster")) {
                tvSeries.setPoster(readField("poster", parser));
            } else if (name.equals("SeriesName")) {
                tvSeries.setName(readField("SeriesName", parser));
            } else if (name.equals("Network")) {
                tvSeries.setNetwork(readField("Network", parser));
            } else if (name.equals("Overview")) {
                tvSeries.setDescription(readField("Overview", parser));
            } else if (name.equals("banner")) {
                tvSeries.setBanner(readField("banner", parser));
            } else if (name.equals("IMDB_ID")) {
                tvSeries.setImdbID(readField("IMDB_ID", parser));
            } else if (name.equals("Airs_DayOfWeek")) {
                tvSeries.setAirDay(readField("Airs_DayOfWeek", parser));
            } else if (name.equals("Genre")) {
                tvSeries.setGenre(readField("Genre", parser));
            } else if (name.equals("lastupdated")) {
                tvSeries.setLastUpdated(readField("lastupdated", parser));
            } else if (name.equals("FirstAired")) {
                tvSeries.setYear(readField("FirstAired", parser).substring(0,4));
            } else {
                skip(parser);
            }
        }
        return tvSeries;
    }

    private TvSeries readEpisode(XmlPullParser parser, TvSeries tvSeries) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "Episode");
        Episode episode = new Episode();
        String seasonNum = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("id")) {
                episode.setId(readField("id", parser));
            } else if (name.equals("EpisodeName")) {
                episode.setName(readField("EpisodeName", parser));
            } else if (name.equals("EpisodeNumber")) {
                episode.setEpisodeNum(readField("EpisodeNumber", parser));
            } else if (name.equals("FirstAired")) {
                episode.setAirdate(readField("FirstAired", parser));
            } else if (name.equals("SeasonNumber")) {
                seasonNum = readField("SeasonNumber", parser);
                episode.setSeasonNum(String.valueOf(seasonNum));
            } else if (name.equals("seriesid")) {
                episode.setSeriesId(readField("seriesid", parser));
            } else if (name.equals("Overview")) {
                episode.setOverview(readField("Overview", parser));
            } else {
                skip(parser);
            }
        }

        tvSeries.modifySeasons(seasonNum, episode);
        return tvSeries;
    }
}
