package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.TvSeriesEpisodesAdapter;
import com.orangemuffin.tvnext.models.TvSeries;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;

/* Created by OrangeMuffin on 8/1/2017 */
public class EpisodesActivity extends AppCompatActivity {
    private LinearLayout linlaHeaderProgress;
    private ListView episodes_list;
    private ImageView overflow_kite;
    private Toolbar toolbar;
    private TvSeriesEpisodesAdapter adapter;
    private String seasonNumber;
    private int seasonIndex;
    private String seriesId, viewing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        overflow_kite = (ImageView) findViewById(R.id.overflow_kite);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        seasonNumber = getIntent().getStringExtra("seasonNumber");
        seasonIndex = getIntent().getIntExtra("seasonIndex", -1);
        seriesId = getIntent().getStringExtra("seriesId");
        viewing = getIntent().getStringExtra("viewing");

        episodes_list = (ListView) findViewById(R.id.episodes_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Season " + seasonNumber);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overflow_kite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(overflow_kite.getTag().toString()) == 1) {
                    overflow_kite.setImageResource(R.drawable.ic_kite_white_on);
                    overflow_kite.setTag(2);
                    adapter.selectSeason();
                } else {
                    overflow_kite.setImageResource(R.drawable.ic_kite_white_off);
                    overflow_kite.setTag(1);
                    adapter.unselectSeason();
                }
            }
        });

        new ProcessTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, seriesId);
    }

    private class ProcessTask extends AsyncTask<String, Void, TvSeries> {
        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            episodes_list.setVisibility(View.GONE);
        }

        @Override
        protected TvSeries doInBackground(String... strings) {
            TvSeries result = new TvSeries();

            try {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                File mypath = new File(directory, strings[0] + ".ser");

                FileInputStream fis = new FileInputStream(mypath);
                FSTObjectInput in = new FSTObjectInput(fis);
                result = (TvSeries) in.readObject(TvSeries.class);
                in.close();
            } catch (Exception e) { }

            return result;
        }

        @Override
        protected void onPostExecute(TvSeries TvSeries) {
            adapter = new TvSeriesEpisodesAdapter(TvSeries, seasonIndex, viewing, EpisodesActivity.this);
            episodes_list.setAdapter(adapter);
            linlaHeaderProgress.setVisibility(View.GONE);
            episodes_list.setVisibility(View.VISIBLE);

            if (adapter.checkSeason()) {
                overflow_kite.setImageResource(R.drawable.ic_kite_white_on);
                overflow_kite.setTag(2);
            }
        }
    }

    public void toggleSeason(boolean checkSeason) {
        if (checkSeason) {
            overflow_kite.setImageResource(R.drawable.ic_kite_white_on);
            overflow_kite.setTag(2);
        } else {
            overflow_kite.setImageResource(R.drawable.ic_kite_white_off);
            overflow_kite.setTag(1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        super.finish();
        overridePendingTransition(0, R.anim.slide_down);
    }
}
