package com.orangemuffin.tvnext.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.orangemuffin.tvnext.fragments.FragmentTvSeries;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.utils.LocalDataUtil;
import com.orangemuffin.tvnext.utils.MeasurementUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Created by OrangeMuffin on 7/29/2017 */
public class TvSeriesDetailsAdapter extends RecyclerView.Adapter<TvSeriesDetailsAdapter.ViewHolder> {
    private List<TvSeries> data;
    private Context context;
    private Activity activity;
    private Fragment parentFragment;

    private Map<String, String> tvshow_map;

    public TvSeriesDetailsAdapter(Context context, List<TvSeries> data, Activity activity, Fragment parentFragment) {
        this.data = data;
        this.context = context;
        this.activity = activity;
        this.parentFragment = parentFragment;

        tvshow_map = LocalDataUtil.getTvSeriesMap(context);
    }

    public void add(List<TvSeries> items) {
        int previousDataSize = this.data.size();
        this.data.addAll(items);
        notifyItemRangeInserted(previousDataSize, items.size());
    }

    public void modify(TvSeries item) {
        boolean breaking = true;
        for(int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(item.getId())) {
                this.data.set(i, item);
                breaking = false;
                break;
            }
        }
        if (breaking) {
            //retrieve current state of user map
            tvshow_map = LocalDataUtil.getTvSeriesMap(context);
            this.data.add(item);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TvSeries tvSeries = data.get(position);

        holder.title.setText(tvSeries.getName());
        holder.episode.setText(tvSeries.getCurrentString());

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("Series", Context.MODE_PRIVATE);
        File mypath_img = new File(directory, tvSeries.getId() + ".jpg");

        String urlPoster = tvSeries.getPoster();
        if (urlPoster != null) {
            Picasso.with(context).load(mypath_img).noFade().fit().centerCrop().into(holder.poster);
        } else {
            Picasso.with(context).load(R.drawable.placeholder_poster).noFade().fit().centerCrop().into(holder.poster);
        }
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
        TextView title, episode;
        ImageView poster, overflow_more;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            episode = (TextView) view.findViewById(R.id.episode);
            poster = (ImageView) view.findViewById(R.id.poster);
            overflow_more = (ImageView) view.findViewById(R.id.overflow_more);

            /* reserve space for listing */
            poster.requestLayout();

            SharedPreferences sp_data = context.getSharedPreferences("PHONEDATA", context.MODE_PRIVATE);
            int width = sp_data.getInt("PHONE_RES", 1080);
            if (width == 1080) {
                poster.getLayoutParams().height = 494; //specific dimension on 1080 res
            } else {
                poster.getLayoutParams().height = (int) ((width - (6 * MeasurementUtil.dpToPixel(3))) * 441 / (3 * 300));
            }

            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, TvSeriesActivity.class);
                    intent.putExtra("seriesName", data.get(position).getName());
                    intent.putExtra("seriesId", data.get(position).getId());
                    intent.putExtra("viewing", "no");
                    ((Activity) context).startActivityForResult(intent, 11005);
                    activity.overridePendingTransition(R.anim.slide_up, R.anim.anim_stay);
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
                final TvSeries tvSeries = data.get(position);
                switch (menuItem.getItemId()) {
                    case R.id.watch:
                        tvSeries.setWatched(null, -1);
                        LocalDataUtil.saveTvSeries(context, tvSeries);
                        notifyItemChanged(position);
                        return true;
                    case R.id.unwatch:
                        tvSeries.setUnwatched(null, -1);
                        LocalDataUtil.saveTvSeries(context, tvSeries);
                        notifyItemChanged(position);
                        return true;
                    case R.id.remove:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        String name_text = "Are you sure you want to remove " + "<font color=#20B5FF>"
                                + tvSeries.getName() + "</font>" + "?<br>"
                                + "(All saved data will be erased)";
                        dialog.setMessage(Html.fromHtml(name_text));
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new RemoveResourcesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvSeries.getId(), tvSeries.getName());
                                data.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            }
                        });
                        dialog.setNegativeButton("No", null);
                        dialog.show();

                        //update display if list is now empty
                        if (parentFragment instanceof FragmentTvSeries) {
                            ((FragmentTvSeries) parentFragment).checkListStatus();
                        }
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private class RemoveResourcesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            //remove entry from user map
            LocalDataUtil.removeFromTvSeriesMap(context, strings[1]);

            //remove tv series data
            LocalDataUtil.removeTvSeriesData(context, strings[0]);

            return strings[1];
        }

        @Override
        protected void onPostExecute(String s) {
            String name_text = "<font color=#20B5FF>" + s + "</font>" + " removed from your list";
            Toast.makeText(context, Html.fromHtml(name_text), Toast.LENGTH_LONG).show();
        }
    }
}
