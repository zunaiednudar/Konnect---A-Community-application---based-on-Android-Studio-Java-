package com.example.konnect_v2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedbackListAdapter extends ArrayAdapter<Feedback> implements TimeAgoFormatter, Support {
    private final Activity context;
    private final List<Feedback> feedbacksList;

    public FeedbackListAdapter(Activity context, List<Feedback> feedbacksList) {
        super(context, R.layout.item_feedback, feedbacksList);
        this.context = context;
        this.feedbacksList = feedbacksList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Feedback currentFeedback = feedbacksList.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.item_feedback, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.feedbackDescription = convertView.findViewById(R.id.item_feedback_description);
            viewHolder.feedbackOwnerUsername = convertView.findViewById(R.id.item_feedback_owner_username);
            viewHolder.feedbackOwnerEmail = convertView.findViewById(R.id.item_feedback_owner_email);
            viewHolder.feedbackRatingBar = convertView.findViewById(R.id.item_feedback_rating_bar);
            viewHolder.feedbackPostDate = convertView.findViewById(R.id.item_feedback_post_date);
            viewHolder.feedbackOwnerImage = convertView.findViewById(R.id.item_feedback_owner_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.feedbackDescription.setText(currentFeedback.getFeedbackDescription());
        viewHolder.feedbackOwnerUsername.setText(currentFeedback.getFeedbackOwnerUsername());
        viewHolder.feedbackOwnerEmail.setText(currentFeedback.getFeedbackOwnerEmail());
        viewHolder.feedbackRatingBar.setRating(currentFeedback.getFeedbackRating());
        viewHolder.feedbackPostDate.setText(TimeAgoFormatter.timeAgo(currentFeedback.getFeedbackDate()));

        if (currentFeedback.getFeedbackOwnerAvatarUrl() != null)
            Picasso.get().load(currentFeedback.getFeedbackOwnerAvatarUrl()).into(viewHolder.feedbackOwnerImage);
        else viewHolder.feedbackOwnerImage.setImageResource(R.drawable.icon_account_circle_black_24);

        return convertView;
    }

    static class ViewHolder {
        TextView feedbackDescription, feedbackOwnerUsername, feedbackOwnerEmail, feedbackPostDate;
        RatingBar feedbackRatingBar;
        ImageView feedbackOwnerImage;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

