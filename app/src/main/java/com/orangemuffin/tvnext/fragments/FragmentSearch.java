package com.orangemuffin.tvnext.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.TvSeriesDiscoverAdapter;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbSeriesService;
import com.uwetrottmann.thetvdb.entities.Series;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/* Created by OrangeMuffin on 3/11/2017 */
public class FragmentSearch extends Fragment {

    private EditText editText;
    private RecyclerView recyclerView;

    // Required empty public constructor
    public FragmentSearch() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        editText = (EditText) getActivity().findViewById(R.id.title_search);
        editText.requestFocus();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((i == EditorInfo.IME_ACTION_DONE)) {
                    String search = editText.getText().toString();
                    search = search.replaceAll(" ", "+");

                    final SearchTask searchTask = new SearchTask(getActivity());
                    searchTask.execute(search, "en");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (searchTask.getStatus() == AsyncTask.Status.RUNNING) {
                                searchTask.onPostExecute(null);
                                searchTask.cancel(true);
                            }
                        }
                    }, 7000);


                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    editText.setFocusable(false);
                    editText.setFocusableInTouchMode(true);
                    editText.clearFocus();
                    return true;
                }
                return false;
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_search);
        RecyclerView.LayoutManager layoutManager;
        if (getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(rootView.getContext(), 2);
        } else {
            layoutManager = new GridLayoutManager(rootView.getContext(), 1);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);

        return rootView;
    }

    private class SearchTask extends AsyncTask<String, Void, List<TvSeries>> {
        private ProgressDialog mDialog;

        private SearchTask(Context context) {
            mDialog = new ProgressDialog(context);
            mDialog.setMessage("Loading..");
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
            super.onPreExecute();
        }

        @Override
        protected List<TvSeries> doInBackground(String... strings) {
            try {
                List<TvSeries> seriesList = new ArrayList<>();
                TheTvdbSeriesService seriesService = new TheTvdbSeriesService();
                List<Series> searchList = seriesService.getSeriesSearch(strings[0], strings[1]);

                for (Series series : searchList) {
                    TvSeries result = new TvSeries();
                    result.setName(series.seriesName);
                    result.setId(String.valueOf(series.id));
                    result.setBanner(series.banner);
                    result.setName(series.seriesName);
                    result.setNetwork(series.network);
                    result.setDescription(series.overview);

                    seriesList.add(result);
                }
                return seriesList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<TvSeries> tvSeriesList) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if (tvSeriesList != null) {
                recyclerView.setAdapter(new TvSeriesDiscoverAdapter(getContext(), tvSeriesList, getActivity()));
            }
        }
    }
}
