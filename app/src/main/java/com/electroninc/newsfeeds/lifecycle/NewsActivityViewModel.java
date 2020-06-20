package com.electroninc.newsfeeds.lifecycle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewsActivityViewModel extends ViewModel {
    public boolean hasLoaded = false;
    public MutableLiveData<String> news = new MutableLiveData<>("");
}
