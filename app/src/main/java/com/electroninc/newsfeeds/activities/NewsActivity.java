package com.electroninc.newsfeeds.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.electroninc.newsfeeds.R;

public class NewsActivity extends AppCompatActivity {

    public static final String SEARCH_TEXT = "search_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        TextView searchResultTextView = findViewById(R.id.search_result_text_view);

        Intent displayNewsIntent = getIntent();
        String searchText = displayNewsIntent.getStringExtra(SEARCH_TEXT);
        searchResultTextView.setText(searchText);
    }

}
