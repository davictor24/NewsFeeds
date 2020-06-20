package com.electroninc.newsfeeds.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

public class NewsLoader extends AsyncTaskLoader<String> {
    private String url;

    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url.trim();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        if (url == null || url.isEmpty()) return null;
        return NetworkUtils.fetchNews(url);
    }
}
