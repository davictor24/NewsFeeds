package com.electroninc.newsfeeds.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.electroninc.newsfeeds.network.NetworkReceiver;
import com.electroninc.newsfeeds.network.NetworkReceiverCallback;
import com.electroninc.newsfeeds.network.NewsLoader;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, NetworkReceiverCallback {

    public static final String SEARCH_TEXT = "search_text";
    private static final int LOADER_ID = 1;
    private static boolean loadFinished = false;

    private TextView searchResultTextView;
    private TextView noResultTextView;
    private TextView notConnectedTextView;
    private ProgressBar loadingProgressSpinner;

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

        Intent displayNewsIntent = getIntent();
        loaderArgs = displayNewsIntent.getExtras();
        // If data had been loaded, retrieve the information again from the loader
        if (loadFinished) {
            startLoader(loaderArgs);
        }
        // Else, let the network receiver load the data if connectivity is available
        else {
            showNotConnected(); // Default view
            networkReceiver = new NetworkReceiver(this);
            registerNetworkReceiver();
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
        // Set loadFinished to true so onCreate can know load status
        loadFinished = true;
        // Network receiver is no longer needed, so unregister it
        unregisterNetworkReceiver();
        searchResultTextView.setText(data);
        showSearchResults();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        // Loader has been reset, so set loadFinished to false
        loadFinished = false;
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
