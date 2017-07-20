package com.orangemuffin.tvnext.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.uwetrottmann.thetvdb.entities.Series;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* Created by OrangeMuffin on 3/12/2017 */
public class TvSeries implements Serializable {
    private String id;
    private String name;
    private String banner;
    private String network;
    private String poster;
    private String airDay;
    private String description;
    private String genre;
    private String imdbID;
    private String lastUpdated;

    private String year = "0000";

    private String TraktRating;
    private String TraktVotes;

    private List<Season> seasons = new ArrayList<>();
    private List<String> fanarts = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();

    private int absoluteCount = 1;
    private int watched = 1;
    private List<Integer> watchedIndex = new ArrayList<>();
    private int absoluteIndex = 0;
    private int numEpisodes = 0;

    private boolean tvdbready = true;

    private String placeholder;

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getAirDay() {
        return airDay;
    }

    public void setAirDay(String airDay) {
        this.airDay = airDay;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public String getTraktRating() {
        return TraktRating;
    }

    public void setTraktRating(String traktRating) {
        TraktRating = traktRating;
    }

    public String getTraktVotes() {
        return TraktVotes;
    }

    public void setTraktVotes(String traktVotes) {
        TraktVotes = traktVotes;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isTvdbready() {
        return tvdbready;
    }

    public void setTvdbready(boolean tvdbready) {
        this.tvdbready = tvdbready;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void modifySeasons(String seasonNum, Episode episode) {
        episode.setSeriesId(id);
        episode.setSeriesName(name);
        episode.setSeriesPoster(poster);
        if (!seasonNum.equals("0")) {
            episode.setAbsoluteNum(absoluteCount);
            absoluteCount++;
        }

        if (seasons.size() != 0) {
            boolean seasonExist = false;
            for (int i = 0; i < seasons.size(); i++) {
                if (seasons.get(i).getSeasonNum().equals(seasonNum)) {
                    seasons.get(i).addEpisode(episode);
                    seasonExist = true;
                } else {
                    seasonExist = false;
                }
            }
            if (!seasonExist) {
                Season season = new Season();
                season.setSeasonNum(seasonNum);
                season.addEpisode(episode);
                seasons.add(season);
            }
        } else {
            Season season = new Season();
            season.setSeasonNum(seasonNum);
            season.addEpisode(episode);
            seasons.add(season);
        }
    }

    public void addFanart(String fanart) {
        this.fanarts.add(fanart);
    }

    public List<String> getFanarts() {
        return fanarts;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void addActor(Actor actor) {
        this.actors.add(actor);
    }

    public String getNearestEpisode(String stage) {
        for (int i = 0; i < seasons.size(); i++) {
            if (!seasons.get(i).getSeasonNum().equals("0")) {
                List<Episode> episodes = seasons.get(i).getEpisodes();
                for (int j = 0; j < episodes.size(); j++) {
                    if (episodes.get(j).getDue() >= 0 || episodes.get(j).getAirdate().equals("unknown")) {
                        Episode episode = episodes.get(j);
                        String seasonNum = episode.getSeasonNum();
                        String episodeNum = episode.getEpisodeNum();
                        String status;

                        if (episode.getAirdate().equals("unknown")) {
                            status = " (TBA)";
                        } else if (episode.getDue() == 0) {
                            status = " (Today)";
                        } else if (episode.getDue() == 1) {
                            status = " (Tomorrow)";
                        } else if (episode.getDue() > 1) {
                            status = " (In " + String.valueOf(episode.getDue()) + " Days)";
                        } else {
                            status = " (Aired)";
                        }

                        if (stage.equals("Details")) {
                            if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) < 10) {
                                return "S0" + seasonNum + "E0" + episodeNum + status;
                            } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) < 10) {
                                return "S" + seasonNum + "E0" + episodeNum + status;
                            } else if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) >= 10) {
                                return "S0" + seasonNum + "E" + episodeNum + status;
                            } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) >= 10) {
                                return "S" + seasonNum + "E" + episodeNum + status;
                            }
                        } else if (stage.equals("overview1")) {
                            if (episode.getDue() >= 0 || episode.getAirdate().equals("unknown")) {
                                String episodeName = "'" + episode.getName() + "'";

                                if (episodeName.equals("''")) {
                                    episodeName = "TBA";
                                }

                                if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) < 10) {
                                    return "S0" + seasonNum + "E0" + episodeNum + " - " + episodeName;
                                } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) < 10) {
                                    return "S" + seasonNum + "E0" + episodeNum + " - " + episodeName;
                                } else if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) >= 10) {
                                    return "S0" + seasonNum + "E" + episodeNum + " - " + episodeName;
                                } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) >= 10) {
                                    return "S" + seasonNum + "E" + episodeNum + " - " + episodeName;
                                }
                            } else {
                                return "No Upcoming Episode";
                            }
                        } else if (stage.equals("overview2")) {
                            if (!episode.getAirdate().equals("unknown")) {
                                String currentDate = episode.getAirdate();
                                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat dt1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                                try {
                                    Date date = dt.parse(currentDate);
                                    currentDate = dt1.format(date);
                                } catch (ParseException e) {
                                }

                                int currentDue = episode.getDue();

                                if (currentDue == 0) {
                                    return "Airs: Today, " + currentDate;
                                } else if (currentDue == 1) {
                                    return "Airs: Tomorrow, " + currentDate;
                                } else if (currentDue > 0) {
                                    return "Airs: In " + currentDue + " days, " + currentDate;
                                }
                            } else {
                                return "Airs: TBA";
                            }
                        }
                    }
                }
            }
        }

        return "No Upcoming Episode";
    }

    public String getCurrentEpisode() {
        int tempIndex = absoluteIndex;
        Episode episode;
        for (int i = 0; i < seasons.size(); i++) {
            if (!seasons.get(i).getSeasonNum().equals("0")) {
                Season season = seasons.get(i);
                if (season.getEpisodes().size() > tempIndex) {
                    episode = season.getEpisodes().get(tempIndex);

                    String seasonNum = episode.getSeasonNum();
                    int seasonInt = Integer.parseInt(seasonNum);
                    String episodeNum = episode.getEpisodeNum();
                    int episodeInt = Integer.parseInt(episodeNum);
                    String status;

                    int duedate = episode.getDue();

                    if (episode.getAirdate().equals("unknown")) {
                        status = " (TBA)";
                    } else if (duedate == 0) {
                        status = " (Today)";
                    } else if (duedate == 1) {
                        status = " (Tomorrow)";
                    } else if (duedate > 1) {
                        status = " (In " + String.valueOf(duedate) + " Days)";
                    } else {
                        status = " (Aired)";
                    }

                    if (seasonInt < 10 && episodeInt < 10) {
                        return "S0" + seasonNum + "E0" + episodeNum + status;
                    } else if (seasonInt >= 10 && episodeInt < 10) {
                        return "S" + seasonNum + "E0" + episodeNum + status;
                    } else if (seasonInt < 10 && episodeInt >= 10) {
                        return "S0" + seasonNum + "E" + episodeNum + status;
                    } else if (seasonInt >= 10 && episodeInt >= 10) {
                        return "S" + seasonNum + "E" + episodeNum + status;
                    } else {
                        return "S0" + seasonNum + "E0" + episodeNum + status;
                    }
                } else {
                    tempIndex = tempIndex - season.getEpisodes().size();
                }
            }
        }

        String temp = "";
        if (seasons.size() == 0) {
            temp = "TBA";
        } else if (seasons.size() == 1 && seasons.get(0).getSeasonNum().equals("0")) {
            temp = "TBA";
        } else {
            temp = "Completed";
        }

        return temp;
    }

    public String getBehindNumber2() {
        setNumEpisodes();
        return String.valueOf(numEpisodes - watched + 1);
    }

    public String getBehindNumber1() {
        return String.valueOf(numEpisodes - watched + 1);
    }

    public int getNumEpisodes() {
        return this.numEpisodes;
    }

    public void setNumEpisodes() {
        for (int i = seasons.size() - 1; i >= 0; i--) {
            if (!seasons.get(i).getSeasonNum().equals("0")) {
                List<Episode> episodes = seasons.get(i).getEpisodes();
                for (int j = episodes.size() - 1; j >= 0; j--) {
                    Episode episode = episodes.get(j);
                    if (!episode.getAirdate().equals("unknown")) {
                        if (episode.getNegativeDue() >= 0) {
                            numEpisodes = episode.getAbsoluteNum();
                            return; //end method
                        }
                    }
                }
            }
        }
    }

    public int getAbsoluteCount() {
        return this.absoluteCount;
    }

    public void setAbsoluteCount(int absoluteCount) {
        this.absoluteCount = absoluteCount;
    }

    public int getAbsoluteIndex() {
        return this.absoluteIndex;
    }

    public void setAbsoluteIndex(int absoluteIndex) {
        this.absoluteIndex = absoluteIndex;
    }

    public int getWatchedVar() {
        return this.watched;
    }

    public void setWatchedVar(int watched) {
        this.watched = watched;
    }

    public List<Integer> getWatchedIndex() {
        return watchedIndex;
    }

    public void setWatchedIndex(List<Integer> watchedIndex) {
        this.watchedIndex = watchedIndex;
    }

    public void setWatched(int absoluteNum) {
        if ((numEpisodes - watched) >= 0) {
            if (absoluteNum != -1) {
                if (absoluteNum > absoluteIndex) {
                    this.absoluteIndex = absoluteNum;
                }
                watchedIndex.add(absoluteNum);
                addToSeasonIndex(absoluteNum);
            } else {
                this.absoluteIndex++;
                watchedIndex.add(absoluteIndex);
                addToSeasonIndex(absoluteIndex);
            }
            Collections.sort(watchedIndex);
            Collections.reverse(watchedIndex);
            this.watched++;
        }
    }

    public void setUnwatched(int absoluteNum) {
        if (!watchedIndex.isEmpty()) {
            if (absoluteNum != -1) {
                removeFromSeasonIndex(absoluteNum);
                watchedIndex.remove(new Integer(absoluteNum));
            } else {
                removeFromSeasonIndex(watchedIndex.get(0));
                watchedIndex.remove(0);
            }
            if (!watchedIndex.isEmpty()) {
                this.absoluteIndex = watchedIndex.get(0);
            } else {
                this.absoluteIndex = 0;
            }
            this.watched--;
        }
    }

    public void addToSeasonIndex(int absoluteNum) {
        int tempIndex = absoluteNum;
        for (int i = 0; i < seasons.size(); i++) {
            Season season = seasons.get(i);
            if (!season.getSeasonNum().equals("0")) {
                if (season.getEpisodes().size() >= tempIndex) {
                    season.setWatched();
                    break;
                } else {
                    tempIndex = tempIndex - season.getEpisodes().size();
                }
            }
        }
    }

    public void removeFromSeasonIndex(int absoluteNum) {
        int tempIndex = absoluteNum;
        for (int i = 0; i < seasons.size(); i++) {
            Season season = seasons.get(i);
            if (!season.getSeasonNum().equals("0")) {
                if (season.getEpisodes().size() >= tempIndex) {
                    season.setUnwatched();
                    break;
                } else {
                    tempIndex = tempIndex - season.getEpisodes().size();
                }
            }
        }
    }
}
