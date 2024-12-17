package com.example.konnect_v2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ContactMeFragment extends Fragment implements Support{

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_me, container, false);
        ImageButton callButton = view.findViewById(R.id.call_button);
        ImageButton emailButton = view.findViewById(R.id.email_button);
        ImageView facebookImage = view.findViewById(R.id.facebook_image);
        ImageView instagramImage = view.findViewById(R.id.instagram_image);
        ImageView twitterImage = view.findViewById(R.id.twitter_image);
        ImageView linkedinImage = view.findViewById(R.id.linkedin_image);
        ImageView githubImage = view.findViewById(R.id.github_image);
        callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel: " + phoneNumber));
            startActivity(callIntent);
        });
        emailButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + emailAddress));
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });
        facebookImage.setOnClickListener(v -> openUrl(facebookUrl));
        instagramImage.setOnClickListener(v -> openUrl(instagramUrl));
        twitterImage.setOnClickListener(v -> openUrl(twitterUrl));
        linkedinImage.setOnClickListener(v -> openUrl(linkedinUrl));
        githubImage.setOnClickListener(v -> openUrl(githubUrl));
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setNavigationVisibility(false);
            DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setNavigationVisibility(true);
            DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }
    private void openUrl(String url) {
       Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
       startActivity(browserIntent);
    }
}