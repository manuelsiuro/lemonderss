package com.msa.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.msa.ui.adapters.RssItem;
import com.msa.ui.fragments.CardViewFragment;
import com.msa.ui.fragments.DetailFragment;
import com.msa.ui.fragments.SettingsFragment;
import com.msa.ui.fragments.WebViewFragment;
import com.msa.ui.preferences.PreferencesManager;

public class MainActivityDrawer extends AppCompatActivity {

    //private final int FRAGMENT_RSS_CARD_VIEW = 0;
    //private final int FRAGMENT_RSS_DETAIL    = 1;
    //private final int FRAGMENT_WEB_VIEW      = 2;
    //private final int FRAGMENT_SETTINGS      = 4;

    private final String FRAGMENT_TAG_CARD = "card";
    private final String FRAGMENT_TAG_DETAIL = "detail";
    private final String FRAGMENT_TAG_WEB = "web";
    private final String FRAGMENT_TAG_SETTINGS = "settings";

    private RssItem rssItem;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private PreferencesManager prefs;



    private String rssURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if(menuItem.isChecked()){
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()){

                    case R.id.le_monde:
                        Toast.makeText(getApplicationContext(),"le_monde Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_LE_MONDE);
                        return true;
                    case R.id.nice_matin:
                        Toast.makeText(getApplicationContext(),"nice_matin Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_NICE_MATIN);
                        return true;
                    case R.id.le_parisien:
                        Toast.makeText(getApplicationContext(),"le_parisien Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_LE_PARISIEN);
                        return true;
                    case R.id.les_echos:
                        Toast.makeText(getApplicationContext(),"les_echos Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_LES_ECHOS);
                        return true;
                    case R.id.lobs:
                        Toast.makeText(getApplicationContext(),"lobs Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_LOBS);
                        return true;
                    case R.id.science_et_avenir:
                        Toast.makeText(getApplicationContext(),"science_et_avenir Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_SCIENCE_AVENIR);
                        return true;
                    case R.id.fr_android:
                        Toast.makeText(getApplicationContext(),"fr_android Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_ANDROID_MT);
                        return true;
                    case R.id.android_mt:
                        Toast.makeText(getApplicationContext(),"android_mt Selected", Toast.LENGTH_SHORT).show();
                        setRssURL(Constants.URL.RSS_FR_ANDROID);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Hum ! Selected", Toast.LENGTH_SHORT).show();
                        setRssURL("");
                        return true;

                }
            }
        });



        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer,  R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        actionBarDrawerToggle.syncState();

        prefs               = new PreferencesManager(this);

        initFragmentManager();

        setRssURL(Constants.URL.RSS_LE_MONDE);
        loadFragment(Constants.FRAGMENT.FRAGMENT_RSS_CARD_VIEW);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
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
                loadFragment(Constants.FRAGMENT.FRAGMENT_SETTINGS);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFragmentManager(){
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                actionBarDrawerToggle.setDrawerIndicatorEnabled(getSupportFragmentManager().getBackStackEntryCount() == 0);
                if(getSupportActionBar()!=null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
                }
                actionBarDrawerToggle.syncState();
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawerLayout.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });
    }

    public void loadFragment(int fragmentIndex) {

        Fragment fragment = null;
        Boolean bBackStack = false;
        String tagFragment = FRAGMENT_TAG_CARD;

        switch (fragmentIndex) {
            case Constants.FRAGMENT.FRAGMENT_RSS_CARD_VIEW:
                fragment = new CardViewFragment();
                break;
            case Constants.FRAGMENT.FRAGMENT_RSS_DETAIL:
                fragment = new DetailFragment();
                tagFragment = FRAGMENT_TAG_DETAIL;
                bBackStack = true;
                break;
            case Constants.FRAGMENT.FRAGMENT_WEB_VIEW:
                fragment = new WebViewFragment();
                tagFragment = FRAGMENT_TAG_WEB;
                bBackStack = true;
                break;
            case Constants.FRAGMENT.FRAGMENT_SETTINGS:
                fragment = new SettingsFragment();
                tagFragment = FRAGMENT_TAG_SETTINGS;
                bBackStack = true;
                break;
            default:
                break;
        }

        if (fragment != null) {

            Bundle bundle = new Bundle();
            bundle.putString("rssURL", getRssURL() );
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit);

            fragmentTransaction.replace(R.id.content_main_frame, fragment, tagFragment);
            if(bBackStack){
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
        }
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

    public String getRssURL() {
        return rssURL;
    }

    public void setRssURL(String rssURL) {
        this.rssURL = rssURL;
        loadFragment(Constants.FRAGMENT.FRAGMENT_RSS_CARD_VIEW);
    }
}
