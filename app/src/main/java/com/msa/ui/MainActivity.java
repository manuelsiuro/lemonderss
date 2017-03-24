package com.msa.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.msa.ui.adapters.RssItem;
import com.msa.ui.fragments.CardViewFragment;
import com.msa.ui.fragments.DetailFragment;
import com.msa.ui.fragments.SettingsFragment;
import com.msa.ui.fragments.WebViewFragment;
import com.msa.ui.preferences.PreferencesManager;

public class MainActivity extends AppCompatActivity {

    private final int FRAGMENT_RSS_CARD_VIEW = 0;
    private final int FRAGMENT_RSS_DETAIL    = 1;
    private final int FRAGMENT_WEB_VIEW      = 2;
    private final int FRAGMENT_SETTINGS      = 4;

    private final String FRAGMENT_TAG_CARD = "card";
    private final String FRAGMENT_TAG_DETAIL = "detail";
    private final String FRAGMENT_TAG_WEB = "web";
    private final String FRAGMENT_TAG_SETTINGS = "settings";

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private ImageView backdrop;
    private RssItem rssItem;
    private PreferencesManager prefs;
    private CoordinatorLayout snackBarCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar             = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout        = (AppBarLayout) findViewById(R.id.appbar);
        backdrop            = (ImageView) findViewById(R.id.backdrop);
        snackBarCoordinator = (CoordinatorLayout)findViewById(R.id.snackbarlocation);

        prefs               = new PreferencesManager(this);

        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        initBackDrop();
        initFragmentManager();
        displayViewDependWifiStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        SettingsFragment settingsFragment = (SettingsFragment)getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SETTINGS);

        if (id == R.id.settings) {
            if (settingsFragment != null && settingsFragment.isVisible()) {
                return true;
            } else {
                displayView(FRAGMENT_SETTINGS);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSnackMessage(String message){
        Snackbar snackbar = Snackbar.make(snackBarCoordinator, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void displayViewDependWifiStatus(){

        if(prefs.getSettingsWifi()){
            if(isOnline()){
                displayView(FRAGMENT_RSS_CARD_VIEW);
            }
        } else {
            if(isOnline()){
                displayView(FRAGMENT_RSS_CARD_VIEW);
            } else {
                displayView(FRAGMENT_SETTINGS);
                showSnackMessage(getString(R.string.no_internet_connexion));
            }
        }
    }

    private void initBackDrop(){
        try {
            assert backdrop != null;
            Glide.with(this).load(R.drawable.logo).into(backdrop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCollapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        assert collapsingToolbar != null;
        collapsingToolbar.setTitle(" ");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initFragmentManager(){
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    //displayViewDependWifiStatus();
                }
            }
        });
    }

    public void displayView(int fragmentIndex) {

        Fragment fragment = null;
        Boolean bBackStack = false;
        String tagFragment = FRAGMENT_TAG_CARD;

        switch (fragmentIndex) {
            case FRAGMENT_RSS_CARD_VIEW:
                fragment = new CardViewFragment();
                appBarLayout.setExpanded(true);
                break;
            case FRAGMENT_RSS_DETAIL:
                fragment = new DetailFragment();
                appBarLayout.setExpanded(false);
                tagFragment = FRAGMENT_TAG_DETAIL;
                bBackStack = true;
                break;
            case FRAGMENT_WEB_VIEW:
                fragment = new WebViewFragment();
                appBarLayout.setExpanded(false);
                tagFragment = FRAGMENT_TAG_WEB;
                bBackStack = true;
                break;
            case FRAGMENT_SETTINGS:
                fragment = new SettingsFragment();
                appBarLayout.setExpanded(false);
                tagFragment = FRAGMENT_TAG_SETTINGS;
                bBackStack = true;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment, tagFragment);
            if(bBackStack){
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        Boolean b = false;
        if (netInfo != null && netInfo.isConnected()) {
            b = true;
        }
        return b;
    }

    public RssItem getRssItem() {
        return rssItem;
    }

    public void setRssItem(RssItem rssItem) {
        this.rssItem = rssItem;
    }

    public PreferencesManager getPrefs() {
        return prefs;
    }
}
