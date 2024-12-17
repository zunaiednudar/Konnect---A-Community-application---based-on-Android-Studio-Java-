package com.example.konnect_v2;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class CreateFragment extends Fragment implements Support {
    String[] createTypes;
    ArrayList<String> allSubKonnectsTitle = new ArrayList<>();
    HashMap<String, String> subKonnects = new HashMap<>();
    ArrayAdapter<String> arrayAdapterType, arrayAdapterSubKonnect;

    Button createButton, clearPostPicturesButton;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        createTypes = getResources().getStringArray(R.array.create_types);

        arrayAdapterType = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, createTypes);
        AutoCompleteTextView chooseCreateType = view.findViewById(R.id.choose_create_type);
        chooseCreateType.setAdapter(arrayAdapterType);

        arrayAdapterSubKonnect = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, allSubKonnectsTitle);
        AutoCompleteTextView chooseSubKonnect = view.findViewById(R.id.choose_subkonnect);
        chooseSubKonnect.setAdapter(arrayAdapterSubKonnect);
        chooseSubKonnect.setEnabled(false);
        chooseSubKonnect.setAlpha(0.5f);

        createButton = view.findViewById(R.id.create_button);
        clearPostPicturesButton = view.findViewById(R.id.clear_post_pictures_button);

        contentTitle = view.findViewById(R.id.content_title);
        contentDescription = view.findViewById(R.id.content_description);

        imageButton1 = view.findViewById(R.id.upload_image1);
        imageButton2 = view.findViewById(R.id.upload_image2);
        imageButton3 = view.findViewById(R.id.upload_image3);
        imageButton4 = view.findViewById(R.id.upload_image4);

        tapToAddImage1 = view.findViewById(R.id.tap_to_add_image_1);
        tapToAddImage2 = view.findViewById(R.id.tap_to_add_image_2);
        tapToAddImage3 = view.findViewById(R.id.tap_to_add_image_3);
        tapToAddImage4 = view.findViewById(R.id.tap_to_add_image_4);

        frameLayoutImage1 = view.findViewById(R.id.frame_layout_upload_image1);
        frameLayoutImage2 = view.findViewById(R.id.frame_layout_upload_image2);
        frameLayoutImage3 = view.findViewById(R.id.frame_layout_upload_image3);
        frameLayoutImage4 = view.findViewById(R.id.frame_layout_upload_image4);

        clearPostPicturesButton.setOnClickListener(v -> eraseAllImages());
        chooseSubKonnect.setOnClickListener(v -> {
            if (!chooseSubKonnect.isEnabled()) {
                showToast("Please select a type first");
            }
        });

        String chooseSubkonnect = "Choose Subkonnect";

        chooseCreateType.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedType = createTypes[position];
            chooseSubKonnect.setText(chooseSubkonnect, false);
            if ("SubKonnect".equalsIgnoreCase(selectedType)) {
                chooseSubKonnect.setEnabled(false);
                chooseSubKonnect.setAlpha(0.5f);

                clearPostPicturesButton.setVisibility(View.GONE);
                frameLayoutImage1.setVisibility(View.GONE);
                frameLayoutImage2.setVisibility(View.GONE);
                frameLayoutImage3.setVisibility(View.GONE);
                frameLayoutImage4.setVisibility(View.GONE);
            } else {
                chooseSubKonnect.setEnabled(true);
                chooseSubKonnect.setAlpha(1f);

                clearPostPicturesButton.setVisibility(View.VISIBLE);
                frameLayoutImage1.setVisibility(View.VISIBLE);
                frameLayoutImage2.setVisibility(View.VISIBLE);
                frameLayoutImage3.setVisibility(View.VISIBLE);
                frameLayoutImage4.setVisibility(View.VISIBLE);
            }
        });

        createButton.setOnClickListener(v -> {
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
                String ownerId = currentUser.getUid();
                String selectedType = chooseCreateType.getText().toString();
                String selectedSubKonnect = chooseSubKonnect.getText().toString();
                String key;

                if ("SubKonnect".equalsIgnoreCase(selectedType)) {
                    key = subKonnectsReference.push().getKey();

                    if (key != null) {
                        SubKonnect newSubKonnect = new SubKonnect(ownerId, key, title, description, new ArrayList<>());
                        subKonnectsReference.child(key).setValue(newSubKonnect);
                        showToast(selectedType + " created successfully");

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                } else if ("Post".equalsIgnoreCase(selectedType)) {
                    if (selectedSubKonnect.isEmpty() || !subKonnects.containsKey(selectedSubKonnect)) {
                        showToast("Choose SubKonnect first");
                        return;
                    }

                    key = postsReference.push().getKey();
                    if (key != null) {
                        if (encodedImage1 != null) storeImageInFirestore(encodedImage1, key + "_1");
                        if (encodedImage2 != null) storeImageInFirestore(encodedImage2, key + "_2");
                        if (encodedImage3 != null) storeImageInFirestore(encodedImage3, key + "_3");
                        if (encodedImage4 != null) storeImageInFirestore(encodedImage4, key + "_4");

                        Post newPost = new Post(ownerId, subKonnects.get(selectedSubKonnect), key, title, description, new Date(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                        postsReference.child(key).setValue(newPost);

                        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);

                                    if (user != null) {
                                        ArrayList<String> postIds = user.getPostIds();

                                        if (postIds == null) postIds = new ArrayList<>();
                                        postIds.add(key);

                                        user.setPostIds(postIds);
                                    }
                                    usersReference.child(ownerId).setValue(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        showToast(selectedType + " created successfully");
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                } else {
                    showToast("Choose a create type");
                }
            } else {
//                Toast.makeText(requireContext(), "No user is signed in.", Toast.LENGTH_SHORT).show();
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

        subKonnectsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                allSubKonnectsTitle.clear();
                subKonnects.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        SubKonnect subKonnect = childSnapshot.getValue(SubKonnect.class);

                        if (subKonnect != null) {
                            assert currentUser != null;
                            ArrayList<String> memberIds = subKonnect.getMemberIds();

                            if (memberIds != null) {
                                if (memberIds.contains(currentUser.getUid())) {
                                    allSubKonnectsTitle.add(subKonnect.getSubKonnectTitle());
                                    subKonnects.put(subKonnect.getSubKonnectTitle(), subKonnect.getSubKonnectId());
                                }
                            }

                            if (Objects.equals(subKonnect.getOwnerId(), currentUser.getUid())) {
                                allSubKonnectsTitle.add(subKonnect.getSubKonnectTitle());
                                subKonnects.put(subKonnect.getSubKonnectTitle(), subKonnect.getSubKonnectId());
                            }
                        }
                    }
                    arrayAdapterSubKonnect.notifyDataSetChanged();
                } else {
                    Log.e("CREATE_FRAGMENT__SUBKONNECT", "No SubKonnect found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
        return view;
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
                    if (isAdded()) {
                        if (task.isSuccessful()) {
                            Log.d("CREATE_FRAGMENT__IMAGE", "Image stored in Firestore");
                        } else {
                            Log.e("CREATE_FRAGMENT__IMAGE", "Error storing image in Firestore");
                        }
                    }
                });
    }

    private Bitmap uriToBitmap(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
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
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}