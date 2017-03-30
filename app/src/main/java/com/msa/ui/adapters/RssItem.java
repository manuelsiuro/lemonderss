package com.msa.ui.adapters;

import java.io.Serializable;

public class RssItem implements Serializable {

    private String title;
    private String enclosure;
    private String strDate;
    private String strTime;
    private String description;
    private String link;

    public RssItem(String title, String enclosure, String strDate, String strTime, String description, String link) {
        this.title = title;
        this.enclosure = enclosure;
        this.strDate = strDate;
        this.strTime = strTime;
        this.description = description;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public String getStrDate() {
        return strDate;
    }

    public String getStrTime() {
        return strTime;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
