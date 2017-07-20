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
import android.widget.TextView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.TvSeriesAirStatusAdapter;
import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/* Created by OrangeMuffin on 3/11/2017 */
public class FragmentToday extends Fragment {

    private StickyListHeadersListView stickyList;
    private TvSeriesAirStatusAdapter adapter;
    private LinearLayout linlaHeaderProgress;
    private TextView noshowtoday;
    private List<TvSeries> data = new ArrayList<>();

    // Required empty public constructor
    public FragmentToday() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        stickyList = (StickyListHeadersListView) rootView.findViewById(R.id.list);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        noshowtoday = (TextView) rootView.findViewById(R.id.noshowtoday);

        new ProcessTask().execute();

        return rootView;
    }

    private class ProcessTask extends AsyncTask<Void, Void, List<Episode>> {
        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            stickyList.setVisibility(View.GONE);
            noshowtoday.setVisibility(View.GONE);
        }

        @Override
        protected List<Episode> doInBackground(Void... voids) {
            Map<String, String> tvshow_map = new HashMap<String, String>();
            try {
                File file = new File(getContext().getDir("data", Context.MODE_PRIVATE), "user_map");
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                tvshow_map = (Map<String, String>) inputStream.readObject();
            } catch (Exception e) { }

            if (tvshow_map.size() == 0) {
                noshowtoday.setText("No show to check");
            }

            SortedSet<String> keys = new TreeSet<>(tvshow_map.keySet());

            for (String key : keys) {
                String value = tvshow_map.get(key);
                if (!value.equals("")) {
                    try {
                        ContextWrapper cw = new ContextWrapper(getContext());
                        final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                        File mypath = new File(directory, value + ".ser");

                        FileInputStream fis = new FileInputStream(mypath);
                        FSTObjectInput in = new FSTObjectInput(fis);
                        TvSeries result = (TvSeries) in.readObject(TvSeries.class);
                        in.close();
                        data.add(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            List<Episode> dataEpisode = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                List<Season> seasons = data.get(i).getSeasons();
                if (seasons.size() != 0) {
                    int lastItem = seasons.size() - 1;
                    List<Episode> episodes = seasons.get(lastItem).getEpisodes();
                    for (int j = 0; j < episodes.size(); j++) {
                        Episode episode = episodes.get(j);
                        if (!episode.getAirdate().equals("unknown")) {
                            if (episode.getDue() == 0) {
                                dataEpisode.add(episode);
                            } else if (episode.getDue() > 0) {
                                break;
                            }
                        }
                    }
                }
            }
            return dataEpisode;
        }

        @Override
        protected void onPostExecute(List<Episode> episodes) {
            if (episodes.size() != 0) {
                adapter = new TvSeriesAirStatusAdapter(getContext(), episodes, getActivity());
                stickyList.setAdapter(adapter);
                stickyList.setVisibility(View.VISIBLE);
            } else {
                noshowtoday.setVisibility(View.VISIBLE);
            }
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }
}