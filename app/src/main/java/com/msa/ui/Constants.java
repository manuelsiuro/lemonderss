package com.msa.ui;

public class Constants {

    public interface FRAGMENT {
        int RSS_CARD    = 0;
        int RSS_DETAIL  = 1;
        int WEB_VIEW    = 2;
        int SETTINGS    = 4;
    }

    public interface TAG {
        String CARD     = "card";
        String DETAIL   = "detail";
        String WEB      = "web";
        String SETTINGS = "settings";
    }

    public interface BUNDLE {
        String RSS_URL = "rssURL";
    }
}
