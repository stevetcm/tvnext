package com.orangemuffin.tvnext.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.MainActivity;
import com.orangemuffin.tvnext.adapters.TvSeriesDiscoverAdapter;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.trakt.TraktSeriesService;
import com.paginate.Paginate;

import java.util.List;

/* Created by OrangeMuffin on 3/11/2017 */
public class FragmentExplore extends Fragment implements Paginate.Callbacks{

    private Spinner categoriesSpinner;
    private LinearLayout linlaHeaderProgress;
    private RecyclerView recyclerView;
    private TvSeriesDiscoverAdapter adapter;
    private TextView trakterror, footer_credit;

    private String currentCategory = "Trending";
    private boolean loading = false;
    private int page = 1;
    private Paginate paginate;
    protected int totalPages = 40;
    protected boolean addLoadingRow = true;

    private boolean initialStart = true;

    public GetTvListTask downloader;

    // Required empty public constructor
    public FragmentExplore() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        trakterror = (TextView) rootView.findViewById(R.id.trakterror);

        footer_credit = (TextView) rootView.findViewById(R.id.footer_credit);

        footer_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://creativecommons.org/licenses/by-nc/4.0/";
                Intent web = new Intent(Intent.ACTION_VIEW);
                web.setData(Uri.parse(url));
                startActivity(web);
            }
        });

        String text = "Trakt service is unresponsive.<br>Try again later or try using the<br>"
                + "<font color=#00C3B0>" + "SEARCH" + "</font>" + " tab.";
        trakterror.setText(Html.fromHtml(text));
        trakterror.setVisibility(View.GONE);

        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_explore);

        String[] category_items = new String[] { "Trending", "Popular", "Watched", "Collected", "Anticipated" };
        categoriesSpinner = (Spinner) rootView.findViewById(R.id.categories_spinner);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.spinner_element, category_items);
        categoriesSpinner.setAdapter(categoriesAdapter);

        downloader = new GetTvListTask(this);

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCategory = adapterView.getItemAtPosition(i).toString().toLowerCase();
                setupPagination(currentCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        return rootView;
    }

    public void setupPagination(String category) {
        if (paginate != null) { paginate.unbind(); }

        RecyclerView.LayoutManager layoutManager;
        if (getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        }
        else {
            layoutManager = new GridLayoutManager(getContext(), 1);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);

        loading = true;
        page = 1;

        //changing category - reset pagination
        if (!initialStart) {
            paginate = Paginate.with(recyclerView, this)
                    .addLoadingListItem(addLoadingRow)
                    .build();

            adapter.removeAll();
        }

        downloader.cancel(true);
        downloader = new GetTvListTask(this);
        downloader.execute(category, String.valueOf(page));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (downloader.getStatus() == AsyncTask.Status.RUNNING) {
                    Toast.makeText(getContext(), "Trakt service is running a bit slow", Toast.LENGTH_LONG).show();
                }
            }
        }, 7500);

        /*new GetTvListTask(this).execute(category, String.valueOf(page));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (downloader.getStatus() == AsyncTask.Status.RUNNING) {
                    downloader.cancel(true);
                    trakterror.setVisibility(View.VISIBLE);
                    linlaHeaderProgress.setVisibility(View.GONE);
                }
            }
        }, 10000);*/
    }

    @Override
    public void onLoadMore() {
        loading = true;
        page++;

        downloader = new GetTvListTask();
        downloader.execute(currentCategory, String.valueOf(page));
    }

    @Override
    public boolean isLoading() { return loading; }

    @Override
    public boolean hasLoadedAllItems() { return page == totalPages; }

    private class GetTvListTask extends AsyncTask<String, Void, List<TvSeries>> {
        public Paginate.Callbacks activity;

        @Override
        protected void onPreExecute() {
            if (page == 1) {
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            super.onPreExecute();
        }

        public GetTvListTask() { }

        public GetTvListTask(Paginate.Callbacks activity) { this.activity = activity; }

        @Override
        protected List<TvSeries> doInBackground(String... strings) {
            try {
                TraktSeriesService seriesService = new TraktSeriesService();
                return seriesService.performTask(strings[0], strings[1]);
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(List<TvSeries> tvSeriesList) {
            if (tvSeriesList != null) {
                if (initialStart && page == 1) {
                    adapter = new TvSeriesDiscoverAdapter(getContext(), tvSeriesList, getActivity());
                    recyclerView.setAdapter(adapter);

                    paginate = Paginate.with(recyclerView, activity)
                            .addLoadingListItem(addLoadingRow)
                            .build();
                    initialStart = false;
                } else {
                    adapter.add(tvSeriesList);
                }
                if (page == 1) {
                    linlaHeaderProgress.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
            loading = false;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            ((MainActivity) getActivity()).makeSearchBarHidden();
            if (!isVisibleToUser) {
                ((MainActivity) getActivity()).makeSearchBarVisible();
            }
        }
    }
}
