package com.orangemuffin.tvnext.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.EpisodesActivity;
import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.utils.LocalDataUtil;
import com.orangemuffin.tvnext.utils.DateAndTimeUtil;
import com.orangemuffin.tvnext.utils.StringFormatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Created by OrangeMuffin on 8/1/2017 */
public class TvSeriesEpisodesAdapter extends BaseAdapter {
    private TvSeries tvSeries;
    private LayoutInflater inflater = null;
    private List<Season> seasons;
    private List<Episode> episodes = new ArrayList<>();
    private String seasonNum;
    private Context context;
    private String viewing;

    public TvSeriesEpisodesAdapter(TvSeries tvSeries, int seasonIndex, String viewing, Context context) {
        this.tvSeries = tvSeries;

        seasons = new ArrayList<>(tvSeries.getSeasons());
        Collections.reverse(seasons);
        episodes = seasons.get(seasonIndex).getEpisodes();
        seasonNum = seasons.get(seasonIndex).getSeasonNum();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.viewing = viewing;
    }

    public void selectSeason() {
        for (Season season : tvSeries.getSeasons()) {
            if (season.getSeasonNum().equals(seasonNum)) {
                for (Episode episode : episodes) {
                    if (episode.getDue() != null && episode.getDue() <= 0) {
                        if (!season.getWatchedIndex().contains(episode.getAbsoluteNum())) {
                            tvSeries.setWatched(seasonNum, episode.getAbsoluteNum());
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        LocalDataUtil.saveTvSeries(context, tvSeries);
        notifyDataSetChanged();
    }

    public void unselectSeason() {
        for (Season season : tvSeries.getSeasons()) {
            if (season.getSeasonNum().equals(seasonNum)) {
                for (Episode episode : episodes) {
                    if (episode.getDue() <= 0) {
                        if (season.getWatchedIndex().contains(episode.getAbsoluteNum())) {
                            tvSeries.setUnwatched(seasonNum, episode.getAbsoluteNum());
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        LocalDataUtil.saveTvSeries(context, tvSeries);
        notifyDataSetChanged();
    }

    public boolean checkSeason() {
        for (Season season : tvSeries.getSeasons()) {
            if (season.getSeasonNum().equals(seasonNum)) {
                if (season.getWatchedIndex().size() == episodes.size()) {
                    return true;
                } else {
                    for (Episode episode : episodes) {
                        if (episode.getDue() != null && episode.getDue() <= 0 && !season.getWatchedIndex().contains(episode.getAbsoluteNum())) {
                            return false;
                        } else if (episode.getDue() == null || episode.getDue() > 0) {
                            break;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public Object getItem(int i) {
        return episodes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder {
        TextView episodeName;
        TextView episodeDate;
        ImageView overflow_kite;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder = new Holder();
        View rootView = inflater.inflate(R.layout.episodeslist_element, null);
        holder.episodeName = (TextView) rootView.findViewById(R.id.episodeslist_name);
        holder.episodeDate = (TextView) rootView.findViewById(R.id.episodeslist_date);
        holder.overflow_kite = (ImageView) rootView.findViewById(R.id.overflow_kite);

        final Episode episode = episodes.get(i);

        if (episode.getSeasonNum().equals("0") && (episode.getDue() != null && episode.getDue() <= 1)) {
            holder.overflow_kite.setVisibility(View.GONE);
            holder.episodeName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.episodeDate.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (episode.getDue() == null || episode.getDue() >= 1) {
            holder.overflow_kite.setVisibility(View.GONE);
            holder.episodeName.setTextColor(Color.parseColor("#9B9B9B"));
            holder.episodeDate.setTextColor(Color.parseColor("#9B9B9B"));
        } else {
            if (tvSeries.getWatchedIndex().contains(episode.getAbsoluteNum())) {
                holder.overflow_kite.setBackgroundResource(R.drawable.ic_kite_white_on);
                holder.overflow_kite.setTag(2);
            } else {
                holder.overflow_kite.setBackgroundResource(R.drawable.ic_kite_white_off);
                holder.overflow_kite.setTag(1);
            }
            holder.episodeName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.episodeDate.setTextColor(Color.parseColor("#FFFFFF"));
        }

        String epiName = "#" + StringFormatUtil.prefixNumber(i+1) + " - " + episode.getName();

        holder.episodeName.setText(epiName);

        if (episode.getDue() != null) {
            String currentDate = DateAndTimeUtil.convertDate("dd MMM yyyy", episode.getAirdate());
            String currentDue = StringFormatUtil.getDueDate(episode.getDue());
            holder.episodeDate.setText(currentDate + " | " + currentDue);
        } else {
            holder.episodeDate.setText("TBA");
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, String.valueOf(seasons.get(1).getWatchedIndex().size()), Toast.LENGTH_LONG).show();
            }
        });

        holder.overflow_kite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(holder.overflow_kite.getTag().toString()) == 1) {
                    holder.overflow_kite.setBackgroundResource(R.drawable.ic_kite_white_on);
                    holder.overflow_kite.setTag(2);
                    tvSeries.setWatched(episode.getSeasonNum(), episode.getAbsoluteNum());
                } else {
                    holder.overflow_kite.setBackgroundResource(R.drawable.ic_kite_white_off);
                    holder.overflow_kite.setTag(1);
                    tvSeries.setUnwatched(episode.getSeasonNum(), episode.getAbsoluteNum());
                }

                LocalDataUtil.saveTvSeries(context, tvSeries);
                ((EpisodesActivity) context).toggleSeason(checkSeason());
            }
        });
        return rootView;
    }
}
