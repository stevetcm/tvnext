package com.orangemuffin.tvnext.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.EpisodesActivity;
import com.orangemuffin.tvnext.adapters.TvSeriesSeasonsAdapter;
import com.orangemuffin.tvnext.models.Season;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbDetailsService;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Created by OrangeMuffin on 3/16/2017 */
public class FragmentSeasons extends Fragment {
    private LinearLayout linlaHeaderProgress;
    private ListView seasons_list;
    private TvSeriesSeasonsAdapter adapter;
    private List<Season> seasons = new ArrayList<>();
    private String seriesId, viewing;
    private Context context;

    // Required empty public constructor
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

        seasons_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), EpisodesActivity.class);
                intent.putExtra("seriesId", seriesId);
                intent.putExtra("viewing", viewing);
                intent.putExtra("seasonNumber", seasons.get(i).getSeasonNum());
                intent.putExtra("seasonIndex", i);
                context.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_top, R.anim.anim_stay);
            }
        });

        if (viewing.equals("yes")) {
            new ProcessTask().execute(seriesId);
        }

        return rootView;
    }

    private class ProcessTask extends AsyncTask<String, Void, List<Season>> {
        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            seasons_list.setVisibility(View.GONE);
        }

        @Override
        protected List<Season> doInBackground(String... strings) {
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
                } catch (Exception e) {
                }
            }

            seasons = result.getSeasons();
            Collections.reverse(seasons);

           return seasons;
        }

        @Override
        protected void onPostExecute(List<Season> seasons) {
            adapter = new TvSeriesSeasonsAdapter(seasons, context, viewing);
            seasons_list.setAdapter(adapter);
            linlaHeaderProgress.setVisibility(View.GONE);
            seasons_list.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewing.equals("no")) {
            new ProcessTask().execute(seriesId);
        }
    }
}
