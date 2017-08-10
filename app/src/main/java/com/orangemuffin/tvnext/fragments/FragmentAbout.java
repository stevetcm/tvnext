package com.orangemuffin.tvnext.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangemuffin.tvnext.R;

/* Created by OrangeMuffin on 8/10/2017 */
public class FragmentAbout extends Fragment {

    //Required empty public constructor
    public FragmentAbout() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        return rootView;
    }
}
