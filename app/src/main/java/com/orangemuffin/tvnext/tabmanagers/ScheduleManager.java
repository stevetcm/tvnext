package com.orangemuffin.tvnext.tabmanagers;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.fragments.FragmentRecent;
import com.orangemuffin.tvnext.fragments.FragmentToday;
import com.orangemuffin.tvnext.fragments.FragmentUpcoming;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 3/11/2017 */
public class ScheduleManager extends Fragment {
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static ViewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_manager_layout, null);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new FragmentRecent(), "Recent");
        viewPagerAdapter.addFragment(new FragmentToday(), "Today");
        viewPagerAdapter.addFragment(new FragmentUpcoming(), "Upcoming");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1, false);
        viewPager.setOffscreenPageLimit(3);
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
}
