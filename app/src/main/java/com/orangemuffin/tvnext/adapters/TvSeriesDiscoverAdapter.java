package com.orangemuffin.tvnext.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.datafetch.TvSeriesDetailsFetch;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.utils.LocalDataUtil;
import com.orangemuffin.tvnext.utils.MeasurementUtil;
import com.squareup.picasso.Picasso;

import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Created by OrangeMuffin on 7/28/2017 */
public class TvSeriesDiscoverAdapter extends RecyclerView.Adapter<TvSeriesDiscoverAdapter.ViewHolder> {

    private List<TvSeries> data;
    private Context context;
    private Activity activity;

    private Map<String, String> tvshow_map;

    public TvSeriesDiscoverAdapter(Context context, List<TvSeries> data, Activity activity) {
        this.context = context;
        this.data = data;
        this.activity = activity;

        //retrieve current state of user map
        tvshow_map = LocalDataUtil.getTvSeriesMap(context);
    }

    public void add(List<TvSeries> items) {
        int previousDataSize = this.data.size();
        this.data.addAll(items);
        notifyItemRangeInserted(previousDataSize, items.size());
    }

    public void removeAll() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @Override
    public TvSeriesDiscoverAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvseries_card_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TvSeriesDiscoverAdapter.ViewHolder holder, int position) {
        final TvSeries tvSeries = data.get(position);

        if (tvshow_map.containsValue(tvSeries.getId())) {
            holder.overflow_add.setImageResource(R.drawable.ic_check_green);
        } else {
            holder.overflow_add.setImageResource(R.drawable.ic_add_white);
        }

        holder.title.setText(tvSeries.getName());
        holder.network.setText("Network: " + tvSeries.getNetwork());

        String urlBase = "http://thetvdb.com/banners/";
        String urlBanner = tvSeries.getBanner();
        if (urlBanner != null && !urlBanner.equals(urlBase)) {
            Picasso.with(context).load(urlBanner).noFade().fit().centerCrop().into(holder.banner);
        } else {
            Picasso.with(context).load(R.drawable.placeholder_banner).noFade().fit().centerCrop().into(holder.banner);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, network;
        ImageView banner, overflow_add;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            network = (TextView) view.findViewById(R.id.network);
            banner = (ImageView) view.findViewById(R.id.banner);
            overflow_add = (ImageView) view.findViewById(R.id.overflow_kite);

            /* reserve space for listing */
            banner.requestLayout();

            SharedPreferences sp_data = context.getSharedPreferences("PHONEDATA", context.MODE_PRIVATE);
            int width = sp_data.getInt("PHONE_RES", 1080);
            banner.getLayoutParams().height = (int) (((width - 2 * (MeasurementUtil.dpToPixel(4))) / 758) * 140);

            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final TvSeries tvSeries = data.get(position);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(tvSeries.getName());
                    dialog.setMessage(tvSeries.getOverview());
                    dialog.setPositiveButton("Close", null);
                    dialog.show();
                }
            });

            overflow_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final TvSeries tvSeries = data.get(position);

                    if (tvshow_map.containsValue(tvSeries.getId())) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        String name_text = "Are you sure you want to remove " + "<font color=#20B5FF>"
                                + tvSeries.getName() + "</font>" + "?<br>"
                                + "(All saved data will be erased)";
                        dialog.setMessage(Html.fromHtml(name_text));
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new RemoveResourcesTask().execute(tvSeries.getId(), tvSeries.getName());
                            }
                        });
                        dialog.setNegativeButton("No", null);
                        dialog.show();
                    } else {
                        new AddResourcesTask(activity).execute(tvSeries.getId());
                    }
                }
            });
        }
    }

    private class AddResourcesTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        private AddResourcesTask(Activity activity) {
            //need activity to prevent window null exception
            mDialog = new ProgressDialog(activity);
            mDialog.setMessage("Downloading Data..");
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                //fetch newest tv series data
                TvSeries tvSeries = TvSeriesDetailsFetch.fetchData(strings[0]);

                //save serializable tv series data
                LocalDataUtil.saveTvSeries(context, tvSeries);

                //saving poster of tv series
                String urlPoster = tvSeries.getPoster();
                LocalDataUtil.saveTvPoster(context, strings[0], urlPoster);

                //add entry to user map
                LocalDataUtil.addToTvSeriesMap(context, tvSeries.getName(), strings[0]);

                return tvSeries.getName();
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(String seriesName) {
            if (mDialog != null && mDialog.isShowing()) { mDialog.dismiss(); }

            if (seriesName != null) {
                tvshow_map = LocalDataUtil.getTvSeriesMap(context);
                notifyDataSetChanged();

                String name_text = "<font color=#20B5FF>" + seriesName + "</font>" + " added to your list";
                Toast.makeText(context, Html.fromHtml(name_text), Toast.LENGTH_LONG).show();
            }
        }
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
        protected void onPostExecute(String seriesName) {
            tvshow_map = LocalDataUtil.getTvSeriesMap(context);
            notifyDataSetChanged();

            String name_text = "<font color=#20B5FF>" + seriesName + "</font>" + " removed from your list";
            Toast.makeText(context, Html.fromHtml(name_text), Toast.LENGTH_LONG).show();
        }
    }
}
