package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.fragments.FragmentAbout;
import com.orangemuffin.tvnext.fragments.FragmentCredits;
import com.orangemuffin.tvnext.fragments.FragmentNotifications;
import com.orangemuffin.tvnext.fragments.FragmentSettings;

/* Created by OrangeMuffin on 8/9/2017 */
public class SettingsActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private boolean originalFragment = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        mFragmentTransaction.replace(R.id.containerView, new FragmentSettings()).commit();
    }

    public void switchFragment(String key) {
        if (key.equals("notification_screen")) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new FragmentNotifications()).commit();
        } else if (key.equals("credits_screen")) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new FragmentCredits()).commit();
        } else if (key.equals("about_screen")) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new FragmentAbout()).commit();
        }
        originalFragment = false;
    }

    public void mainFragment() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new FragmentSettings()).commit();
        originalFragment = true;
    }

    @Override
    public void onBackPressed() {
        if (!originalFragment) {
            mainFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!originalFragment) {
                    mainFragment();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        super.finish();
        overridePendingTransition(0, R.anim.slide_right);
    }
}
