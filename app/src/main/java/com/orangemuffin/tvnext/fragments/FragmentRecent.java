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
import com.orangemuffin.tvnext.adapters.TvSeriesAirStatusAdapter;
import com.orangemuffin.tvnext.models.Episode;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/* Created by OrangeMuffin on 3/11/2017 */
public class FragmentRecent extends Fragment {

    private StickyListHeadersListView stickyList;
    private TvSeriesAirStatusAdapter adapter;
    private LinearLayout linlaHeaderProgress;
    private List<TvSeries> data = new ArrayList<>();

    // Required empty public constructor
    public FragmentRecent() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        stickyList = (StickyListHeadersListView) rootView.findViewById(R.id.listRecent);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        new ProcessTask().execute();

        return rootView;
    }

    private class ProcessTask extends AsyncTask<Void, Void, List<Episode>> {
        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            stickyList.setVisibility(View.GONE);
        }

        @Override
        protected List<Episode> doInBackground(Void... voids) {
            Map<String, String> tvshow_map = new HashMap<String, String>();
            try {
                File file = new File(getContext().getDir("data", Context.MODE_PRIVATE), "user_map");
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                tvshow_map = (Map<String, String>) inputStream.readObject();
            } catch (Exception e) { }

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
                    List<Episode> episodes = new ArrayList<>();
                    for (int j = 0; j < seasons.size(); j++) {
                        episodes.addAll(seasons.get(j).getEpisodes());
                    }

                    for (int k = episodes.size() - 1; k >= 0; k--) {
                        Episode episode = episodes.get(k);
                        episode.setSeriesName(data.get(i).getName());
                        episode.setSeriesPoster(data.get(i).getPoster());

                        if (!episode.getAirdate().equals("unknown")) {
                            int dateprocess = episode.getNegativeDue();
                            if (dateprocess > 0) {// && episodes.get(j).getNegativeDue() <= 7) {
                                if (dateprocess >= 350) {
                                    break;
                                }
                                dataEpisode.add(episode);
                            }
                        }
                    }
                }
            }

            Collections.sort(dataEpisode, new Comparator<Episode>() {
                @Override
                public int compare(Episode episode, Episode episode2) {
                    return Double.compare(episode.getDue(), episode2.getDue());
                }
            });
            Collections.reverse(dataEpisode);
            return dataEpisode;
        }

        @Override
        protected void onPostExecute(List<Episode> episodes) {
            adapter = new TvSeriesAirStatusAdapter(getContext(), episodes, getActivity());
            stickyList.setAdapter(adapter);
            linlaHeaderProgress.setVisibility(View.GONE);
            stickyList.setVisibility(View.VISIBLE);
        }

    }
}
