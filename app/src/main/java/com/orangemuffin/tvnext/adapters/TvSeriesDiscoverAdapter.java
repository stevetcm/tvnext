package com.orangemuffin.tvnext.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.TvSeriesActivity;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbDetailsService;
import com.squareup.picasso.Picasso;

import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/* Created by OrangeMuffin on 3/12/2017 */
public class TvSeriesDiscoverAdapter extends RecyclerView.Adapter<TvSeriesDiscoverAdapter.ViewHolder> {
    private List<TvSeries> data;
    private Context context;
    private ImageLoader imageLoader;
    private Activity activity;

    private String currentId;

    private Picasso picasso;

    private Map<String, String> tvshow_map = new HashMap<String, String>();

    public TvSeriesDiscoverAdapter(Context context, List<TvSeries> data, Activity activity) {
        this.context = context;
        this.data = data;
        this.activity = activity;

        //retrieve current state of user map
        try {
            File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            tvshow_map = (Map<String, String>) inputStream.readObject();
        } catch (Exception e) { }

        imageLoader = ImageLoader.getInstance();
        picasso = new Picasso.Builder(context).executor(Executors.newSingleThreadExecutor()).build();
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
    public TvSeriesDiscoverAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tvseries_card_banner, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TvSeries tvShow = data.get(i);

        if (!tvShow.isTvdbready()) {
            viewHolder.overflow_add.setVisibility(View.GONE);
        }

        if (tvshow_map.containsValue(tvShow.getId())) {
            viewHolder.overflow_add.setBackgroundResource(R.drawable.ic_add_tick);
            viewHolder.overflow_add.setTag(2);
        } else {
            viewHolder.overflow_add.setBackgroundResource(R.drawable.ic_add_plus);
            viewHolder.overflow_add.setTag(1);
        }

        viewHolder.title.setText(tvShow.getName());
        viewHolder.network.setText("Network: " + tvShow.getNetwork());
        String urlBase = "http://thetvdb.com/banners/";
        String urlBanner = tvShow.getBanner();
        if (urlBanner != null) {
            if (urlBanner.equals("")) {
                picasso.with(context).load(R.drawable.placeholder_landscape).noFade().fit().centerCrop().into(viewHolder.banner);
            } else {
                picasso.with(context).load(urlBase + urlBanner).noFade().fit().centerCrop().into(viewHolder.banner);
            }
        } else {
            picasso.with(context).load(R.drawable.placeholder_landscape).noFade().fit().centerCrop().into(viewHolder.banner);
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
            overflow_add = (ImageView) view.findViewById(R.id.overflow_add);

            /* reserve space for listing */
            banner.requestLayout();

            SharedPreferences sp_data = context.getSharedPreferences("PHONEDATA", context.MODE_PRIVATE);
            int width = sp_data.getInt("PHONE_RES", 1080);

            if (context.getResources().getBoolean(R.bool.isTablet)) {
                banner.getLayoutParams().height = (int) (((width - 4 * (dpToPixel(4))) / (2 * 758)) * 140);
            } else {
                banner.getLayoutParams().height = (int) (((width - 2 * (dpToPixel(4))) / 758) * 140);
            }

            /* onclick listener - don't make banner clickable in layout xml*/
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final TvSeries currentTvShow = data.get(position);
                    LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                    View mView = layoutInflater.inflate(R.layout.dialog_tvseries, null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                    alertDialogBuilder.setView(mView);

                    if (!currentTvShow.isTvdbready()) {
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                            }
                                        });
                    } else if (tvshow_map.containsValue(currentTvShow.getId())) {
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Remove",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                AlertDialog.Builder alertDialogBuilderInner = new AlertDialog.Builder(context);
                                                String name_text = "Are you sure you want to remove " + "<font color=#20B5FF>"
                                                        + currentTvShow.getName() + "</font>" + "?<br>"
                                                        + "(All saved data will be erased)";
                                                alertDialogBuilderInner.setMessage(Html.fromHtml(name_text));
                                                alertDialogBuilderInner
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                new RemoveResourcesTask(activity).execute(currentTvShow.getId(), currentTvShow.getName());
                                                                overflow_add.setBackgroundResource(R.drawable.ic_add_plus);
                                                                overflow_add.setTag(1);
                                                            }
                                                        })
                                                        .setNegativeButton("No", null);
                                                alertDialogBuilderInner.show();
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                            }
                                        })
                                .setNeutralButton("Open",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                Intent intent = new Intent(context, TvSeriesActivity.class);
                                                intent.putExtra("seriesName", currentTvShow.getName());
                                                intent.putExtra("seriesId", currentTvShow.getId());
                                                intent.putExtra("viewing", "no");
                                                activity.startActivity(intent);
                                                activity.overridePendingTransition(R.anim.slide_top, R.anim.anim_stay);
                                            }
                                        });
                    } else {
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Add",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                currentId = currentTvShow.getId();
                                                new AddResourcesTask(activity).execute(currentTvShow.getId());
                                                overflow_add.setBackgroundResource(R.drawable.ic_add_tick);
                                                overflow_add.setTag(2);
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                            }
                                        })
                                .setNeutralButton("Open",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                if (isNetworkAvailable()) {
                                                    Intent intent = new Intent(context, TvSeriesActivity.class);
                                                    intent.putExtra("seriesName", currentTvShow.getName());
                                                    intent.putExtra("seriesId", currentTvShow.getId());
                                                    intent.putExtra("viewing", "yes");
                                                    context.startActivity(intent);
                                                } else {
                                                    String name_text = "<font color=#FF3A3A>" + "Internet connection not found" + "</font>";
                                                    Toast.makeText(context, Html.fromHtml(name_text), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                    }

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(true);

                    TextView title = (TextView) mView.findViewById(R.id.dialogTitle);
                    title.setText(currentTvShow.getName());

                    TextView description = (TextView) mView.findViewById(R.id.dialogDescription);
                    description.setText(currentTvShow.getDescription());

                    alertDialog.show();
                }
            });

            overflow_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final TvSeries currentTvShow = data.get(position);

                    if (tvshow_map.containsValue(currentTvShow.getId())) {
                        AlertDialog.Builder alertDialogBuilderInner = new AlertDialog.Builder(context);
                        String name_text = "Are you sure you want to remove " + "<font color=#20B5FF>"
                                + currentTvShow.getName() + "</font>" + "?<br>"
                                + "(All saved data will be erased)";
                        alertDialogBuilderInner.setMessage(Html.fromHtml(name_text));
                        alertDialogBuilderInner
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new RemoveResourcesTask(activity).execute(currentTvShow.getId(), currentTvShow.getName());
                                        overflow_add.setBackgroundResource(R.drawable.ic_add_plus);
                                        overflow_add.setTag(1);
                                    }
                                })
                                .setNegativeButton("No", null);
                        alertDialogBuilderInner.show();
                    } else {
                        overflow_add.setBackgroundResource(R.drawable.ic_add_tick);
                        overflow_add.setTag(2);
                        currentId = currentTvShow.getId();
                        new AddResourcesTask(activity).execute(currentTvShow.getId());
                    }
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                TheTvdbDetailsService detailsService = new TheTvdbDetailsService();
                final TvSeries tvSeries = detailsService.getSeriesDetails(strings[0]);

                /*if (tvSeries.getPoster() == null) {
                    return "ERR_NETWORK_CHANGED";
                }*/

                tvSeries.getBehindNumber2(); //will instantiate variable and get saved

                //retrieve current state of user map
                try {
                    File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
                    ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                    tvshow_map = (Map<String, String>) inputStream.readObject();
                } catch (Exception e) {
                }

                //get internal location: data/Series
                ContextWrapper cw = new ContextWrapper(context);
                final File directory = cw.getDir("Series", Context.MODE_PRIVATE);

                //save serializable tv series data
                File mypath = new File(directory, tvSeries.getId() + ".ser");
                if (mypath.exists()) mypath.delete();
                try {
                    FileOutputStream fos = new FileOutputStream(mypath);
                    FSTObjectOutput out = new FSTObjectOutput(fos);
                    out.writeObject(tvSeries, TvSeries.class);
                    out.close();
                } catch (Exception e) { }

                //saving poster of tv series
                String urlBase = "http://thetvdb.com/banners/_cache/";
                String urlPoster = tvSeries.getPoster();
                if (urlPoster != null && !urlPoster.equals("")) {
                    imageLoader.loadImage(urlBase + urlPoster, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            //save image poster of tv show
                            File mypath_img = new File(directory, tvSeries.getId() + ".jpg");
                            if (mypath_img.exists()) mypath_img.delete();
                            try {
                                FileOutputStream fos = new FileOutputStream(mypath_img);
                                loadedImage.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                return tvSeries.getName();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            if (s != null) {
                //add entry to user map
                try {
                    File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                    tvshow_map.put(s, currentId);
                    outputStream.writeObject(tvshow_map);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) { }
            }

            String name_text;
            if (!s.equals("ERR_NETWORK_CHANGED")) {
                name_text = "<font color=#20B5FF>" + s + "</font>" + " added to your list";
            } else {
                name_text = "<font color=#FF3A3A>" + "Something went wrong, try again later" + "</font>";
                notifyDataSetChanged();
            }
            Toast.makeText(context, Html.fromHtml(name_text), Toast.LENGTH_LONG).show();
        }
    }

    private class RemoveResourcesTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        private RemoveResourcesTask(Activity activity) {
            //need activity to prevent window null exception
            mDialog = new ProgressDialog(activity);
            mDialog.setMessage("Removing Data..");
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            //retrieve current state of user map
            try {
                File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                tvshow_map = (Map<String, String>) inputStream.readObject();
            } catch (Exception e) {
            }

            //remove entry from user map
            try {
                File file = new File(context.getDir("data", Context.MODE_PRIVATE), "user_map");
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                tvshow_map.remove(strings[1]);
                outputStream.writeObject(tvshow_map);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }

            //get internal location data/series
            ContextWrapper cw = new ContextWrapper(context);
            final File directory = cw.getDir("Series", Context.MODE_PRIVATE);

            //remove serializable tv show data
            File mypath = new File(directory, strings[0] + ".ser");
            if (mypath.exists()) mypath.delete();

            //remove image poster of tv show
            File mypath_img = new File(directory, strings[0] + ".jpg");
            if (mypath_img.exists()) mypath_img.delete();
            Picasso.with(context).invalidate(mypath_img);

            return strings[1];
        }

        @Override
        protected void onPostExecute(String s) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            String name_text = "<font color=#20B5FF>" + s + "</font>" + " removed from your list";
            Toast.makeText(context, Html.fromHtml(name_text), Toast.LENGTH_LONG).show();
        }
    }

    public float dpToPixel(int dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
