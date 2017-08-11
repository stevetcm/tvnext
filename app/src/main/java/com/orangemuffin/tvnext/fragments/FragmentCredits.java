package com.orangemuffin.tvnext.fragments;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;

/* Created by OrangeMuffin on 8/11/2017 */
public class FragmentCredits extends Fragment {

    //Required empty public constructor
    public FragmentCredits() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credits, container, false);

        TextView credits_title = (TextView) rootView.findViewById(R.id.credits_title);
        credits_title.setPaintFlags(credits_title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        TextView thetvdb_terms = (TextView) rootView.findViewById(R.id.thetvdb_terms);
        thetvdb_terms.setMovementMethod(LinkMovementMethod.getInstance());
        String thetvdb_terms_text = "<a href='https://thetvdb.com/?tab=tos'>" + thetvdb_terms.getText() + "</a>";
        thetvdb_terms.setText(Html.fromHtml(thetvdb_terms_text));

        TextView ccbync40 = (TextView) rootView.findViewById(R.id.ccbync40);
        ccbync40.setMovementMethod(LinkMovementMethod.getInstance());
        String ccbync40_text = "<a href='http://creativecommons.org/licenses/by-nc/4.0'>" + ccbync40.getText() + "</a>";
        ccbync40.setText(Html.fromHtml(ccbync40_text));

        TextView tmdb_terms = (TextView) rootView.findViewById(R.id.tmdb_terms);
        tmdb_terms.setMovementMethod(LinkMovementMethod.getInstance());
        String tmdb_terms_text = "<a href='https://www.themoviedb.org/terms-of-use'>" + tmdb_terms.getText() + "</a>";
        tmdb_terms.setText(Html.fromHtml(tmdb_terms_text));

        TextView tmdb_api_terms = (TextView) rootView.findViewById(R.id.tmdb_api_terms);
        tmdb_api_terms.setMovementMethod(LinkMovementMethod.getInstance());
        String tmdb_api_terms_text = "<a href='https://www.themoviedb.org/documentation/api/terms-of-use'>" + tmdb_api_terms.getText() + "</a>";
        tmdb_api_terms.setText(Html.fromHtml(tmdb_api_terms_text));

        TextView trakt_terms = (TextView) rootView.findViewById(R.id.trakt_terms);
        trakt_terms.setMovementMethod(LinkMovementMethod.getInstance());
        String trakt_terms_text = "<a href='https://trakt.tv/terms'>" + trakt_terms.getText() + "</a>";
        trakt_terms.setText(Html.fromHtml(trakt_terms_text));

        return rootView;
    }
}
