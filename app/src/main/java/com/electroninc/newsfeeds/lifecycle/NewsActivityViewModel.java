package com.electroninc.newsfeeds.lifecycle;

import com.electroninc.newsfeeds.models.News;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewsActivityViewModel extends ViewModel {
    public boolean hasLoaded = false;
    public MutableLiveData<ArrayList<News>> news = new MutableLiveData<>(new ArrayList<News>());
}
