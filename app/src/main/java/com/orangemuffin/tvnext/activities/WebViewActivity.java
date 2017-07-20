package com.orangemuffin.tvnext.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orangemuffin.tvnext.R;

/* Created by OrangeMuffin on 6/11/2017 */
public class WebViewActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private WebView webpage;

    private LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        webpage = (WebView) findViewById(R.id.webview_page);
        webpage.setBackgroundColor(Color.parseColor("#393939"));

        //remove session information
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(getApplicationContext());
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }


        linlaHeaderProgress.setVisibility(View.VISIBLE);
        webpage.setVisibility(View.GONE);

        String auth_url = getIntent().getStringExtra("AUTH_URL");

        webpage.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                linlaHeaderProgress.setVisibility(View.GONE);
                webpage.setVisibility(View.VISIBLE);

                if (url.toString().length() < 45) {
                    //new ProcessAccessToken().execute(url.toString());
                }
            }
        });

        webpage.loadUrl(auth_url);
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
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom);
    }
}
