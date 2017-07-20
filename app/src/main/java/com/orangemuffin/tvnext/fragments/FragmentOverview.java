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
import com.orangemuffin.tvnext.models.Overview;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbDetailsService;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/* Created by OrangeMuffin on 3/16/2017 */
public class FragmentOverview extends Fragment {
    private StickyListHeadersListView stickyList;
    private TvSeriesOverviewAdapter adapter;
    private LinearLayout linlaHeaderProgress;
    private String seriesId, viewing;
    private Context context;

    // Required empty public constructor
    public FragmentOverview() {
    }

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
        viewing = getArguments().getString("viewing");

        stickyList = (StickyListHeadersListView) rootView.findViewById(R.id.listOverview);

        new ProcessTask().execute(seriesId);

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

            if (viewing.equals("yes")) {
                TheTvdbDetailsService detailsService = new TheTvdbDetailsService();
                result = detailsService.getSeriesDetails(strings[0]);
            } else {
                try {
                    ContextWrapper cw = new ContextWrapper(context);
                    final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                    File mypath = new File(directory, strings[0] + ".ser");

                    FileInputStream fis = new FileInputStream(mypath);
                    FSTObjectInput in = new FSTObjectInput(fis);
                    result = (TvSeries) in.readObject(TvSeries.class);
                    in.close();
                } catch (Exception e) { }
            }

            for (int i = 0; i < 9; i++) {
                Overview overview = new Overview();
                overview.setOrder(i); //set header id
                overview.setId(result.getId());
                if (i == 0) {
                    overview.setHeader(result.getName());
                    overview.setFanarts(result.getFanarts());
                } else if (i == 1) {
                    overview.setHeader("Next Unaired Episode");
                    overview.setText(result.getNearestEpisode("overview1"));
                    overview.setText2(result.getNearestEpisode("overview2"));
                } else if (i == 2) {
                    overview.setHeader("Description");
                    overview.setText(result.getDescription());
                } else if (i == 3) {
                    overview.setHeader("Genre");
                    overview.setText(result.getGenre());
                } else if (i == 4) {
                    overview.setHeader("Network");
                    overview.setText(result.getNetwork() + " / " + result.getYear());
                } else if (i == 5) {
                    overview.setHeader("Casts");
                    overview.setActors(result.getActors());
                } else if (i == 6) {
                    overview.setHeader("Trakt Rating");
                    overview.setText(result.getTraktRating() + "/10.0 (" + result.getTraktVotes() + " Votes)");
                } else if (i == 7) {
                    overview.setHeader("More");
                    overview.setText("Open on Trakt (Coming Soon)");
                } else if (i == 8) {
                    overview.setHeader("Last Edit on TheTVDB");
                    if (result.getLastUpdated() != null) {
                        Date date = new Date(Long.valueOf(result.getLastUpdated()) * 1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String formatted = sdf.format(date);
                        overview.setText(formatted);
                    } else {
                        overview.setText("Information not available");
                    }
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
