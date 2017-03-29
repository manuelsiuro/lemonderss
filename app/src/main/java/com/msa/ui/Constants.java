package com.msa.ui;



public class Constants {

    public interface FRAGMENT {
        int FRAGMENT_RSS_CARD_VIEW = 0;
        int FRAGMENT_RSS_DETAIL    = 1;
        int FRAGMENT_WEB_VIEW      = 2;
        int FRAGMENT_SETTINGS      = 4;
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
