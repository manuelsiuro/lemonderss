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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.msa.ui.adapters.RssItem;
import com.msa.ui.fragments.CardViewFragment;
import com.msa.ui.fragments.DetailFragment;
import com.msa.ui.fragments.SettingsFragment;
import com.msa.ui.fragments.WebViewFragment;
import com.msa.ui.preferences.PreferencesManager;

public class MainActivity extends AppCompatActivity {

    final int FRAGMENT_RSS_CARD_VIEW = 0;
    final int FRAGMENT_RSS_DETAIL    = 1;
    final int FRAGMENT_WEB_VIEW      = 2;
    final int FRAGMENT_SETTINGS      = 4;

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private ImageView backdrop;
    private Fragment currentFragment;
    private RssItem rssItem;
    private int currentFragmentIndex;

    private PreferencesManager prefs;
    private CoordinatorLayout snackBarCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar             = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout  = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        appBarLayout        = (AppBarLayout) findViewById(R.id.appbar);
        backdrop            = (ImageView) findViewById(R.id.backdrop);
        snackBarCoordinator = (CoordinatorLayout)findViewById(R.id.snackbarlocation);

        prefs = new PreferencesManager(this);

        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        initBackDrop();
        initSwipeRefreshLayout();
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

        if (id == R.id.settings && currentFragmentIndex != FRAGMENT_SETTINGS) {
            displayView(FRAGMENT_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                Snackbar snackbar = Snackbar.make(snackBarCoordinator, getString(R.string.no_internet_connexion), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    private void initSwipeRefreshLayout(){

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(currentFragment!=null){

                            switch (currentFragmentIndex) {
                                case FRAGMENT_RSS_CARD_VIEW:
                                    CardViewFragment fragment = (CardViewFragment) currentFragment;
                                    fragment.loadRSS();
                                    break;
                                default:
                                    stopLoaderRefreshLayout();
                                    break;
                            }
                        }
                    }
                }
        );
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

    public void stopLoaderRefreshLayout(){
        swipeRefreshLayout.setRefreshing(false);
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
        currentFragmentIndex = fragmentIndex;

        switch (fragmentIndex) {
            case FRAGMENT_RSS_CARD_VIEW:
                fragment = new CardViewFragment();
                appBarLayout.setExpanded(true);
                break;
            case FRAGMENT_RSS_DETAIL:
                fragment = new DetailFragment();
                appBarLayout.setExpanded(false);
                bBackStack = true;
                break;
            case FRAGMENT_WEB_VIEW:
                fragment = new WebViewFragment();
                appBarLayout.setExpanded(false);
                bBackStack = true;
                break;
            case FRAGMENT_SETTINGS:
                fragment = new SettingsFragment();
                appBarLayout.setExpanded(false);
                bBackStack = true;
                break;
            default:
                break;
        }

        if (fragment != null) {
            currentFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
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
