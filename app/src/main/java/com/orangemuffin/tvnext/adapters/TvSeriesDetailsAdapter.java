package com.orangemuffin.tvnext.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.TvSeriesActivity;
import com.orangemuffin.tvnext.models.TvSeries;
import com.squareup.picasso.Picasso;

import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* Created by OrangeMuffin on 3/14/2017 */
public class TvSeriesDetailsAdapter extends RecyclerView.Adapter<TvSeriesDetailsAdapter.ViewHolder> {
    private List<TvSeries> data;
    private Context context;
    private Activity activity;
    private Fragment pContext;

    private Picasso picasso;

    private Map<String, String> tvshow_map = new HashMap<String, String>();

    public TvSeriesDetailsAdapter(Context context, List<TvSeries> data, Activity activity, Fragment pContext) {
        this.data = data;
        this.context = context;
        this.activity = activity;
        this.pContext = pContext;

        try {
            File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            tvshow_map = (Map<String, String>) inputStream.readObject();
        } catch (Exception e) { }

        picasso = new Picasso.Builder(context).executor(Executors.newSingleThreadExecutor()).build();
    }

    public void add(List<TvSeries> items) {
        int previousDataSize = this.data.size();
        this.data.addAll(items);
        notifyItemRangeInserted(previousDataSize, items.size());
    }

    public void modify(TvSeries item) {
        for(int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(item.getId())) {
                this.data.set(i, item);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TvSeries tvSeries = data.get(i);

        viewHolder.title.setText(tvSeries.getName());
        //viewHolder.episode.setText(tvSeries.getNearestEpisode("Details"));
        viewHolder.episode.setText(tvSeries.getCurrentEpisode());

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("Series", Context.MODE_PRIVATE);
        File mypath_img = new File(directory, tvSeries.getId() + ".jpg");

        String urlPoster = tvSeries.getPoster();
        if (urlPoster != null && urlPoster.equals("")) {
            picasso.with(context).load(R.drawable.placeholder_portrait).noFade().fit().centerCrop().into(viewHolder.poster);
        } else if (urlPoster != null) {
            picasso.with(context).load(mypath_img).noFade().fit().centerCrop().into(viewHolder.poster);
        } else {
            picasso.with(context).load(R.drawable.placeholder_portrait).noFade().fit().centerCrop().into(viewHolder.poster);
        }

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());

        /*SharedPreferences sp_data = context.getSharedPreferences("PHONEDATA", context.MODE_PRIVATE);
        String value = sp_data.getString("TVSERIES_NUMTIMER", "UNKNOWN");

        int duedate = -1;

        if (value.equals("UNKNOWN")) {
            duedate = 1;
        } else {
            try {
                Date one = dateFormat.parse(today);
                Date two = dateFormat.parse(value);
                long diff = one.getTime() - two.getTime();
                duedate = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            } catch (Exception e) { }
        }

        int current;
        if (duedate > 0) {
            int temp = tvSeries.getNumEpisodes();
            current = Integer.parseInt(tvSeries.getBehindNumber2());
            int temp2 = tvSeries.getNumEpisodes();

            //update numEpisodes with proper date
            if (temp != temp2) {
                File mypath = new File(directory, tvSeries.getId() + ".ser");
                if (mypath.exists()) mypath.delete();
                try {
                    FileOutputStream fos = new FileOutputStream(mypath);
                    FSTObjectOutput out = new FSTObjectOutput(fos);
                    out.writeObject(tvSeries, TvSeries.class);
                    out.close();
                } catch (Exception e) {}
            }

            SharedPreferences.Editor editor = sp_data.edit();
            editor.putString("TVSERIES_NUMTIMER", today);
            editor.apply();
        } else {
            current = Integer.parseInt(tvSeries.getBehindNumber1());
        }*/

        String getDay = dateToDay(today);
        int current;

        if (tvSeries.getAirDay().equals(getDay)) {
            int temp = tvSeries.getNumEpisodes();
            current = Integer.parseInt(tvSeries.getBehindNumber2());
            int temp2 = tvSeries.getNumEpisodes();

            //update numEpisodes with proper date
            if (temp != temp2) {
                File mypath = new File(directory, tvSeries.getId() + ".ser");
                if (mypath.exists()) mypath.delete();
                try {
                    FileOutputStream fos = new FileOutputStream(mypath);
                    FSTObjectOutput out = new FSTObjectOutput(fos);
                    out.writeObject(tvSeries, TvSeries.class);
                    out.close();
                } catch (Exception e) {}
            }
        } else {
            current = Integer.parseInt(tvSeries.getBehindNumber1());
        }

        if (current >= 0 && current < 10) {
            viewHolder.episodebehind.setPadding(0, (int)dpToPixel(2), (int)dpToPixel(6), 0);
        } else if (current >= 10 && current < 100) {
            viewHolder.episodebehind.setPadding(0, (int)dpToPixel(2), (int)dpToPixel(4), 0);
        } else if (current >= 100 && current < 1000) {
            viewHolder.episodebehind.setPadding(0, (int)dpToPixel(2), (int)dpToPixel(2), 0);
        }

        viewHolder.episodebehind.setText(String.valueOf(current));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        //need unique id - re-activate animation on items
        return Integer.parseInt(tvshow_map.get(data.get(position).getName()));
    }

