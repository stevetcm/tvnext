package com.orangemuffin.tvnext.activities;

import android.content.Context;
import android.content.ContextWrapper;
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
import com.orangemuffin.tvnext.thetvdb.TheTvdbDetailsService;

import org.nustaq.serialization.FSTObjectInput;

import java.io.File;
import java.io.FileInputStream;

/* Created by OrangeMuffin on 4/13/2017 */
public class EpisodesActivity extends AppCompatActivity {
    private LinearLayout linlaHeaderProgress;
    private ListView episodes_list;
    private ImageView overflow_add;
    private Toolbar toolbar;
    private TvSeriesEpisodesAdapter adapter;
    private String seasonNumber;
    private int seasonIndex;
    private String seriesId, viewing;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        overflow_add = (ImageView) findViewById(R.id.overflow_add);

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

        if (seasonNumber.equals("0")) {
            overflow_add.setVisibility(View.GONE);
        }

        if (viewing.equals("yes")) {
            overflow_add.setVisibility(View.GONE);
        }

        overflow_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(overflow_add.getTag().toString()) == 1) {
                    overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt2);
                    overflow_add.setTag(2);
                    adapter.selectSeason();
                } else {
                    overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt1);
                    overflow_add.setTag(1);
                    adapter.unselectSeason();
                }
            }
        });

        new ProcessTask().execute(seriesId);
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

            if (viewing.equals("yes")) {
                TheTvdbDetailsService detailsService = new TheTvdbDetailsService();
                result = detailsService.getSeriesDetails(strings[0]);
                result.setNumEpisodes();
            } else {
                try {
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    final File directory = cw.getDir("Series", Context.MODE_PRIVATE);
                    File mypath = new File(directory, strings[0] + ".ser");

                    FileInputStream fis = new FileInputStream(mypath);
                    FSTObjectInput in = new FSTObjectInput(fis);
                    result = (TvSeries) in.readObject(TvSeries.class);
                    in.close();
                } catch (Exception e) {
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(TvSeries TvSeries) {
            adapter = new TvSeriesEpisodesAdapter(TvSeries, seasonIndex, viewing, EpisodesActivity.this);
            episodes_list.setAdapter(adapter);
            linlaHeaderProgress.setVisibility(View.GONE);
            episodes_list.setVisibility(View.VISIBLE);

            if (adapter.checkSeason()) {
                overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt2);
                overflow_add.setTag(2);
            }
        }
    }

    public void toggleSeason(boolean checkSeason) {
        if (checkSeason) {
            overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt2);
            overflow_add.setTag(2);
        } else {
            overflow_add.setBackgroundResource(R.drawable.ic_add_sqrt1);
            overflow_add.setTag(1);
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
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom);
    }
}
