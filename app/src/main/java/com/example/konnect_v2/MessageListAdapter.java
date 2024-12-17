package com.example.konnect_v2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> implements Support, TimeAgoFormatter {
    Context context;
    ArrayList<MessageListModel> messageArrayList;

    public MessageListAdapter(Context context, ArrayList<MessageListModel> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        MessageListModel currentMessage = messageArrayList.get(position);

        if (currentMessage.getSenderAvatarUrl() != null)
            Picasso.get().load(currentMessage.getSenderAvatarUrl()).into(holder.senderImage);
        else holder.senderImage.setImageResource(R.drawable.icon_account_circle_black_24);

        holder.senderUsername.setText("");

        usersReference.child(currentMessage.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        holder.senderUsername.setText(user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (isValidUrl(currentMessage.getMessageDescription())) {
            setLinkToTextView(holder.messageDescription, currentMessage.getMessageDescription());
        } else {
            holder.messageDescription.setText(currentMessage.getMessageDescription());
        }

        holder.messageTimestamp.setText(TimeAgoFormatter.timeAgo(currentMessage.getMessageTimestamp()));

        holder.messageDescription.setOnLongClickListener(v -> {
            assert currentUser != null;
            if (Objects.equals(currentMessage.getSenderId(), currentUser.getUid()))
                showDeleteDialog(holder.itemView.getContext(), position, currentMessage, currentMessage.getChatId());
            return true;
        });
    }

    private void showDeleteDialog(Context context, int position, MessageListModel message, String chatId) {
        Typeface customFont = ResourcesCompat.getFont(context, R.font.acme);
        String deleteMessageTitle = "Delete Message";
        String deleteMessageText = "Are you sure you want to delete this message?";

        // Create a custom view for the AlertDialog
        TextView titleView = new TextView(context);
        titleView.setText(deleteMessageTitle);
        titleView.setTextSize(20);
        titleView.setTypeface(customFont);
        titleView.setPadding(40, 40, 40, 20);

        TextView messageView = new TextView(context);
        messageView.setText(deleteMessageText);
        messageView.setTextSize(16);
        messageView.setTypeface(customFont);
        messageView.setPadding(40, 20, 40, 40);

//        Create and show the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleView)
                .setView(messageView)
                .setPositiveButton("Delete", (dialogInterface, which) -> {
//                    Remove the message locally
                    messageArrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, messageArrayList.size());

//                    Delete from Firebase
                    deleteMessageFromFirebase(message, chatId);

                    showToast(context, "Message deleted");
                })
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(customFont);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(customFont);
        });

        dialog.show();
    }

    private void deleteMessageFromFirebase(MessageListModel message, String chatId) {
        chatsReference.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat != null) {
                        ArrayList<Message> messages = chat.getMessages();
                        if (messages != null) {
//                            Find and remove the message from Firebase
                            messages.removeIf(m -> m.getTimeStamp().equals(message.getMessageTimestamp())
                                    && m.getSenderId().equals(message.getSenderId()));
                            chat.setMessages(messages);
                            chatsReference.child(chatId).setValue(chat);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DELETE_MESSAGE", "Failed to delete message: " + databaseError.getMessage());
            }
        });
    }



    private boolean isValidUrl(String text) {
        return !TextUtils.isEmpty(text) && Patterns.WEB_URL.matcher(text).matches();
    }

    private void setLinkToTextView(TextView textView, String url) {
        SpannableString spannableString = new SpannableString(url);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
//                Open the URL in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                textView.getContext().startActivity(browserIntent);
            }
        };

        spannableString.setSpan(clickableSpan, 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView senderImage;
        TextView senderUsername, messageDescription, messageTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderImage = itemView.findViewById(R.id.item_message_sender_image);
            senderUsername = itemView.findViewById(R.id.item_message_sender_username);
            messageDescription = itemView.findViewById(R.id.item_message_description);
            messageTimestamp = itemView.findViewById(R.id.item_message_timestamp);
        }
    }
}

