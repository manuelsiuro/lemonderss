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

    public interface URL {
        String RSS_LE_MONDE         = "http://www.lemonde.fr/rss/une.xml";
        String RSS_NICE_MATIN       = "http://www.nicematin.com/ville/cote-d-azur/rss";
        String RSS_LE_PARISIEN      = "http://www.leparisien.fr/une/rss.xml";
        String RSS_LES_ECHOS        = "https://www.lesechos.fr/rss/rss_articles_journal.xml";
        String RSS_LOBS             = "http://tempsreel.nouvelobs.com/rss.xml";
        String RSS_SCIENCE_AVENIR   = "https://www.sciencesetavenir.fr/rss.xml";
        String RSS_FR_ANDROID       = "";
        String RSS_ANDROID_MT       = "http://feeds.feedburner.com/AndroidMtNews?format=xml";
    }

}
