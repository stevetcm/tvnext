package com.orangemuffin.tvnext.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 7/30/2017 */
public class Season implements Serializable {
    private String seasonNum;
    private List<Episode> episodes = new ArrayList<>();
    private List<Integer> watchedIndex = new ArrayList<>();

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

    public List<Integer> getWatchedIndex() {
        return watchedIndex;
    }

    public void setWatchedIndex(List<Integer> watchedIndex) {
        this.watchedIndex = watchedIndex;
    }

    public void setWatched(int absoluteNum) {
        watchedIndex.add(absoluteNum);
    }

    public void setUnwatched(int absoluteNum) {
        watchedIndex.remove(new Integer(absoluteNum));
    }

    public String getNumWatched() {
        return watchedIndex.size() + "/" + episodes.size();
    }
}
