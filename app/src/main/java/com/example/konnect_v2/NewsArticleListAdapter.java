package com.example.konnect_v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;

public class NewsArticleListAdapter extends RecyclerView.Adapter<NewsArticleListAdapter.ViewHolder> implements TimeAgoFormatter {
    Context context;
    ArrayList<NewsArticleListModel> newsArticlesArrayList;
    private NewsArticleListAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(NewsArticleListAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public NewsArticleListAdapter(Context context, ArrayList<NewsArticleListModel> newsArticlesArrayList) {
        this.context = context;
        this.newsArticlesArrayList = newsArticlesArrayList;
    }

    @NonNull
    @Override
    public NewsArticleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_article, parent, false);
        return new NewsArticleListAdapter.ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsArticleListAdapter.ViewHolder holder, int position) {
        NewsArticleListModel currentNewsArticle = newsArticlesArrayList.get(position);


        if (currentNewsArticle.getUriImageLink() != null) {
            Picasso.get().load(currentNewsArticle.getUriImageLink()).into(holder.articleImage);
        } else {
            holder.articleImage.setVisibility(View.GONE);
        }

        holder.articleTitle.setText(newsArticlesArrayList.get(position).getTitle());
        holder.articleContent.setText(newsArticlesArrayList.get(position).getContent());
        try {
            holder.articleDate.setText(TimeAgoFormatter.timeAgo(TimeAgoFormatter.parseISODate(newsArticlesArrayList.get(position).getPublishOn())));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return newsArticlesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        TextView articleTitle, articleContent, articleDate;

        public ViewHolder(@NonNull View itemView, NewsArticleListAdapter.OnItemClickListener listener) {
            super(itemView);

            articleImage = itemView.findViewById(R.id.item_news_article_image);
            articleTitle = itemView.findViewById(R.id.item_news_article__title);
            articleContent = itemView.findViewById(R.id.item_news_article_content);
            articleDate = itemView.findViewById(R.id.item_news_article_date);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
