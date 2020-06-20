package com.electroninc.newsfeeds.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.electroninc.newsfeeds.R;
import com.electroninc.newsfeeds.lifecycle.NewsActivityViewModel;
import com.electroninc.newsfeeds.network.NetworkReceiver;
import com.electroninc.newsfeeds.network.NetworkReceiverCallback;
import com.electroninc.newsfeeds.network.NewsLoader;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, NetworkReceiverCallback {

    public static final String SEARCH_TEXT = "search_text";
    private static final int LOADER_ID = 1;

    private TextView searchResultTextView;
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
        searchResultTextView = findViewById(R.id.search_result_text_view);
        noResultTextView = findViewById(R.id.no_result_text_view);
        notConnectedTextView = findViewById(R.id.not_connected_text_view);
        loadingProgressSpinner = findViewById(R.id.loading_progress_spinner);

        newsViewModel = new ViewModelProvider(this).get(NewsActivityViewModel.class);
        newsViewModel.news.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                searchResultTextView.setText(s);
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
            showSearchResults();
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
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String searchText = "";
        if (args != null) searchText = args.getString(SEARCH_TEXT);
        String query = (searchText == null || searchText.isEmpty()) ? "" : "&q=" + searchText;
        String url = "https://content.guardianapis.com/search?api-key=54a4d561-a782-447d-a8bb-e0370f42288b" + query;
        return new NewsLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        // Network receiver is no longer needed, so unregister it
        unregisterNetworkReceiver();
        newsViewModel.news.postValue(data);
        newsViewModel.hasLoaded = true;
        showSearchResults();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        // Remove references to data
        newsViewModel.news.postValue("");
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

    private void startLoader(Bundle args) {
        showLoadingProgress();
        LoaderManager.getInstance(this).initLoader(LOADER_ID, args, this);
    }

    private void showSearchResults() {
        searchResultTextView.setVisibility(View.VISIBLE);
        hideViews(noResultTextView, notConnectedTextView, loadingProgressSpinner);
    }

    private void showNoResult() {
        noResultTextView.setVisibility(View.VISIBLE);
        hideViews(searchResultTextView, notConnectedTextView, loadingProgressSpinner);
    }

    private void showNotConnected() {
        notConnectedTextView.setVisibility(View.VISIBLE);
        hideViews(searchResultTextView, noResultTextView, loadingProgressSpinner);
    }

    private void showLoadingProgress() {
        loadingProgressSpinner.setVisibility(View.VISIBLE);
        hideViews(searchResultTextView, noResultTextView, notConnectedTextView);
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
