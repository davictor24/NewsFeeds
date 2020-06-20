package com.electroninc.newsfeeds.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.electroninc.newsfeeds.R;
import com.electroninc.newsfeeds.network.NewsLoader;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    public static final String SEARCH_TEXT = "search_text";
    private static final int LOADER_ID = 1;

    private TextView searchResultTextView;
    private TextView noResultTextView;
    private TextView notConnectedTextView;
    private ProgressBar loadingProgressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        searchResultTextView = findViewById(R.id.search_result_text_view);
        noResultTextView = findViewById(R.id.no_result_text_view);
        notConnectedTextView = findViewById(R.id.not_connected_text_view);
        loadingProgressSpinner = findViewById(R.id.loading_progress_spinner);

        Intent displayNewsIntent = getIntent();
        LoaderManager.getInstance(this).initLoader(LOADER_ID, displayNewsIntent.getExtras(), this);
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
        searchResultTextView.setText(data);
        showSearchResults();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
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
}
