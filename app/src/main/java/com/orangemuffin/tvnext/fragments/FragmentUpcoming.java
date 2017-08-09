package com.orangemuffin.tvnext.fragments;

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
import com.orangemuffin.tvnext.utils.LocalDataUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/* Created by OrangeMuffin on 7/28/2017 */
public class FragmentUpcoming extends Fragment {

    private StickyListHeadersListView stickyList;
    private TvSeriesAirStatusAdapter adapter;
    private LinearLayout linlaHeaderProgress;
    private List<TvSeries> data = new ArrayList<>();

    //Required empty public constructor
    public FragmentUpcoming() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

        stickyList = (StickyListHeadersListView) rootView.findViewById(R.id.list_upcoming);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        new ProcessTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
            try {
                data.addAll(LocalDataUtil.getTvSeriesLocal(getContext()));
            } catch (Exception e) { }

            List<Episode> dataEpisode = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                seriesLabel: {
                    List<Season> seasons = data.get(i).getSeasons();
                    for (int j = seasons.size()-1; j >= 0; j--) {
                        if (!seasons.get(j).getSeasonNum().equals("0")) {
                            List<Episode> episodes = seasons.get(j).getEpisodes();
                            for (int k = episodes.size() - 1; k >= 0; k--) {
                                Episode episode = episodes.get(k);
                                if (!episode.getAirdate().equals("unknown")) {
                                    if (episode.getDue() > 0) {
                                        dataEpisode.add(episode);
                                    } else if (episode.getDue() < 0) {
                                        break seriesLabel;
                                    }
                                }
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
