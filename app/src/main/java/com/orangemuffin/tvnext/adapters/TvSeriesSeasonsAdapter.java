package com.orangemuffin.tvnext.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.models.Season;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 4/12/2017 */
public class TvSeriesSeasonsAdapter extends BaseAdapter {
    private List<Season> seasons = new ArrayList<>();
    private LayoutInflater inflater = null;
    private String viewing;

    public TvSeriesSeasonsAdapter(List<Season> seasons, Context context, String viewing) {
        this.seasons = seasons;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewing = viewing;
    }

    @Override
    public int getCount() {
        return seasons.size();
    }

    @Override
    public Object getItem(int i) {
        return seasons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder {
        TextView seasonNumber;
        TextView seasonWatched;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = new Holder();
        View rootView = inflater.inflate(R.layout.seasonslist_element, null);
        holder.seasonNumber = (TextView) rootView.findViewById(R.id.seasonslist_item);
        holder.seasonWatched = (TextView) rootView.findViewById(R.id.seasonslist_watched);

        Season season = seasons.get(i);
        holder.seasonNumber.setText("Season " + season.getSeasonNum());
        if (!season.getSeasonNum().equals("0")) {
            if (!viewing.equals("yes")) {
                holder.seasonWatched.setText(String.valueOf(season.getWatchedVar()) + "/" + String.valueOf(season.getEpisodes().size()));
            } else {
                if (season.getEpisodes().size() <= 1) {
                    holder.seasonWatched.setText(String.valueOf(season.getEpisodes().size()) + " Episode");
                } else {
                    holder.seasonWatched.setText(String.valueOf(season.getEpisodes().size()) + " Episodes");
                }
            }
        } else {
            if (season.getEpisodes().size() <= 1) {
                holder.seasonWatched.setText(String.valueOf(String.valueOf(season.getEpisodes().size())) + " Special");
            } else {
                holder.seasonWatched.setText(String.valueOf(String.valueOf(season.getEpisodes().size())) + " Specials");
            }
        }
        return rootView;
    }
}
