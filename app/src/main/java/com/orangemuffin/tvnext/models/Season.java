package com.orangemuffin.tvnext.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* Created by OrangeMuffin on 3/16/2017 */
public class Season implements Serializable {
    private String seasonNum;
    private List<Episode> episodes = new ArrayList<>();
    private int watched = 0;

    public String getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(String seasonNum) {
        this.seasonNum = seasonNum;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }

    public int getWatchedVar() {
        return watched;
    }

    public void setWatchedVar(int watched) {
        this.watched = watched;
    }

    public void setWatched() {
        this.watched++;
    }

    public void setUnwatched() {
        this.watched--;
    }
}
