package com.orangemuffin.tvnext.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.EpisodesActivity;
import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;

import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/* Created by OrangeMuffin on 4/13/2017 */
public class TvSeriesEpisodesAdapter extends BaseAdapter {
    private TvSeries tvSeries;
    private LayoutInflater inflater = null;
    private List<Season> seasons;
    private List<Episode> episodes = new ArrayList<>();
    private Context context;
    private int seasonIndex;
    private String viewing;

    public TvSeriesEpisodesAdapter(TvSeries tvSeries, int seasonIndex, String viewing, Context context) {
        this.tvSeries = tvSeries;
        seasons = new ArrayList<>(tvSeries.getSeasons());
        Collections.reverse(seasons);
        this.seasonIndex = seasonIndex;
        this.episodes = seasons.get(seasonIndex).getEpisodes();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewing = viewing;
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
        ImageView overflow_add;
    }

    public void selectSeason() {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
        File mypath = new File(directory, tvSeries.getId() + ".ser");
        if (mypath.exists()) mypath.delete();

        for(int i = 0; i < episodes.size(); i++) {
            Episode episode = episodes.get(i);
            if (episode.getAbsoluteNum() <= tvSeries.getNumEpisodes()) {
                if (!tvSeries.getWatchedIndex().contains(episode.getAbsoluteNum())) {
                    tvSeries.setWatched(episode.getAbsoluteNum());
                }
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(mypath);
            FSTObjectOutput out = new FSTObjectOutput(fos);
            out.writeObject(tvSeries, TvSeries.class);
            out.close();
        } catch (Exception e) {}

        notifyDataSetChanged();
    }

    public void unselectSeason() {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
        File mypath = new File(directory, tvSeries.getId() + ".ser");
        if (mypath.exists()) mypath.delete();

        for(int i = 0; i < episodes.size(); i++) {
            Episode episode = episodes.get(i);
            if (episode.getAbsoluteNum() <= tvSeries.getNumEpisodes()) {
                if (tvSeries.getWatchedIndex().contains(episode.getAbsoluteNum())) {
                    tvSeries.setUnwatched(episode.getAbsoluteNum());
                }
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(mypath);
            FSTObjectOutput out = new FSTObjectOutput(fos);
            out.writeObject(tvSeries, TvSeries.class);
            out.close();
        } catch (Exception e) { }

        notifyDataSetChanged();
    }

    public boolean checkSeason() {
        if (episodes.size() == seasons.get(seasonIndex).getWatchedVar()) {
            return true;
        } else {
            int count = 0;
            for (int i = 0; i < episodes.size(); i++) {
                if (episodes.get(i).getAbsoluteNum() <= tvSeries.getNumEpisodes()) {
                    count++;
                } else {
                    break;
                }
            }
            if (count == seasons.get(seasonIndex).getWatchedVar()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder = new Holder();
        View rootView = inflater.inflate(R.layout.episodeslist_element, null);
        holder.episodeName = (TextView) rootView.findViewById(R.id.episodeslist_name);
        holder.episodeDate = (TextView) rootView.findViewById(R.id.episodeslist_date);
        holder.overflow_add = (ImageView) rootView.findViewById(R.id.overflow_add);

        final Episode episode = episodes.get(i);
        if (episode.getAbsoluteNum() > tvSeries.getNumEpisodes()) {
            holder.overflow_add.setVisibility(View.GONE);
            holder.episodeName.setTextColor(Color.parseColor("#9B9B9B"));
            holder.episodeDate.setTextColor(Color.parseColor("#9B9B9B"));
        } else {
            holder.episodeName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.episodeDate.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (viewing.equals("yes")) {
            holder.overflow_add.setVisibility(View.GONE);
        }

        if (seasons.get(seasonIndex).getSeasonNum().equals("0")) {
            holder.overflow_add.setVisibility(View.GONE);
        }

        String epiname;
        if (i+1 < 10) {
            epiname = "#0" + String.valueOf(i+1) + " - " + episode.getName();
        } else {
            epiname = "#" + String.valueOf(i+1) + " - " + episode.getName();
        }

        String currentDate = episode.getAirdate();
        Date date;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy");
        try {
            date = dt.parse(currentDate);
            currentDate = dt1.format(date);
        } catch (ParseException e) { }


        int currentDue = episode.getDue();
        int currentNegativeDue = episode.getNegativeDue();

        if (tvSeries.getWatchedIndex().contains(episode.getAbsoluteNum())) {
            holder.overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt2);
            holder.overflow_add.setTag(2);
        } else {
            holder.overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt1);
            holder.overflow_add.setTag(1);
        }

        holder.overflow_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextWrapper cw = new ContextWrapper(context);
                final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                File mypath = new File(directory, tvSeries.getId() + ".ser");
                if (mypath.exists()) mypath.delete();
                //View parentView = (View) view.getParent();
                //ListView listView = (ListView) parentView.getParent();
                //final int position = listView.getPositionForView(parentView);
                if ((Integer)holder.overflow_add.getTag() == 2) {
                    holder.overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt1);
                    holder.overflow_add.setTag(1);
                    tvSeries.setUnwatched(episode.getAbsoluteNum());
                } else {
                    holder.overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt2);
                    holder.overflow_add.setTag(2);
                    tvSeries.setWatched(episode.getAbsoluteNum());
                }
                try {
                    FileOutputStream fos = new FileOutputStream(mypath);
                    FSTObjectOutput out = new FSTObjectOutput(fos);
                    out.writeObject(tvSeries, TvSeries.class);
                    out.close();
                } catch (Exception e) {}
                ((EpisodesActivity) context).toggleSeason(checkSeason());
            }
        });

        String episodeDue = "";
        if (currentDue == 0) {
            episodeDue = " | Today";
        } else if (currentDue > 0) {
            if (currentDue == 1) {
                episodeDue = " | Tomorrow";
            } else {
                episodeDue = " | In " + currentDue + " Days";
            }
        } else if (currentNegativeDue > 0) {
            if (currentNegativeDue == 1) {
                episodeDue = " | Yesterday";
            } else {
                episodeDue = " | " + currentNegativeDue + " Days Ago";
            }
        }

        holder.episodeName.setText(epiname);
        holder.episodeDate.setText(currentDate + episodeDue);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View mView = layoutInflater.inflate(R.layout.dialog_tvseries, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(mView);

                String status_name;
                if ((Integer)holder.overflow_add.getTag() == 2) {
                    status_name = "Set Unwatched";
                } else {
                    status_name = "Set Watched";
                }

                if (viewing.equals("yes") || seasons.get(seasonIndex).getSeasonNum().equals("0")
                        || episode.getAbsoluteNum() > tvSeries.getNumEpisodes()) {
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });
                } else {
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton(status_name,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            ContextWrapper cw = new ContextWrapper(context);
                                            final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                                            File mypath = new File(directory, tvSeries.getId() + ".ser");
                                            if (mypath.exists()) mypath.delete();

                                            if ((Integer) holder.overflow_add.getTag() == 2) {
                                                holder.overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt1);
                                                holder.overflow_add.setTag(1);
                                                tvSeries.setUnwatched(episode.getAbsoluteNum());
                                            } else {
                                                holder.overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt2);
                                                holder.overflow_add.setTag(2);
                                                tvSeries.setWatched(episode.getAbsoluteNum());
                                            }
                                            try {
                                                FileOutputStream fos = new FileOutputStream(mypath);
                                                FSTObjectOutput out = new FSTObjectOutput(fos);
                                                out.writeObject(tvSeries, TvSeries.class);
                                                out.close();
                                            } catch (Exception e) {
                                            }
                                            ((EpisodesActivity) context).toggleSeason(checkSeason());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });
                }

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);

                TextView episode_title = (TextView) mView.findViewById(R.id.dialogTitle);

                String episode_title_text = "";
                String seasonNum = episode.getSeasonNum();
                String episodeNum = episode.getEpisodeNum();
                String episodeName = episode.getName();
                if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) < 10) {
                    episode_title_text = "S0" + seasonNum + "E0" + episodeNum + " - " + episodeName;
                } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) < 10) {
                    episode_title_text = "S" + seasonNum + "E0" + episodeNum + " - " + episodeName;
                } else if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) >= 10) {
                    episode_title_text = "S0" + seasonNum + "E" + episodeNum + " - " + episodeName;
                } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) >= 10) {
                    episode_title_text = "S" + seasonNum + "E" + episodeNum + " - " + episodeName;
                }
                episode_title.setText(episode_title_text);

                TextView description = (TextView) mView.findViewById(R.id.dialogDescription);
                description.setText(episode.getOverview());

                alertDialog.show();
            }
        });

        return rootView;
    }
}