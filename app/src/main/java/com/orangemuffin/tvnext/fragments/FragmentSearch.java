package com.orangemuffin.tvnext.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.TvSeriesDiscoverAdapter;
import com.orangemuffin.tvnext.models.TvSeries;
import com.orangemuffin.tvnext.thetvdb.TheTvdbSeriesService;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 7/28/2017 */
public class FragmentSearch extends Fragment {

    private EditText editText;
    private RecyclerView recyclerView;
    private TvSeriesDiscoverAdapter adapter;

    private TextView footer_credit;

    //Required empty public constructor
    public FragmentSearch() { }

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

                    final SearchTask searchTask = new SearchTask(getActivity());
                    searchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search, "en");

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
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 1));
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);

        footer_credit = (TextView) rootView.findViewById(R.id.footer_credit);
        footer_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "This app uses data and images by TheTVDB licensed under CC BY-NC 4.0\n\n" +
                        "(https://thetvdb.com/?tab=tos)\n" +
                        "(http://creativecommons.org/licenses/by-nc/4.0).";

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("TheTVDB.com");
                dialog.setMessage(text);
                dialog.setPositiveButton("Close", null);
                dialog.show();
            }
        });

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
                return TheTvdbSeriesService.getSeriesSearch(strings[0], strings[1]);
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(List<TvSeries> seriesList) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            if (seriesList != null) {
                adapter = new TvSeriesDiscoverAdapter(getContext(), seriesList, getActivity());
                recyclerView.setAdapter(adapter);
                footer_credit.setVisibility(View.VISIBLE);
            }
        }
    }
}
