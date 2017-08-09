package com.orangemuffin.tvnext.models;

import com.orangemuffin.tvnext.utils.DateAndTimeUtil;
import com.orangemuffin.tvnext.utils.StringFormatUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Created by OrangeMuffin on 7/28/2017 */
public class TvSeries implements Serializable {
    private String id;
    private String name;
    private String banner;
    private String network;
    private String poster;
    private String overview;
    private String imdbID;
    private String airDay;
    private String genre;
    private String lastUpdated;

    private List<Season> seasons = new ArrayList<>();
    private List<String> backgrounds = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();

    private String TraktRating;

    private List<Integer> watchedIndex = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getAirDay() {
        return airDay;
    }

    public void setAirDay(String airDay) {
        this.airDay = airDay;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public List<String> getBackgrounds() {
        return backgrounds;
    }

    public void setBackgrounds(List<String> backgrounds) {
        this.backgrounds = backgrounds;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public String getTraktRating() {
        return TraktRating;
    }

    public void setTraktRating(String traktRating) {
        TraktRating = traktRating;
    }

    public String getNearestEpisode() {
        for (int i = seasons.size()-1; i >= 0; i--) {
            Season season = seasons.get(i);
            if (!season.getSeasonNum().equals("0")) {
                List<Episode> episodes = season.getEpisodes();
                for (int j = 0; j < episodes.size(); j++) {
                    Episode episode = episodes.get(j);
                    if (episode.getDue() != null && episode.getDue() >= 0) {
                        String episodeString = "S" + StringFormatUtil.prefixNumber(Integer.parseInt(episode.getSeasonNum())) +
                            "E" + StringFormatUtil.prefixNumber(Integer.parseInt(episode.getEpisodeNum()));
                        episodeString = episodeString + " - " + episode.getName() + "\n";

                        episodeString = episodeString + "Airs: " + StringFormatUtil.getDueDate(episode.getDue()) +
                            ", " + DateAndTimeUtil.convertDate("EEE, dd MMM yyyy", episode.getAirdate());

                        return episodeString;
                    }
                }
            }
        }

        return "No Upcoming Episode";
    }

    public Episode getCurrentEpisode(int offset) {
        int tempIndex = 0;
        if (!watchedIndex.isEmpty()) {
            tempIndex = watchedIndex.get(0) + offset;
        }

        for (int i = 0; i < seasons.size(); i++) {
            Season season = seasons.get(i);
            if (!season.getSeasonNum().equals("0")) {
                if (season.getEpisodes().size() > tempIndex) {
                    return season.getEpisodes().get(tempIndex);
                } else {
                    tempIndex = tempIndex - season.getEpisodes().size();
                }
            }
        }

        return null;
    }

    public String getCurrentString() {
        Episode episode = getCurrentEpisode(0);
        if (episode != null) {
            String status = "Aired";
            if (episode.getDue() == null) {
                status = "TBA";
            } else if (episode.getDue() >= -1) {
                status = StringFormatUtil.getDueDate(episode.getDue());
            }

            String episodeString = "S" + StringFormatUtil.prefixNumber(Integer.parseInt(episode.getSeasonNum())) +
                    "E" + StringFormatUtil.prefixNumber(Integer.parseInt(episode.getEpisodeNum()));

            return episodeString + " (" + status + ")";
        }

        if (seasons.size() == 0) {
            return "TBA";
        } else if (seasons.size() == 1 && seasons.get(0).getSeasonNum().equals("0")) {
            return "TBA";
        } else {
            return "Completed";
        }
    }

    public List<Integer> getWatchedIndex() {
        return watchedIndex;
    }

    public void setWatchedIndex(List<Integer> watchedIndex) {
        this.watchedIndex = watchedIndex;
    }

    public void setWatched(String seasonNum, int absoluteNum) {
        if (absoluteNum > 0) {
            for (Season season : seasons) {
                if (season.getSeasonNum().equals(seasonNum)) {
                    season.setWatched(absoluteNum);
                    break;
                }
            }
            watchedIndex.add(absoluteNum);
            Collections.sort(watchedIndex);
            Collections.reverse(watchedIndex);
        } else if (absoluteNum == -1) {
            Episode episode = getCurrentEpisode(0);
            if (episode != null) {
                if (episode.getDue() != null && episode.getDue() <= 0) {
                    setWatched(episode.getSeasonNum(), episode.getAbsoluteNum());
                }
            }
        } else if (absoluteNum == -2) {
            for (Season season : seasons) {
                if (season.getSeasonNum().equals(seasonNum)) {
                    for (Episode episode : season.getEpisodes()) {
                        if (episode.getDue() != null && episode.getDue() <= 0) {
                            if (!season.getWatchedIndex().contains(episode.getAbsoluteNum())) {
                                setWatched(seasonNum, episode.getAbsoluteNum());
                            }
                        }
                    }
                    Collections.sort(watchedIndex);
                    Collections.reverse(watchedIndex);
                    break;
                }
            }
        }
    }

    public void setUnwatched(String seasonNum, int absoluteNum) {
        if (!watchedIndex.isEmpty()) {
            if (absoluteNum > 0) {
                for (Season season : seasons) {
                    if (season.getSeasonNum().equals(seasonNum)) {
                        season.setUnwatched(absoluteNum);
                        break;
                    }
                }
                watchedIndex.remove(new Integer(absoluteNum));
            } else if (absoluteNum == -1) {
                Episode episode = getCurrentEpisode(-1);
                setUnwatched(episode.getSeasonNum(), episode.getAbsoluteNum());
            } else if (absoluteNum == -2) {
                for (Season season : seasons) {
                    if (season.getSeasonNum().equals(seasonNum)) {
                        for (Episode episode : season.getEpisodes()) {
                            season.setUnwatched(episode.getAbsoluteNum());
                            watchedIndex.remove(new Integer(episode.getAbsoluteNum()));
                        }
                        break;
                    }
                }
            }
        }
    }
}
