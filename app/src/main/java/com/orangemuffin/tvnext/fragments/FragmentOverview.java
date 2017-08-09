package com.orangemuffin.tvnext.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.TvSeriesOverviewAdapter;
import com.orangemuffin.tvnext.datafetch.TvSeriesDetailsFetch;
import com.orangemuffin.tvnext.models.Overview;
import com.orangemuffin.tvnext.models.TvSeries;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/* Created by OrangeMuffin on 7/31/2017 */
public class FragmentOverview extends Fragment {

    private StickyListHeadersListView stickyList;
    private TvSeriesOverviewAdapter adapter;
    private LinearLayout linlaHeaderProgress;
    private String seriesId, viewing;
    private Context context;

    //Required empty public constructor
    public FragmentOverview() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        context = rootView.getContext();

        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        seriesId = getArguments().getString("seriesId");
        //viewing = getArguments().getString("viewing");

        stickyList = (StickyListHeadersListView) rootView.findViewById(R.id.listOverview);

        new ProcessTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, seriesId);

        return rootView;
    }

    private class ProcessTask extends AsyncTask<String, Void, List<Overview>> {

        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            stickyList.setVisibility(View.GONE);
        }

        @Override
        protected List<Overview> doInBackground(String... strings) {
            List<Overview> data = new ArrayList<>();
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

            for (int i = 0; i < 9; i++) {
                Overview overview = new Overview();
                overview.setId(i); //set header id
                if (i == 0) {
                    overview.setHeader(result.getName());
                    overview.setBackgrounds(result.getBackgrounds());
                } else if (i == 1) {
                    overview.setHeader("Next Unaired Episode");
                    overview.setText(result.getNearestEpisode());
                } else if (i == 2) {
                    overview.setHeader("Description");
                    overview.setText(result.getOverview());
                } else if (i == 3) {
                    overview.setHeader("Genre");
                    overview.setText(result.getGenre());
                } else if (i == 4) {
                    overview.setHeader("Network");
                    overview.setText(result.getNetwork());
                } else if (i == 5) {
                    overview.setHeader("Casts");
                    overview.setActors(result.getActors());
                } else if (i == 6) {
                    overview.setHeader("Trakt Rating");
                    overview.setText(result.getTraktRating());
                } else if (i == 7) {
                    overview.setHeader("More");
                    overview.setText("Open on Trakt (Coming Soon)");
                } else if (i == 8) {
                    overview.setHeader("Last Updated");
                    overview.setText(result.getLastUpdated());
                }
                data.add(overview);
            }

            return data;
        }

        @Override
        protected void onPostExecute(List<Overview> overviews) {
            adapter = new TvSeriesOverviewAdapter(context, overviews);
            stickyList.setAdapter(adapter);
            linlaHeaderProgress.setVisibility(View.GONE);
            stickyList.setVisibility(View.VISIBLE);
        }
    }
}