    @Override
    public TvSeriesDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tvseries_card_poster, viewGroup, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, episode, episodebehind;
        ImageView poster, overflow_more;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            episode = (TextView) view.findViewById(R.id.episode);
            episodebehind = (TextView) view.findViewById(R.id.episodebehind);
            poster = (ImageView) view.findViewById(R.id.poster);
            overflow_more = (ImageView) view.findViewById(R.id.overflow_more);

            /* reserve space for listing */
            poster.requestLayout();

            SharedPreferences sp_data = context.getSharedPreferences("PHONEDATA", context.MODE_PRIVATE);
            int width = sp_data.getInt("PHONE_RES", 1080);

            if (width == 1080) {
                poster.getLayoutParams().height = 494; //on 1080 res
            } else {
                if (context.getResources().getBoolean(R.bool.isTablet)) {
                    poster.getLayoutParams().height = (int) ((width - (10 * dpToPixel(3))) * 441 / (5 * 300));
                } else {
                    poster.getLayoutParams().height = (int) ((width - (6 * dpToPixel(3))) * 441 / (3 * 300));
                }
            }

            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, TvSeriesActivity.class);
                    intent.putExtra("seriesName", data.get(position).getName());
                    intent.putExtra("seriesId", data.get(position).getId());
                    intent.putExtra("viewing", "no");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_top, R.anim.anim_stay);
                }
            });

            overflow_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(overflow_more, getAdapterPosition());
                }
            });
        }
    }

    private void showPopupMenu(final View view, final int position) {
        final PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_tvseries, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                TvSeries tvSeries = data.get(position);
                ContextWrapper cw = new ContextWrapper(context);
                final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                File mypath = new File(directory, tvSeries.getId() + ".ser");
                if (mypath.exists()) mypath.delete();
                switch (menuItem.getItemId()) {
                    case R.id.watched:
                        tvSeries.setWatched(-1);
                        notifyDataSetChanged();
                        try {
                            FileOutputStream fos = new FileOutputStream(mypath);
                            FSTObjectOutput out = new FSTObjectOutput(fos);
                            out.writeObject(data.get(position), TvSeries.class);
                            out.close();
                        } catch (Exception e) {}
                        return true;
                    case R.id.unwatched:
                        data.get(position).setUnwatched(-1);
                        notifyDataSetChanged();
                        try {
                            FileOutputStream fos = new FileOutputStream(mypath);
                            FSTObjectOutput out = new FSTObjectOutput(fos);
                            out.writeObject(data.get(position), TvSeries.class);
                            out.close();
                        } catch (Exception e) {}
                        return true;
                    case R.id.remove:
                        AlertDialog.Builder alertDialogBuilderInner = new AlertDialog.Builder(context);
                        String name_text = "Are you sure you want to remove " + "<font color=#20B5FF>"
                                + data.get(position).getName() + "</font>" + "?<br>"
                                + "(All saved data will be erased)";
                        alertDialogBuilderInner.setMessage(Html.fromHtml(name_text));
                        alertDialogBuilderInner
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //remove entry from user map
                                        try {
                                            File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
                                            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                                            tvshow_map.remove(data.get(position).getName());
                                            outputStream.writeObject(tvshow_map);
                                            outputStream.flush();
                                            outputStream.close();
                                        } catch (Exception e) { }

                                        String name_text = "<font color=#20B5FF>" + data.get(position).getName() + "</font>" + " removed from your list";
                                        Toast.makeText(context, Html.fromHtml(name_text), Toast.LENGTH_LONG).show();

                                        //get internal location data/series
                                        ContextWrapper cw = new ContextWrapper(context);
                                        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);

                                        //remove serializable tv show data
                                        File mypath = new File(directory, data.get(position).getId() + ".ser");
                                        if (mypath.exists()) mypath.delete();

                                        //remove image poster of tv show
                                        File mypath_img = new File(directory, data.get(position).getId() + ".jpg");
                                        if (mypath_img.exists()) mypath_img.delete();
                                        Picasso.with(context).invalidate(mypath_img);

                                        data.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("No", null);
                        alertDialogBuilderInner.show();
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public float dpToPixel(int dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
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
