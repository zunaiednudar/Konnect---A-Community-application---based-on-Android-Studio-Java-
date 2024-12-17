package com.example.konnect_v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostEditActivity extends AppCompatActivity implements Support {
    Button updateButton, clearPostPicturesButton;
    EditText contentTitle, contentDescription;

    private String encodedImage1 = null;
    private String encodedImage2 = null;
    private String encodedImage3 = null;
    private String encodedImage4 = null;

    ImageButton imageButton1, imageButton2, imageButton3, imageButton4, lastClickedImageButton;
    ImageView tapToAddImage1, tapToAddImage2, tapToAddImage3, tapToAddImage4;
    FrameLayout frameLayoutImage1, frameLayoutImage2, frameLayoutImage3, frameLayoutImage4;

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        updateButton = findViewById(R.id.post_edit_update_button);
        clearPostPicturesButton = findViewById(R.id.post_edit_clear_post_pictures_button);

        contentTitle = findViewById(R.id.post_edit_content_title);
        contentDescription = findViewById(R.id.post_edit_content_description);

        imageButton1 = findViewById(R.id.post_edit_upload_image1);
        imageButton2 = findViewById(R.id.post_edit_upload_image2);
        imageButton3 = findViewById(R.id.post_edit_upload_image3);
        imageButton4 = findViewById(R.id.post_edit_upload_image4);

        tapToAddImage1 = findViewById(R.id.post_edit_tap_to_add_image_1);
        tapToAddImage2 = findViewById(R.id.post_edit_tap_to_add_image_2);
        tapToAddImage3 = findViewById(R.id.post_edit_tap_to_add_image_3);
        tapToAddImage4 = findViewById(R.id.post_edit_tap_to_add_image_4);

        frameLayoutImage1 = findViewById(R.id.post_edit_frame_layout_upload_image1);
        frameLayoutImage2 = findViewById(R.id.post_edit_frame_layout_upload_image2);
        frameLayoutImage3 = findViewById(R.id.post_edit_frame_layout_upload_image3);
        frameLayoutImage4 = findViewById(R.id.post_edit_frame_layout_upload_image4);


        clearPostPicturesButton.setOnClickListener(v -> eraseAllImages());

        String postId = getIntent().getStringExtra("postId");
        String oldPostTitle = getIntent().getStringExtra("postTitle");
        String oldPostDescription = getIntent().getStringExtra("postDescription");

        contentTitle.setText(oldPostTitle);
        contentDescription.setText(oldPostDescription);

        fetchPostImages(postId);


        updateButton.setOnClickListener(v -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            String title = contentTitle.getText().toString().trim();
            String description = contentDescription.getText().toString().trim();

            if (title.isEmpty()) {
                showToast("Title field cannot be empty");
                return;
            }

            if (description.isEmpty()) {
                showToast("Description field cannot be empty");
                return;
            }

            if (currentUser != null) {
                if (postId != null) {
                    if (encodedImage1 != null) storeImageInFirestore(encodedImage1, postId + "_1");
                    else deleteImageReferenceInFirestore(postId + "_1");

                    if (encodedImage2 != null) storeImageInFirestore(encodedImage2, postId + "_2");
                    else deleteImageReferenceInFirestore(postId + "_2");

                    if (encodedImage3 != null) storeImageInFirestore(encodedImage3, postId + "_3");
                    else deleteImageReferenceInFirestore(postId + "_3");

                    if (encodedImage4 != null) storeImageInFirestore(encodedImage4, postId + "_4");
                    else deleteImageReferenceInFirestore(postId + "_4");

                    postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Post post = dataSnapshot.getValue(Post.class);

                                if (post != null) {
                                    post.setTitle(title);
                                    post.setDescription(description);
                                    postsReference.child(postId).setValue(post);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    showToast("Post updated successfully");
                    Intent intent = new Intent(PostEditActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        imageButton1.setOnClickListener(v -> {
            lastClickedImageButton = imageButton1;
            openGallery();
        });

        imageButton2.setOnClickListener(v -> {
            lastClickedImageButton = imageButton2;
            openGallery();
        });

        imageButton3.setOnClickListener(v -> {
            lastClickedImageButton = imageButton3;
            openGallery();
        });

        imageButton4.setOnClickListener(v -> {
            lastClickedImageButton = imageButton4;
            openGallery();
        });
    }

    private void eraseAllImages() {
        imageButton1.setImageResource(R.color.card_background);
        imageButton2.setImageResource(R.color.card_background);
        imageButton3.setImageResource(R.color.card_background);
        imageButton4.setImageResource(R.color.card_background);

        lastClickedImageButton = null;
        encodedImage1 = null;
        encodedImage2 = null;
        encodedImage3 = null;
        encodedImage4 = null;

        tapToAddImage1.setVisibility(View.VISIBLE);
        tapToAddImage2.setVisibility(View.VISIBLE);
        tapToAddImage3.setVisibility(View.VISIBLE);
        tapToAddImage4.setVisibility(View.VISIBLE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void deleteImageReferenceInFirestore(String path) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("postImages").document(path);

        // Remove the image reference from Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("image", FieldValue.delete());

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Image reference " + "image" + " deleted successfully."))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete image reference " + "image", e));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null && lastClickedImageButton != null) {
                Bitmap bitmap = uriToBitmap(selectedImageUri);

                if (bitmap != null) {
                    String base64Image = encodeImageToBase64(bitmap);

                    if (lastClickedImageButton == imageButton1) {
                        imageButton1.setImageURI(selectedImageUri);
                        tapToAddImage1.setVisibility(View.GONE);
                        encodedImage1 = base64Image;
                    } else if (lastClickedImageButton == imageButton2) {
                        imageButton2.setImageURI(selectedImageUri);
                        tapToAddImage2.setVisibility(View.GONE);
                        encodedImage2 = base64Image;
                    } else if (lastClickedImageButton == imageButton3) {
                        imageButton3.setImageURI(selectedImageUri);
                        tapToAddImage3.setVisibility(View.GONE);
                        encodedImage3 = base64Image;
                    } else if (lastClickedImageButton == imageButton4) {
                        imageButton4.setImageURI(selectedImageUri);
                        tapToAddImage4.setVisibility(View.GONE);
                        encodedImage4 = base64Image;
                    }

                    showToast("Image encoded successfully!");
                } else {
                    Log.e("CREATE_FRAGMENT__IMAGE", "Failed to encode image");
                }
            } else {
                Log.e("CREATE_FRAGMENT__IMAGE", "Failed to get image");
            }
        }
    }

    public void storeImageInFirestore(String base64Image, String path) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("image", base64Image);

        firestore.collection("postImages").document(path)
                .set(imageMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("CREATE_FRAGMENT__IMAGE", "Image stored in Firestore");
                    } else {
                        Log.e("CREATE_FRAGMENT__IMAGE", "Error storing image in Firestore");
                    }
                });
    }

    private void fetchPostImages(String postId) {
        retrievePostImagesFromFirestore(postId + "_1", 1);
        retrievePostImagesFromFirestore(postId + "_2", 2);
        retrievePostImagesFromFirestore(postId + "_3", 3);
        retrievePostImagesFromFirestore(postId + "_4", 4);
    }

    private void retrievePostImagesFromFirestore(String path, int number) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("postImages").document(path)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String base64Image = document.getString("image");
                            if (base64Image != null) {
                                Bitmap bitmap = decodeBase64ToBitmap(base64Image);
                                if (bitmap != null) {
                                    switch (number) {
                                        case 1:
                                            imageButton1.setImageBitmap(bitmap);
                                            tapToAddImage1.setVisibility(View.GONE);
                                            encodedImage1 = base64Image;
                                            break;
                                        case 2:
                                            imageButton2.setImageBitmap(bitmap);
                                            tapToAddImage2.setVisibility(View.GONE);
                                            encodedImage2 = base64Image;
                                            break;
                                        case 3:
                                            imageButton3.setImageBitmap(bitmap);
                                            tapToAddImage3.setVisibility(View.GONE);
                                            encodedImage3 = base64Image;
                                            break;
                                        case 4:
                                            imageButton4.setImageBitmap(bitmap);
                                            tapToAddImage4.setVisibility(View.GONE);
                                            encodedImage4 = base64Image;
                                            break;
                                    }
                                } else {
                                    Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "Failed to decode image");
                                }
                            } else {
                                Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "No image found");
                            }
                        } else {
                            Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "No image document found");
                        }
                    } else {
                        Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "Error retrieving image: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private Bitmap uriToBitmap(Uri uri) {
        try {
            InputStream inputStream = PostEditActivity.this.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encodeImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void showToast(String message) {
        Toast.makeText(PostEditActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}