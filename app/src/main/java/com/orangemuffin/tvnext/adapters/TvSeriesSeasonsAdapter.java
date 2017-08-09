package com.orangemuffin.tvnext.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.EpisodesActivity;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.utils.LocalDataUtil;
import com.orangemuffin.tvnext.utils.MeasurementUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Created by OrangeMuffin on 8/1/2017 */
public class TvSeriesSeasonsAdapter extends BaseAdapter {
    private List<Season> seasons;
    private LayoutInflater inflater = null;
    private Context context;
    private String viewing;
    private String seriesId;
    private Activity activity;
    private TvSeries tvSeries;

    public TvSeriesSeasonsAdapter(TvSeries tvSeries, Context context, Activity activity, String seriesId, String viewing) {
        this.tvSeries = tvSeries;

        seasons = new ArrayList<>(tvSeries.getSeasons());
        Collections.reverse(seasons);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.viewing = viewing;
        this.seriesId = seriesId;
        this.activity = activity;
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
        ImageView overflow_more;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        final Holder holder = new Holder();
        View rootView = inflater.inflate(R.layout.seasonslist_element, null);
        holder.seasonNumber = (TextView) rootView.findViewById(R.id.seasonslist_item);
        holder.seasonWatched = (TextView) rootView.findViewById(R.id.seasonslist_watched);
        holder.overflow_more = (ImageView) rootView.findViewById(R.id.overflow_more);

        final Season season = seasons.get(i);
        holder.seasonNumber.setText("Season " + season.getSeasonNum());

        if (season.getSeasonNum().equals("0")) {
            holder.overflow_more.setVisibility(View.GONE);
            MeasurementUtil.setMargin(holder.seasonWatched, 0, 10, 10, 0);
            if (season.getEpisodes().size() <= 1) {
                holder.seasonWatched.setText(String.valueOf(String.valueOf(season.getEpisodes().size())) + " Special");
            } else {
                holder.seasonWatched.setText(String.valueOf(String.valueOf(season.getEpisodes().size())) + " Specials");
            }
        } else {
            holder.seasonWatched.setText(season.getNumWatched());
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EpisodesActivity.class);
                intent.putExtra("seriesId", seriesId);
                intent.putExtra("viewing", viewing);
                intent.putExtra("seasonNumber", seasons.get(position).getSeasonNum());
                intent.putExtra("seasonIndex", position);
                ((Activity) context).startActivityForResult(intent, 11006);
                activity.overridePendingTransition(R.anim.slide_up, R.anim.anim_stay);
            }
        });

        holder.overflow_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow_more, position);
            }
        });

        return rootView;
    }

    private void showPopupMenu(final View view, final int position) {
        final PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_season, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final Season season = seasons.get(position);
                switch (menuItem.getItemId()) {
                    case R.id.watch:
                        tvSeries.setWatched(season.getSeasonNum(), -2);
                        LocalDataUtil.saveTvSeries(context, tvSeries);
                        notifyDataSetChanged();
                        return true;
                    case R.id.unwatch:
                        tvSeries.setUnwatched(season.getSeasonNum(), -2);
                        LocalDataUtil.saveTvSeries(context, tvSeries);
                        notifyDataSetChanged();
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
