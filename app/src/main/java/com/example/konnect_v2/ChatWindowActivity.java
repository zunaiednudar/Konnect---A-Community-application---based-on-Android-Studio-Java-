package com.example.konnect_v2;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatWindowActivity extends AppCompatActivity implements Support {
    String senderId, receiverAvatarUrl, receiverId, receiverName, chatId;

    ImageView receiverProfilePicture;
    TextView receiverNameText;
    EditText messageText;
    ImageButton messageSendButton;

    RecyclerView recyclerViewMessage;
    MessageListAdapter messageListAdapter;
    ArrayList<MessageListModel> allMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        receiverName = getIntent().getStringExtra("receiver_name");
        receiverAvatarUrl = getIntent().getStringExtra("receiver_avatar_url");
        receiverId = getIntent().getStringExtra("receiver_id");
        chatId = getIntent().getStringExtra("chat_id");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        assert currentUser != null;
        senderId = currentUser.getUid();

        receiverNameText = findViewById(R.id.receiver_name);
        messageText = findViewById(R.id.message_text);
        messageSendButton = findViewById(R.id.message_send_button);
        recyclerViewMessage = findViewById(R.id.recycler_view_message);

        receiverProfilePicture = findViewById(R.id.receiver_profile_picture);
        if (receiverAvatarUrl != null)
            Picasso.get().load(receiverAvatarUrl).into(receiverProfilePicture);
        else receiverProfilePicture.setImageResource(R.drawable.icon_account_circle_black_100);

        receiverNameText.setText(receiverName);

//        Loads all the current messages
        allMessages = new ArrayList<>();

        chatsReference.child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    allMessages.clear();
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat != null) {
                        ArrayList<Message> messages = chat.getMessages();
                        if (messages != null && !messages.isEmpty()) {
                            for (Message message : messages) {
                                MessageListModel messageListModel = new MessageListModel(message.getSenderId(), chatId, message.getMessage(), message.getTimeStamp());
                                allMessages.add(messageListModel);

//                            Fetch user data
                                int index = allMessages.size() - 1;
                                fetchUserDate(index, message.getSenderId());
                            }
                        }
                    }
                    messageListAdapter.notifyDataSetChanged();
                    if (allMessages.size() > 0) {
                        recyclerViewMessage.scrollToPosition(allMessages.size() - 1);
                    }
                } else {
                    Log.e("CHAT_WINDOW_ACTIVITY__MESSAGE", "No message found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CHAT_WINDOW_ACTIVITY__MESSAGE", databaseError.getMessage());
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatWindowActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);
        messageListAdapter = new MessageListAdapter(ChatWindowActivity.this, allMessages);
        recyclerViewMessage.setAdapter(messageListAdapter);
//

        messageSendButton.setOnClickListener(v -> {
            String messageString = messageText.getText().toString().trim();
            if (messageString.isEmpty()) {
                showToast(ChatWindowActivity.this, "Enter the message first");
                return;
            }
            messageText.setText("");
            Message message = new Message(senderId, messageString, new Date());

//                Adding new message to the chat message list
            chatsReference.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Chat chat = dataSnapshot.getValue(Chat.class);

                        if (chat != null) {
                            ArrayList<Message> allMessages = chat.getMessages();
                            if (allMessages == null) allMessages = new ArrayList<>();
                            allMessages.add(message);
                            chat.setMessages(allMessages);
                            chatsReference.child(chatId).setValue(chat);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }

    private void fetchUserDate(int index, String userId) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        allMessages.get(index).setSenderAvatarUrl(user.getAvatarUrl());
                        messageListAdapter.notifyItemChanged(index);
                    }
                } else {
                    Log.e("CHAT_WINDOW_ACTIVITY__USER", "No user found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CHAT_WINDOW_ACTIVITY__USER", databaseError.getMessage());
            }
        });
    }
}