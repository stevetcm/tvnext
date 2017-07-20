package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;

/* Created by OrangeMuffin on 5/9/2017 */
public class ChangelogActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView changelog_image;
    private TextView changelog_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Changelog");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changelog_image = (ImageView) findViewById(R.id.changelog_image);
        changelog_text = (TextView) findViewById(R.id.changelog_text);

        String text = "v0.9Beta" + "\n" +
                "- Early release";

        changelog_text.setText(text);
        changelog_image.setImageResource(R.drawable.changelog_image);
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
