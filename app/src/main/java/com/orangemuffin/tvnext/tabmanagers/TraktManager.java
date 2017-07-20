package com.orangemuffin.tvnext.tabmanagers;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.fragments.FragmentConnect;
import com.orangemuffin.tvnext.fragments.FragmentTvSeries;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 6/11/2017 */
public class TraktManager extends Fragment {
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

        View tabView = getActivity().getLayoutInflater().inflate(R.layout.tab_element_layout, null);
        TextView tabText = (TextView) tabView.findViewById(R.id.tabText);
        tabText.setText("Connect");

        ImageView tabIcon = (ImageView) tabView.findViewById(R.id.tabIcon);
        tabIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trakticon3));

        tabLayout.getTabAt(0).setCustomView(tabView);

        //endless effort to remove indicator when using a single tab page
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new FragmentConnect(), "Connect");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
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
