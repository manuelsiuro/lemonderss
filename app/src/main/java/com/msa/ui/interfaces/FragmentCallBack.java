package com.msa.ui.interfaces;

import com.msa.ui.adapters.RssItem;
import com.msa.ui.preferences.PreferencesManager;

public interface FragmentCallBack {

    RssItem getRssItem();
    void setRssItem(RssItem rssItem);
    void loadFragment(int fragmentIndex);
    PreferencesManager getPrefs();
}
