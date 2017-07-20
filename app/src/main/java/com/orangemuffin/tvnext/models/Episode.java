package com.orangemuffin.tvnext.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/* Created by OrangeMuffin on 3/16/2017 */
public class Episode implements Serializable {
    private String id;
    private String name;
    private String seriesName;
    private String seriesId;
    private String overview;
    private String seriesPoster;
    private String episodeNum;
    private String airdate;
    private String seasonNum;
    private int absoluteNum;
    private boolean watched = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesPoster() {
        return seriesPoster;
    }

    public void setSeriesPoster(String seriesPoster) {
        this.seriesPoster = seriesPoster;
    }

    public String getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(String episodeNum) {
        this.episodeNum = episodeNum;
    }

    public String getAirdate() {
        return airdate;
    }

    public void setAirdate(String airdate) {
        if (airdate == null || airdate.equals("")) {
            this.airdate = "unknown";
        } else {
            this.airdate = airdate;
        }
    }

    public String getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(String seasonNum) {
        this.seasonNum = seasonNum;
    }

    public int getDue() {
        if (!getAirdate().equals("unknown")) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            Calendar calendar = Calendar.getInstance();
            String today = dateFormat.format(calendar.getTime());
            try {
                Date one = dateFormat.parse(today);
                Date two = dateFormat.parse(this.airdate);
                long diff = two.getTime() - one.getTime();
                return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    public int getNegativeDue() {
        if (!getAirdate().equals("unknown")) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            Calendar calendar = Calendar.getInstance();
            String today = dateFormat.format(calendar.getTime());
            try {
                Date two = dateFormat.parse(today);
                Date one = dateFormat.parse(this.airdate);
                long diff = two.getTime() - one.getTime();
                return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    public int getAbsoluteNum() {
        return absoluteNum;
    }

    public void setAbsoluteNum(int absoluteNum) {
        this.absoluteNum = absoluteNum;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
