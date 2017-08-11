package com.orangemuffin.tvnext.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.MainActivity;
import com.orangemuffin.tvnext.adapters.TvSeriesDetailsAdapter;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.utils.LocalDataUtil;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* Created by OrangeMuffin on 7/28/2017 */
public class FragmentTvSeries extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout linlaHeaderProgress;
    private TvSeriesDetailsAdapter adapter;
    private List<TvSeries> data = new ArrayList<>();

    private TextView notvseriesText;

    //Required empty public constructor
    public FragmentTvSeries() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tvseries, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).switchTabManager(R.id.discover);
                ((MainActivity) getActivity()).setNavBarItem(2);
            }
        });

        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 3));

        adapter = new TvSeriesDetailsAdapter(getContext(), new ArrayList<TvSeries>(), getActivity(), this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);

        notvseriesText = (TextView) rootView.findViewById(R.id.no_tvseries_text);

        new ProcessTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return rootView;
    }

    private class ProcessTask extends AsyncTask<Void, Void, List<TvSeries>> {
        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected List<TvSeries> doInBackground(Void... voids) {
            try {
                List<TvSeries> seriesList = LocalDataUtil.getTvSeriesLocal(getContext());
                for (TvSeries tvSeries : seriesList) {
                    tvSeries.setEpisodesLeft();
                }
                data.addAll(seriesList);
            } catch (Exception e) { }
            return data;
        }

        protected void onPostExecute(List<TvSeries> tvSeriesList) {
            if (tvSeriesList != null) { adapter.add(tvSeriesList); }
            linlaHeaderProgress.setVisibility(View.GONE);
            checkListStatus();
        }
    }

    public void checkListStatus() {
        Map<String, String> tvshow_map = LocalDataUtil.getTvSeriesMap(getContext());
        if (tvshow_map.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            notvseriesText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            notvseriesText.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver responseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String seriesId = intent.getStringExtra("seriesId");
            String seriesName = intent.getStringExtra("seriesName");
            new ProcessTvSeries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, seriesId);
            //Toast.makeText(getContext(), seriesName + " updated", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(responseReceiver, new IntentFilter("UPDATE_REFRESH"));
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11005) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("seriesId");
                new ProcessTvSeries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ProcessTvSeries extends AsyncTask<String, Void, TvSeries> {
        @Override
        protected TvSeries doInBackground(String... strings) {
            TvSeries result = new TvSeries();

            try {
                ContextWrapper cw = new ContextWrapper(getContext());
                final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                File mypath = new File(directory, strings[0] + ".ser");

                FileInputStream fis = new FileInputStream(mypath);
                FSTObjectInput in = new FSTObjectInput(fis);
                result = (TvSeries) in.readObject(TvSeries.class);
                in.close();
            } catch (Exception e) { }

            result.setEpisodesLeft();
            return result;
        }

        @Override
        protected void onPostExecute(TvSeries tvSeries) {
            if (tvSeries != null) {
                adapter.modify(tvSeries);
                recyclerView.setVisibility(View.VISIBLE);
                notvseriesText.setVisibility(View.GONE);
            }
        }
    }
}
