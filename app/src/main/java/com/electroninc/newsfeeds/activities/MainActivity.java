package com.electroninc.newsfeeds.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.electroninc.newsfeeds.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText searchEditText = findViewById(R.id.news_search_edit_text);
        Button searchBtn = findViewById(R.id.news_search_btn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchEditText.getText().toString().trim();
                Intent displayNewsIntent = new Intent(MainActivity.this, NewsActivity.class);
                Bundle args = new Bundle();
                args.putString(NewsActivity.SEARCH_TEXT, searchText);
                displayNewsIntent.putExtras(args);
                startActivity(displayNewsIntent);
            }
        });
    }

}
