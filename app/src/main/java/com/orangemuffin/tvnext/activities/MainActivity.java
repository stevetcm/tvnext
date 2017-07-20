package com.orangemuffin.tvnext.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.services.AlarmScheduler;
import com.orangemuffin.tvnext.tabmanagers.DiscoverManager;
import com.orangemuffin.tvnext.tabmanagers.LibraryManager;
import com.orangemuffin.tvnext.tabmanagers.ScheduleManager;
import com.orangemuffin.tvnext.tabmanagers.TraktManager;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editText;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private int clickedItem = 0;
    private int currentItem = 0;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Library");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.title_search);
        editText.setVisibility(View.GONE);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.containerView, new LibraryManager()).commit();
        currentItem = R.id.library;

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                clickedItem = menuItem.getItemId();
                return false;
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (clickedItem == R.id.settings) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivityForResult(intent, 1002);
                    overridePendingTransition(R.anim.slide_left, R.anim.anim_stay);
                } else {
                    switchTabManager(clickedItem);
                }
                clickedItem = 0;
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //basic setup for universal image loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache()).build();
        ImageLoader.getInstance().init(config);

        SharedPreferences sp_data = this.getSharedPreferences("PHONEDATA", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_data.edit();

        //get screen resolution
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        editor.putInt("PHONE_RES", width);
        editor.apply();

        boolean notif_state = sp_data.getBoolean("NOTIF_STATE", true);

        if (notif_state) {
            //initiate notification service through alarm scheduler if it's not running
            if (!isMyServiceRunning()) {
                Intent serviceIntent = new Intent(this, AlarmScheduler.class);
                startService(serviceIntent);
            }
        }
    }

    //check if AlarmScheduler is running
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AlarmScheduler.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void makeSearchBarHidden() {
        editText.setVisibility(View.GONE);
    }

    public void makeSearchBarVisible() {
        editText.setText("");
        editText.setVisibility(View.VISIBLE);
    }

    public void switchTabManager(int clicked) {
        if (clicked == R.id.library && currentItem != R.id.library) {
            editText.setVisibility(View.GONE);
            toolbar.setTitle("Library");
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            mFragmentTransaction.add(R.id.containerView, new LibraryManager()).commit();
            currentItem = R.id.library;
            setNavBarItem(0);
        } else if (clicked == R.id.schedule && currentItem != R.id.schedule) {
            editText.setVisibility(View.GONE);
            toolbar.setTitle("Schedule");
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            mFragmentTransaction.add(R.id.containerView, new ScheduleManager()).commit();
            currentItem = R.id.schedule;
            setNavBarItem(1);
        } else if (clicked == R.id.discover && currentItem != R.id.discover) {
            editText.setVisibility(View.GONE);
            toolbar.setTitle("Discover");
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            mFragmentTransaction.add(R.id.containerView, new DiscoverManager()).commit();
            currentItem = R.id.discover;
            setNavBarItem(2);
        } else if (clicked == R.id.trakt && currentItem != R.id.trakt) {
            editText.setVisibility(View.GONE);
            toolbar.setTitle("Trakt.tv");
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            mFragmentTransaction.add(R.id.containerView, new TraktManager()).commit();
            currentItem = R.id.trakt;
            setNavBarItem(3);
        }
    }

    public void reloadTraktManager() {
        if (currentItem == R.id.trakt) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            mFragmentTransaction.add(R.id.containerView, new TraktManager()).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1002) { //code 1002 is SettingsActivity
            if (resultCode == Activity.RESULT_CANCELED) {
                if (currentItem == R.id.library) {
                    setNavBarItem(0);
                } else if (currentItem == R.id.schedule) {
                    setNavBarItem(1);
                } else if (currentItem == R.id.discover) {
                    setNavBarItem(2);
                } else if (currentItem == R.id.trakt) {
                    setNavBarItem(3);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START) && !doubleBackToExitPressedOnce) {
            drawerLayout.closeDrawers();
        } else if (!doubleBackToExitPressedOnce) {
            drawerLayout.openDrawer(Gravity.LEFT);
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

            //timer to reset double back pressed
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    public void setNavBarItem(int position) {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, 1002);
            overridePendingTransition(R.anim.slide_left, R.anim.anim_stay);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
