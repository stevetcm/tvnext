package com.orangemuffin.tvnext.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.utils.DateAndTimeUtil;
import com.orangemuffin.tvnext.utils.StringFormatUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/* Created by OrangeMuffin on 7/30/2017 */
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
        String getDay = DateAndTimeUtil.dateToDay(currentDate);

        Date date;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy");
        try {
            date = dt.parse(currentDate);
            currentDate = dt1.format(date);
        } catch (ParseException e) { }

        if (currentDue == 0) {
            holder.header_text.setText(DateAndTimeUtil.dateToDay(today) + " | " + currentDate + " | " + StringFormatUtil.getDueDate(currentDue));
        } else {
            holder.header_text.setText(getDay + " | " + currentDate + " | " + StringFormatUtil.getDueDate(currentDue));
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
                Picasso.with(context).load(R.drawable.placeholder_banner).noFade().centerCrop().fit().into(holder.serieImage);
            } else {
                Picasso.with(context).load(mypath).noFade().centerCrop().fit().into(holder.serieImage);
            }
        } catch (Exception e) { }

        holder.name.setText(episode.getSeriesName());

        String episode_text = StringFormatUtil.numDisplay(episode.getSeasonNum(), episode.getEpisodeNum()) + " - " + episode.getName();
        holder.episode.setText(episode_text);

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

        public HeaderViewHolder(View v) {
            header_text = (TextView) v.findViewById(R.id.header_text);
        }
    }
}
