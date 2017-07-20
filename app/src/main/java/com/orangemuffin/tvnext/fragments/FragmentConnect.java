package com.orangemuffin.tvnext.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.activities.WebViewActivity;
import com.orangemuffin.tvnext.trakt.TraktServiceBase;
import com.uwetrottmann.trakt5.TraktV2;

import java.math.BigInteger;
import java.security.SecureRandom;

/* Created by OrangeMuffin on 6/11/2017 */
public class FragmentConnect extends Fragment {

    private Button connect;

    private static final String CLIENT_ID = "f91a443e63288bad011935067c07e25e12d9bea5a67468971d4b08f35e3ad847";
    private static final String CLIENTSECRET = "3a6013d4ae5c053365fff2cea56528115881b9771af8370912d869c121879afb";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final String TRAKT_BASE_URL = "https://api-v2launch.trakt.tv/";
    private static final TraktV2 traktV2 = new TraktV2(CLIENT_ID, CLIENTSECRET, REDIRECT_URI);

    // Required empty public constructor
    public FragmentConnect() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        connect = (Button) rootView.findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new LoadDataTask().execute();
                Toast.makeText(getContext(), "Under Development!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public class LoadDataTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String state = new BigInteger(130, new SecureRandom()).toString(32);
                return traktV2.buildAuthorizationUrl(state);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Intent webpage = new Intent(getContext(), WebViewActivity.class);
            webpage.putExtra("AUTH_URL", s);
            getActivity().startActivity(webpage);
            getActivity().overridePendingTransition(R.anim.slide_top, R.anim.anim_stay);
        }
    }
}
