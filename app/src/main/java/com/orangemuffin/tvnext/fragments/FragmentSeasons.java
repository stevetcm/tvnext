package com.orangemuffin.tvnext.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.TvSeriesSeasonsAdapter;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Created by OrangeMuffin on 7/31/2017 */
public class FragmentSeasons extends Fragment {

    private LinearLayout linlaHeaderProgress;
    private ListView seasons_list;
    private TvSeriesSeasonsAdapter adapter;
    private String seriesId, viewing;
    private Context context;

    //Required empty public constructor
    public FragmentSeasons() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seasons, container, false);

        context = rootView.getContext();

        seasons_list = (ListView) rootView.findViewById(R.id.seasons_list);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        seriesId = getArguments().getString("seriesId");
        viewing = getArguments().getString("viewing");

        new ProcessTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, seriesId);

        return rootView;
    }

    private class ProcessTask extends AsyncTask<String, Void, TvSeries> {
        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            seasons_list.setVisibility(View.GONE);
        }

        @Override
        protected TvSeries doInBackground(String... strings) {
            TvSeries result = new TvSeries();

            try {
                ContextWrapper cw = new ContextWrapper(context);
                final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                File mypath = new File(directory, strings[0] + ".ser");

                FileInputStream fis = new FileInputStream(mypath);
                FSTObjectInput in = new FSTObjectInput(fis);
                result = (TvSeries) in.readObject(TvSeries.class);
                in.close();
            } catch (Exception e) { }

            return result;
        }

        @Override
        protected void onPostExecute(TvSeries tvSeries) {
            adapter = new TvSeriesSeasonsAdapter(tvSeries, context, getActivity(), seriesId, viewing);
            seasons_list.setAdapter(adapter);
            linlaHeaderProgress.setVisibility(View.GONE);
            seasons_list.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11006) {
            if (resultCode == Activity.RESULT_CANCELED) {
                new ProcessTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, seriesId);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
