package com.electroninc.newsfeeds.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.electroninc.newsfeeds.R;
import com.electroninc.newsfeeds.models.News;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    private List<News> newsList;
    private Context context;
    private ItemClickListener itemClickListener;

    public NewsAdapter(Context context, List<News> newsList, ItemClickListener itemClickListener) {
        this.context = context;
        this.newsList = newsList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        String title = news.getTitle();
        String section = news.getSection();
        String dateString = news.getDate();

        holder.newsTitleTextView.setText(title);
        holder.newsSectionTextView.setText(section.isEmpty() ? "" : "Posted in: " + section);
        try {
            Date date = dateFormat.parse(dateString);
            if (date == null) throw new ParseException("Null date", -1);
            CharSequence formattedDate = DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), 0L);
            holder.newsDateTextView.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.newsDateTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    public interface ItemClickListener {
        void onItemClicked(int itemId);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView newsTitleTextView;
        TextView newsSectionTextView;
        TextView newsDateTextView;

        NewsViewHolder(View itemView) {
            super(itemView);
            newsTitleTextView = itemView.findViewById(R.id.news_title_text_view);
            newsSectionTextView = itemView.findViewById(R.id.news_section_text_view);
            newsDateTextView = itemView.findViewById(R.id.news_date_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(getAdapterPosition());
        }
    }
}
