package com.electroninc.newsfeeds.network;

import android.content.Context;

import com.electroninc.newsfeeds.models.News;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
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
    public List<News> loadInBackground() {
        if (url == null || url.isEmpty()) return null;
        return NetworkUtils.fetchNews(url);
    }
}
