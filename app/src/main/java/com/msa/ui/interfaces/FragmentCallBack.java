package com.msa.ui.interfaces;


import com.msa.ui.adapters.RssItem;
import com.msa.ui.preferences.PreferencesManager;

public interface FragmentCallBack {

    public RssItem getRssItem();
    public void setRssItem(RssItem rssItem);
    public void loadFragment(int fragmentIndex);
    public PreferencesManager getPrefs();
}
