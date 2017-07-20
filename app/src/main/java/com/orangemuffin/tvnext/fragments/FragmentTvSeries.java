package com.orangemuffin.tvnext.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.MainActivity;
import com.orangemuffin.tvnext.adapters.TvSeriesDetailsAdapter;
import com.orangemuffin.tvnext.layoutmanager.SimpleGridLayoutManager;
import com.orangemuffin.tvnext.models.TvSeries;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit2.Call;

/* Created by OrangeMuffin on 3/10/2017 */
public class FragmentTvSeries extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout linlaHeaderProgress;
    private TvSeriesDetailsAdapter adapter;
    private List<TvSeries> data = new ArrayList<>();

    public Map<String, String> tvshow_map = new HashMap<String, String>();

    // Required empty public constructor
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
        RecyclerView.LayoutManager mLayoutManager;
        if (getResources().getBoolean(R.bool.isTablet)) {
            mLayoutManager = new SimpleGridLayoutManager(rootView.getContext(), 5);
        } else {
            mLayoutManager = new SimpleGridLayoutManager(rootView.getContext(), 3);
        }
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new TvSeriesDetailsAdapter(getContext(), new ArrayList<TvSeries>(), getActivity(), getParentFragment());
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);

        new ProcessTask().execute();

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
                File file = new File(getContext().getDir("data", Context.MODE_PRIVATE), "user_map");
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                tvshow_map = (Map<String, String>) inputStream.readObject();
            } catch (Exception e) {
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

            return data;
        }

        protected void onPostExecute(List<TvSeries> tvSeriesList) {
            if (tvSeriesList != null) {
                adapter.add(tvSeriesList);
            }

            linlaHeaderProgress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //only one item is going to change here?
        //can use sharedpreferences to communicate which tvseries is being
        //changed and use adapter.modifystate to do the change and reload
        //the data in FragmentTvSeries to display up to date data.
        SharedPreferences sp_data = getContext().getSharedPreferences("PHONEDATA", getContext().MODE_PRIVATE);
        String value = sp_data.getString("TVSERIES_MODIFY", "NULL");

        if (!value.equals("NULL")) {
            try {
                File file = new File(getContext().getDir("data", Context.MODE_PRIVATE), "user_map");
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                tvshow_map = (Map<String, String>) inputStream.readObject();

                if (tvshow_map.containsValue(value)) {
                    new ProcessTvSeries().execute(value);
                }
            } catch (Exception e) { }
        }
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(TvSeries tvSeries) {
            if (tvSeries != null) {
                adapter.modify(tvSeries);
            }
            SharedPreferences sp_data = getContext().getSharedPreferences("PHONEDATA", getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sp_data.edit();
            editor.putString("TVSERIES_MODIFY", "NULL");
            editor.apply();
        }
    }
}
