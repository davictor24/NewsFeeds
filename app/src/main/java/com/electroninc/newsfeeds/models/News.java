package com.electroninc.newsfeeds.models;

public class News {
    private String title;
    private String url;
    private String section;
    private String date;

    public News(String title, String url, String section, String date) {
        this.title = title;
        this.url = url;
        this.section = section;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSection() {
        return section;
    }

    public String getDate() {
        return date;
    }
}
