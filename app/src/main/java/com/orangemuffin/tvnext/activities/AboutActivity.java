package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;

/* Created by OrangeMuffin on 5/9/2017 */
public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        about = (TextView) findViewById(R.id.about);

        String about_text = "This app uses data and images provided by TheTVDB licensed under CC BY-NC 4.0." + "\n" +
                "https://thetvdb.com/?tab=tos" + "\n" +
                "http://creativecommons.org/licenses/by-nc/4.0" + "\n\n" +
                "This product uses the TMDB API but is not endorsed or certified by TMDB" +  "\n" +
                "https://www.themoviedb.org/terms-of-use" + "\n" +
                "https://www.themoviedb.org/documentation/api/terms-of-use" + "\n\n" +
                "This app uses data and images provided by trakt.tv" + "\n" +
                "https://trakt.tv/terms" + "\n\n" +
                "Support them in any way you can! :)";

        about.setText(about_text);
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
    }
}
