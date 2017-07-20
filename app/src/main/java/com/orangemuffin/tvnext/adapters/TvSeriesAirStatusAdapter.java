package com.orangemuffin.tvnext.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.TvSeriesActivity;
import com.orangemuffin.tvnext.models.Episode;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/* Created by OrangeMuffin on 3/16/2017 */
public class TvSeriesAirStatusAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<Episode> data;
    private Context context;
    private Activity activity;

    public TvSeriesAirStatusAdapter(Context context, List<Episode> data, Activity activity) {
        this.data = data;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view == null) {
            view = inflater.inflate(R.layout.stickydetails_header, viewGroup, false);
            holder = new HeaderViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        Episode episode = data.get(i);

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());

        String currentDate = episode.getAirdate();
        int currentDue = episode.getDue();
        int currentNegativeDue = episode.getNegativeDue();
        String getDay = dateToDay(currentDate);

        Date date;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy");
        try {
            date = dt.parse(currentDate);
            currentDate = dt1.format(date);
        } catch (ParseException e) { }


        //holder.header_divider.setBackgroundColor(Color.parseColor("#d2d2d2"));

        if (currentDue == 0) {
            //String today_text = getDayFromDate(today) + " | " + today + " | <font color=#ff0000>Today</font>";
            //holder.header_text.setText(Html.fromHtml(today_text));
            //holder.header_divider.setBackgroundColor(Color.RED);
            holder.header_text.setText(dateToDay(today) + " | " + currentDate + " | Today");
        } else if (currentDue > 0) {
            if (currentDue == 1) {
                holder.header_text.setText(getDay + " | " + currentDate + " | Tomorrow");
            } else {
                holder.header_text.setText(getDay + " | " + currentDate + " | In " + currentDue + " Days");
            }
        } else if (currentNegativeDue > 0) {
            if (currentNegativeDue == 1) {
                holder.header_text.setText(getDay + " | " + currentDate + " | Yesterday");
            } else {
                holder.header_text.setText(getDay + " | " + currentDate + " | " + currentNegativeDue + " Days Ago");
            }
        }

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return data.get(i).getDue();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.stickyairstatus_row, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Episode episode = data.get(i);

        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("Series", Context.MODE_PRIVATE);
            File mypath = new File(directory, episode.getSeriesId() + ".jpg");
            if (!mypath.exists()) {
                Picasso.with(context).load(R.drawable.placeholder_landscape).config(Bitmap.Config.RGB_565).tag("resume_tag").noFade().into(holder.serieImage);
            } else {
                Picasso.with(context).load(mypath).config(Bitmap.Config.RGB_565).tag("resume_tag").noFade().into(holder.serieImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.name.setText(episode.getSeriesName());
        String episode_text = "";
        String seasonNum = episode.getSeasonNum();
        String episodeNum = episode.getEpisodeNum();
        String episodeName = episode.getName();
        if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) < 10) {
            episode_text = "S0" + seasonNum + "E0" + episodeNum + " - " + episodeName;
        } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) < 10) {
            episode_text = "S" + seasonNum + "E0" + episodeNum + " - " + episodeName;
        } else if (Integer.parseInt(seasonNum) < 10 && Integer.parseInt(episodeNum) >= 10) {
            episode_text = "S0" + seasonNum + "E" + episodeNum + " - " + episodeName;
        } else if (Integer.parseInt(seasonNum) >= 10 && Integer.parseInt(episodeNum) >= 10) {
            episode_text = "S" + seasonNum + "E" + episodeNum + " - " + episodeName;
        }
        holder.episode.setText(episode_text);

        final int position = i;
        view.setOnClickListener(new View.OnClickListener() {
            Episode episode = data.get(position);
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View mView = layoutInflater.inflate(R.layout.dialog_tvseries, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(mView);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                })
                        .setNeutralButton("Open",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        Intent intent = new Intent(context, TvSeriesActivity.class);
                                        intent.putExtra("seriesName", episode.getSeriesName());
                                        intent.putExtra("seriesId", episode.getSeriesId());
                                        intent.putExtra("viewing", "no");
                                        activity.startActivity(intent);
                                        activity.overridePendingTransition(R.anim.slide_top, R.anim.anim_stay);
                                    }
                                });;

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
        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView episode;
        private ImageView serieImage;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.element_text);
            episode = (TextView) v.findViewById(R.id.textView1);
            serieImage = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    private class HeaderViewHolder {
        private TextView header_text;
        private View header_divider;

        public HeaderViewHolder(View v) {
            header_text = (TextView) v.findViewById(R.id.header_text);
            header_divider = (View) v.findViewById(R.id.header_divider);
        }
    }

    public String dateToDay(String date) {
        String day = "";
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
            int result = calendar.get(Calendar.DAY_OF_WEEK);
            switch (result) {
                case Calendar.MONDAY:
                    day = "Monday";
                    break;
                case Calendar.TUESDAY:
                    day = "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    day = "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    day = "Thursday";
                    break;
                case Calendar.FRIDAY:
                    day = "Friday";
                    break;
                case Calendar.SATURDAY:
                    day = "Saturday";
                    break;
                case Calendar.SUNDAY:
                    day = "Sunday";
                    break;
            }
        } catch (Exception e) { }
        return day;
    }
}
