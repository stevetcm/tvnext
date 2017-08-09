package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.fragments.FragmentOverview;
import com.orangemuffin.tvnext.fragments.FragmentSeasons;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 7/31/2017 */
public class TvSeriesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String dataName, dataId, viewing;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvseries);

        dataName = getIntent().getExtras().getString("seriesName");
        dataId = getIntent().getExtras().getString("seriesId");
        viewing = getIntent().getExtras().getString("viewing");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(dataName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle mybundle = new Bundle();
        mybundle.putString("seriesId", dataId);
        mybundle.putString("viewing", viewing);

        FragmentOverview overview = new FragmentOverview();
        overview.setArguments(mybundle);
        adapter.addFragment(overview, "Overview");

        FragmentSeasons episodes = new FragmentSeasons();
        episodes.setArguments(mybundle);
        adapter.addFragment(episodes, "Seasons");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
        returnIntent.putExtra("seriesId", dataId);
        setResult(Activity.RESULT_OK, returnIntent);

        super.finish();
        overridePendingTransition(0, R.anim.slide_down);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //enable onActivityResult on children fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
