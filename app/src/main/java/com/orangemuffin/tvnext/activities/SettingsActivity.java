package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.adapters.SettingsCategoryAdapter;

/* Created by OrangeMuffin on 3/25/2017 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private SettingsCategoryAdapter adapter;
    private ListView settings_list;

    private String[] settingsCategory = {"Notification & Update", "About", "Changelog"};
    private String[] settingsDetails = {};

    private ImageView logos_api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings_list = (ListView) findViewById(R.id.settings_list);

        adapter = new SettingsCategoryAdapter(settingsCategory, settingsDetails, getApplicationContext());
        settings_list.setAdapter(adapter);

        settings_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                    startActivityForResult(intent, 1003); //NotificationActivity
                } else if (i == 1) {
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivityForResult(intent, 1004); //AboutActivity
                } else if (i == 2) {
                    Intent intent = new Intent(getApplicationContext(), ChangelogActivity.class);
                    startActivityForResult(intent, 1005); //ChangelogActivity
                }
            }
        });

        logos_api = (ImageView) findViewById(R.id.logos_api);
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
        setResult(Activity.RESULT_CANCELED,returnIntent);

        super.finish();
        overridePendingTransition(0, R.anim.slide_right);
    }
}
