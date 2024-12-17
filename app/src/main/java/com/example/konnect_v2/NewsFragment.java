package com.example.konnect_v2;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NewsFragment extends Fragment implements Support {
    RecyclerView recyclerViewNewsArticles;
    NewsArticleListAdapter newsArticleListAdapter;
    ArrayList<NewsArticleListModel> newsArticlesArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsArticlesArrayList = new ArrayList<>();
        fetchNews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerViewNewsArticles = view.findViewById(R.id.recycler_view_news_fragment);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerViewNewsArticles.addItemDecoration(dividerItemDecoration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewNewsArticles.setLayoutManager(linearLayoutManager);
        newsArticleListAdapter = new NewsArticleListAdapter(requireContext(), newsArticlesArrayList);
        recyclerViewNewsArticles.setAdapter(newsArticleListAdapter);

        newsArticleListAdapter.setOnItemClickListener(position -> {
            NewsArticleListModel clickedNewsArticle = newsArticlesArrayList.get(position);

            String url = clickedNewsArticle.getCanonicalLink();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        return view;
    }

    private void fetchNews() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                response -> {
                    parseNewsResponse(response.toString());

                    newsArticleListAdapter.notifyDataSetChanged();
                },
                error -> Log.e(TAG, "Volley error: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-rapidapi-key", API_KEY);
                headers.put("x-rapidapi-host", API_HOST);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void parseNewsResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray articlesArray = jsonObject.getJSONArray("data"); // Adjust key if different

            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleObject = articlesArray.getJSONObject(i);

                JSONObject articleAttributes = articleObject.getJSONObject("attributes");
                String publishOn = articleAttributes.getString("publishOn");
                String imageUrl = articleAttributes.getString("gettyImageUrl");
                String title = articleAttributes.getString("title");
                String htmlContent = articleAttributes.getString("content");

                Spanned spanned = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY);
                String plainContent = spanned.toString();

                JSONObject articleLinks = articleObject.getJSONObject("links");
                String articleCanonicalLink = articleLinks.getString("canonical");

                NewsArticleListModel article = new NewsArticleListModel(title, plainContent, articleCanonicalLink, imageUrl, publishOn);
                newsArticlesArrayList.add(article);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing Error: " + e.getMessage(), e);
        }
    }
}