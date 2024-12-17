package com.example.konnect_v2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Support {
    private DrawerLayout drawerLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_feedback) {
            openFragment(new FeedbackFragment());
        } else if (itemId == R.id.nav_terms_and_conditions) {
            openFragment(new TermsAndConditionsFragment());
        } else if (itemId == R.id.nav_contact_us) {
            openFragment(new ContactMeFragment());
        } else if (itemId == R.id.nav_share) {
            openFragment(new ShareFragment());
        } else if (itemId == R.id.nav_news) {
            openFragment(new NewsFragment());
        } else {
            openFragment(new LogoutFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String key = currentUser.getUid();
            usersReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            NavigationView navigationView = findViewById(R.id.navigation_drawer);
                            View headerView = navigationView.getHeaderView(0);

                            ImageView drawerImage = headerView.findViewById(R.id.drawer_image);

                            if (user.getAvatarUrl() != null) {
                                Picasso.get().load(user.getAvatarUrl()).into(drawerImage);
                            } else {
                                drawerImage.setImageResource(R.drawable.icon_account_circle_black_24);
                            }

                            TextView drawerUsername = headerView.findViewById(R.id.drawer_username);
                            TextView drawerEmail = headerView.findViewById(R.id.drawer_email);

                            if (drawerUsername != null && drawerEmail != null) {
                                drawerUsername.setText(user.getUsername());
                                drawerEmail.setText(user.getEmail());
                            } else {
                                Log.e("DEBUG", "TextViews are null in NavigationDrawer header.");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast(getApplicationContext(), "Error");
                }
            });
        }

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                openFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_users) {
                openFragment(new UsersFragment());
                return true;
            } else if (itemId == R.id.nav_create) {
                openFragment(new CreateFragment());
                return true;
            } else if (itemId == R.id.nav_communities) {
                openFragment(new CommunitiesFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                openFragment(new ProfileFragment());
                return true;
            } else return false;
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());
    }

    public void setNavigationVisibility(boolean isVisible) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_appbar);
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        bottomNavigationView.setVisibility(visibility);
        toolbar.setVisibility(visibility);
        bottomAppBar.setVisibility(visibility);
    }
}