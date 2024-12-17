package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileCustomizationActivity extends AppCompatActivity implements Support {
    String url = "https://api.myjson.online/v1/records/94a18379-f753-4d41-b0f4-76fd0befc711";
    EditText userBio;
    Button registerButton;
    BottomSheetDialog bottomSheetDialog;
    Button chooseAvatarButton;
    ImageView avatar1, avatar2, avatar3, avatar4, avatar5, avatar6, avatar7, avatar8, avatar9, avatar10, avatarNone, chosenAvatarImage;
    private int lastSelectedAvatar;
    private HashMap<Integer, String> avatarUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_customization);

        lastSelectedAvatar = -1;
        avatarUrls = new HashMap<>();

        userBio = findViewById(R.id.user_bio);
        registerButton = findViewById(R.id.register_button);
        chooseAvatarButton = findViewById(R.id.choose_avatar_button);
        chosenAvatarImage = findViewById(R.id.avatar_image);

//        Initially making the avatar image invisible
        chosenAvatarImage.setVisibility(View.GONE);

        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_choose_avatar_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        avatar1 = bottomSheetDialog.findViewById(R.id.avatar_1);
        avatar2 = bottomSheetDialog.findViewById(R.id.avatar_2);
        avatar3 = bottomSheetDialog.findViewById(R.id.avatar_3);
        avatar4 = bottomSheetDialog.findViewById(R.id.avatar_4);
        avatar5 = bottomSheetDialog.findViewById(R.id.avatar_5);
        avatar6 = bottomSheetDialog.findViewById(R.id.avatar_6);
        avatar7 = bottomSheetDialog.findViewById(R.id.avatar_7);
        avatar8 = bottomSheetDialog.findViewById(R.id.avatar_8);
        avatar9 = bottomSheetDialog.findViewById(R.id.avatar_9);
        avatar10 = bottomSheetDialog.findViewById(R.id.avatar_10);
        avatarNone = bottomSheetDialog.findViewById(R.id.avatar_none);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, jsonObject -> {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject avatar = jsonArray.getJSONObject(i);
                    int avatarId = avatar.getInt("avatar_id");
                    String avatarUrlDirectLink = getDirectLink(avatar.getString("avatar_url"));
                    avatarUrls.put(avatarId, avatarUrlDirectLink);
                    switch (avatarId) {
                        case 1:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar1);
                            break;
                        case 2:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar2);
                            break;
                        case 3:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar3);
                            break;
                        case 4:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar4);
                            break;
                        case 5:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar5);
                            break;
                        case 6:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar6);
                            break;
                        case 7:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar7);
                            break;
                        case 8:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar8);
                            break;
                        case 9:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar9);
                            break;
                        case 10:
                            Picasso.get().load(avatarUrlDirectLink).into(avatar10);
                            break;
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, Throwable::printStackTrace);

        requestQueue.add(jsonObjectRequest);
        chooseAvatarButton.setOnClickListener(v -> bottomSheetDialog.show());

        avatar1.setOnClickListener(v -> {
            lastSelectedAvatar = 1;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(1)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar2.setOnClickListener(v -> {
            lastSelectedAvatar = 2;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(2)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar3.setOnClickListener(v -> {
            lastSelectedAvatar = 3;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(3)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar4.setOnClickListener(v -> {
            lastSelectedAvatar = 4;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(4)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar5.setOnClickListener(v -> {
            lastSelectedAvatar = 5;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(5)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar6.setOnClickListener(v -> {
            lastSelectedAvatar = 6;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(6)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar7.setOnClickListener(v -> {
            lastSelectedAvatar = 7;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(7)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar8.setOnClickListener(v -> {
            lastSelectedAvatar = 8;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(8)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar9.setOnClickListener(v -> {
            lastSelectedAvatar = 9;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(9)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatar10.setOnClickListener(v -> {
            lastSelectedAvatar = 10;
            bottomSheetDialog.dismiss();
            avatarChosenMessage();
            Picasso.get().load(avatarUrls.get(10)).into(chosenAvatarImage);
            chosenAvatarImage.setVisibility(View.VISIBLE);
        });

        avatarNone.setOnClickListener(v -> {
            lastSelectedAvatar = -1;
            bottomSheetDialog.dismiss();
            chosenAvatarImage.setImageResource(R.color.primary_mid);
            chosenAvatarImage.setVisibility(View.GONE);
        });

        registerButton.setOnClickListener(v -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                String key = currentUser.getUid();
                usersReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User loadedUser = dataSnapshot.getValue(User.class);
                            assert loadedUser != null;
                            loadedUser.setBio(userBio.getText().toString());

                            if (lastSelectedAvatar != -1) loadedUser.setAvatarUrl(avatarUrls.get(lastSelectedAvatar));
                            usersReference.child(key).setValue(loadedUser);

                            Toast.makeText(ProfileCustomizationActivity.this, "Registration completed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileCustomizationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileCustomizationActivity.this, "User not found in database.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileCustomizationActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ProfileCustomizationActivity.this, "No user is signed in.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String getDirectLink(String googleDriveLink) {
//        Extract FILE_ID from the shareable link
        String[] parts = googleDriveLink.split("/");
//        FILE_ID is the 5th element in the URL parts
        String fileId = parts[5];
        return "https://drive.google.com/uc?id=" + fileId;
    }

    public void avatarChosenMessage() {
        Toast.makeText(ProfileCustomizationActivity.this, "Avatar chosen", Toast.LENGTH_SHORT).show();
    }
}