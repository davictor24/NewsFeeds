package com.electroninc.newsfeeds.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.electroninc.newsfeeds.R;
import com.electroninc.newsfeeds.adapters.NewsAdapter;
import com.electroninc.newsfeeds.lifecycle.NewsActivityViewModel;
import com.electroninc.newsfeeds.models.News;
import com.electroninc.newsfeeds.network.NetworkReceiver;
import com.electroninc.newsfeeds.network.NetworkReceiverCallback;
import com.electroninc.newsfeeds.network.NewsLoader;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>,
        NetworkReceiverCallback, NewsAdapter.ItemClickListener {

    public static final String SEARCH_TEXT = "search_text";
    private static final int LOADER_ID = 1;

    private RecyclerView newsRecyclerView;
    private TextView noResultTextView;
    private TextView notConnectedTextView;
    private ProgressBar loadingProgressSpinner;

    private NewsActivityViewModel newsViewModel;
    private Bundle loaderArgs;
    private BroadcastReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsRecyclerView = findViewById(R.id.news_recycler_view);
        noResultTextView = findViewById(R.id.no_result_text_view);
        notConnectedTextView = findViewById(R.id.not_connected_text_view);
        loadingProgressSpinner = findViewById(R.id.loading_progress_spinner);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        newsRecyclerView.setLayoutManager(layoutManager);

        newsViewModel = new ViewModelProvider(this).get(NewsActivityViewModel.class);
        newsViewModel.news.observe(this, new Observer<List<News>>() {
            @Override
            public void onChanged(List<News> newsList) {
                if (newsList.size() == 0) showNoResult();
                else {
                    NewsAdapter newsAdapter = new NewsAdapter(NewsActivity.this,
                            newsList,
                            NewsActivity.this);
                    newsRecyclerView.setAdapter(newsAdapter);
                    showNewsRecyclerView();
                }
            }
        });

        // If data has not been loaded, allow the network receiver load data if/when connectivity is available
        if (!newsViewModel.hasLoaded) {
            showNotConnected(); // Default view
            Intent displayNewsIntent = getIntent();
            loaderArgs = displayNewsIntent.getExtras();
            networkReceiver = new NetworkReceiver(this);
            registerNetworkReceiver();
        } else {
            showNewsRecyclerView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver if still active
        unregisterNetworkReceiver();
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        String searchText = "";
        if (args != null) searchText = args.getString(SEARCH_TEXT);
        String query = (searchText == null || searchText.isEmpty()) ? "" : "&q=" + searchText;
        String url = "https://content.guardianapis.com/search?api-key=54a4d561-a782-447d-a8bb-e0370f42288b&page-size=25" + query;
        return new NewsLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        // Network receiver is no longer needed, so unregister it
        unregisterNetworkReceiver();
        newsViewModel.news.postValue((ArrayList<News>) data);
        newsViewModel.hasLoaded = true;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        // Remove references to data
        newsViewModel.news.postValue(new ArrayList<News>());
        newsViewModel.hasLoaded = false;
        // Network receiver is now needed
        registerNetworkReceiver();
    }

    @Override
    public void onConnected() {
        // Load data if connectivity is available
        startLoader(loaderArgs);
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onItemClicked(int itemId) {
        List<News> newsList = newsViewModel.news.getValue();
        if (newsList == null) return;
        String url = newsList.get(itemId).getUrl();
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }

    private void startLoader(Bundle args) {
        showLoadingProgress();
        LoaderManager.getInstance(this).initLoader(LOADER_ID, args, this);
    }

    private void showNewsRecyclerView() {
        newsRecyclerView.setVisibility(View.VISIBLE);
        hideViews(noResultTextView, notConnectedTextView, loadingProgressSpinner);
    }

    private void showNoResult() {
        noResultTextView.setVisibility(View.VISIBLE);
        hideViews(newsRecyclerView, notConnectedTextView, loadingProgressSpinner);
    }

    private void showNotConnected() {
        notConnectedTextView.setVisibility(View.VISIBLE);
        hideViews(newsRecyclerView, noResultTextView, loadingProgressSpinner);
    }

    private void showLoadingProgress() {
        loadingProgressSpinner.setVisibility(View.VISIBLE);
        hideViews(newsRecyclerView, noResultTextView, notConnectedTextView);
    }

    private void hideViews(View... views) {
        for (View view : views) view.setVisibility(View.GONE);
    }

    private void registerNetworkReceiver() {
        // Sticking to this, for the same reason given in NetworkUtils.isConnectedOrConnecting()
        //noinspection deprecation
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterNetworkReceiver() {
        try {
            unregisterReceiver(networkReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

}
