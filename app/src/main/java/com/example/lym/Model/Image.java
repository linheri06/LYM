package com.example.lym.Model;

public class Image {
    public String url;
    public String cap;
    public long date;

    public Image(){}

    public Image(String url, String cap, long date) {
        this.url = url;
        this.cap = cap;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
