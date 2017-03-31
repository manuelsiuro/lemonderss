package com.msa.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.msa.ui.adapters.RssItem;
import com.msa.ui.fragments.CardViewFragment;
import com.msa.ui.fragments.DetailFragment;
import com.msa.ui.fragments.SettingsFragment;
import com.msa.ui.fragments.WebViewFragment;
import com.msa.ui.interfaces.FragmentCallBack;
import com.msa.ui.parser.JsonSourcesParser;
import com.msa.ui.preferences.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityDrawer extends AppCompatActivity implements FragmentCallBack {

    private String TAG = "MainActivityDrawer";

    private RssItem rssItem;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CollapsingToolbarLayout collapsingToolbar;
    private PreferencesManager prefs;
    private String rssURL;
    private ArrayList<HashMap<String, String>> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        drawerLayout      = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar           = (Toolbar) findViewById(R.id.toolbar);
        navigationView    = (NavigationView) findViewById(R.id.nav_view);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        setSupportActionBar(toolbar);

        initNavigationView();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer,  R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
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

        /*
        if(savedInstanceState == null){
            setRssURL(Constants.URL.RSS_LE_MONDE, getString(R.string.nav_menu_lemonde));
            navigationView.getMenu().getItem(0).setChecked(true);
        }*/

        JsonSourcesParser jsonSourcesParser = new JsonSourcesParser(this);
        jsonSourcesParser.parseJson();

        itemList = jsonSourcesParser.getItemList();

        Menu menu = navigationView.getMenu();
        menu.clear();

        for (HashMap<String, String> feed : itemList) {

            int menuItemId = Integer.parseInt( feed.get("id") );

            menu.add(0, menuItemId, 0, feed.get("label"));
            menu.findItem( menuItemId ).setIcon( R.drawable.ic_rss );
            menu.findItem( menuItemId ).setChecked(false);


            if(menuItemId == 0){
                if(savedInstanceState == null){
                    setRssURL(feed.get("url"), feed.get("label"));
                    navigationView.getMenu().getItem(menuItemId).setChecked(true);
                }
            }

            /*
            System.out.println(" ");
            System.out.println(feed.get("label"));
            System.out.println(feed.get("url"));
            System.out.println(feed.get("category"));
            */
        }

        //showQuit();

    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        if (outState != null) {
            outState.putSerializable("rssItem", rssItem);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
        if ((savedInstanceState != null) && (savedInstanceState.getSerializable("rssItem") != null)) {
            rssItem = (RssItem) savedInstanceState.getSerializable("rssItem");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(TAG, "onPostCreate");
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged");
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void uncheckAllItem(){
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    private void initNavigationView(){

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                /*
                if(menuItem.isChecked()){
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                */
                uncheckAllItem();

                drawerLayout.closeDrawers();

                System.out.println("===> getItemId " + menuItem.getItemId());

                for (HashMap<String, String> feed : itemList) {

                    int menuItemId = Integer.parseInt( feed.get("id") );

                    if(menuItemId == menuItem.getItemId()){
                        menuItem.setChecked(true);
                        setRssURL(feed.get("url"), feed.get("label"));
                        return true;
                    }
                }


                /*
                switch (menuItem.getItemId()){

                    case R.id.le_monde:
                        setRssURL(Constants.URL.RSS_LE_MONDE, menuItem.getTitle().toString());
                        return true;
                    case R.id.nice_matin:
                        setRssURL(Constants.URL.RSS_NICE_MATIN, menuItem.getTitle().toString());
                        return true;
                    case R.id.le_parisien:
                        setRssURL(Constants.URL.RSS_LE_PARISIEN, menuItem.getTitle().toString());
                        return true;
                    case R.id.les_echos:
                        setRssURL(Constants.URL.RSS_LES_ECHOS, menuItem.getTitle().toString());
                        return true;
                    case R.id.lobs:
                        setRssURL(Constants.URL.RSS_LOBS, menuItem.getTitle().toString());
                        return true;
                    case R.id.science_et_avenir:
                        setRssURL(Constants.URL.RSS_SCIENCE_AVENIR, menuItem.getTitle().toString());
                        return true;
                    case R.id.fr_android:
                        setRssURL(Constants.URL.RSS_ANDROID_MT, menuItem.getTitle().toString());
                        return true;
                    case R.id.android_mt:
                        setRssURL(Constants.URL.RSS_FR_ANDROID, menuItem.getTitle().toString());
                        return true;
                    case R.id.settings:
                        SettingsFragment settingsFragment = (SettingsFragment)getSupportFragmentManager().findFragmentByTag(Constants.TAG.SETTINGS);
                        if (settingsFragment != null && settingsFragment.isVisible()) {
                            return true;
                        } else {
                            loadFragment(Constants.FRAGMENT.SETTINGS);
                            return true;
                        }
                    default:
                        return true;
                }*/

                return true;
            }
        });
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

    private void showQuit(){
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.quit)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    public String getRssURL() {
        return rssURL;
    }

    public void setRssURL(String rssURL){
        this.rssURL = rssURL;
    }

    public void setRssURL(String rssURL, String title) {
        setRssURL(rssURL);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        loadFragment(Constants.FRAGMENT.RSS_CARD);
        collapsingToolbar.setTitle(title);
    }

    /** interfaces */
    @Override
    public RssItem getRssItem() {
        return rssItem;
    }

    @Override
    public void setRssItem(RssItem rssItem) {
        this.rssItem = rssItem;
    }

    @Override
    public void loadFragment(int fragmentIndex) {

        Fragment fragment = null;
        Boolean bBackStack = false;
        String tagFragment = Constants.TAG.CARD;

        switch (fragmentIndex) {
            case Constants.FRAGMENT.RSS_CARD:
                fragment = new CardViewFragment();
                break;
            case Constants.FRAGMENT.RSS_DETAIL:
                fragment = new DetailFragment();
                tagFragment = Constants.TAG.DETAIL;
                bBackStack = true;
                break;
            case Constants.FRAGMENT.WEB_VIEW:
                fragment = new WebViewFragment();
                tagFragment = Constants.TAG.WEB;
                bBackStack = true;
                break;
            case Constants.FRAGMENT.SETTINGS:
                fragment = new SettingsFragment();
                tagFragment = Constants.TAG.SETTINGS;
                bBackStack = true;
                break;
            default:
                break;
        }

        if (fragment != null) {

            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.RSS_URL, getRssURL() );
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

    @Override
    public PreferencesManager getPrefs() {
        return prefs;
    }
}
