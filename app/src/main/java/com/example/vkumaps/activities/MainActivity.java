package com.example.vkumaps.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vkumaps.R;
import com.example.vkumaps.fragment.AboutFragment;
import com.example.vkumaps.fragment.AdmissionsFragment;
import com.example.vkumaps.fragment.EventFragment;
import com.example.vkumaps.fragment.FeedbackFragment;
import com.example.vkumaps.fragment.HomeFragment;
import com.example.vkumaps.fragment.LoginFragment;
import com.example.vkumaps.fragment.MyInfoFragment;
import com.example.vkumaps.fragment.SearchByAreaFragment;
import com.example.vkumaps.fragment.WeekScheduleFragment;
import com.example.vkumaps.listener.BottomSheetListener;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.MarkerModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChangeFragmentListener, BottomSheetListener, MyInfoFragment.MyInfoListener {
    private DrawerLayout drawerLayout;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_ADMISSION = 1;
    private static final int FRAGMENT_EVENT = 2;
    private static final int FRAGMENT_SEARCH_BY_AREA = 3;
    private static final int FRAGMENT_WEEKLY_SCHEDULE = 4;
    private static final int FRAGMENT_ACCOUNT = 5;
    private static final int FRAGMENT_FEEDBACK = 6;
    private static final int FRAGMENT_MY_INFORMATION = 7;
    private static final int FRAGMENT_ABOUT = 8;
    private int currentFragment = FRAGMENT_HOME;
    private FirebaseAuth auth;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SplashScreen.installSplashScreen(this);
        int nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        AppCompatDelegate.setDefaultNightMode(nightMode);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragment = new HomeFragment(this, this);
        Intent intent = getIntent();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (intent != null && intent.getStringExtra("startPoint") != null && intent.getStringExtra("endPoint") != null) {
            Bundle bundle = new Bundle();
            bundle.putString("startPoint", intent.getStringExtra("startPoint"));
            bundle.putString("endPoint", intent.getStringExtra("endPoint"));
            fragment.setArguments(bundle);
        }
        if (savedInstanceState == null) {
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
            currentFragment = FRAGMENT_HOME;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_main_maps: {
                if (currentFragment != FRAGMENT_HOME) {
                    fragment = new HomeFragment(this, this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_HOME;
                }
                break;
            }
            case R.id.menu_direction: {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(getApplicationContext(), DirectionActivity.class));
                break;
            }
            case R.id.menu_admissions: {
                if (currentFragment != FRAGMENT_ADMISSION) {
                    fragment = new AdmissionsFragment(this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_ADMISSION;
                }
                break;
            }
            case R.id.menu_event: {
                if (currentFragment != FRAGMENT_EVENT) {
                    fragment = new EventFragment(this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_EVENT;
                }
                break;
            }
            case R.id.menu_about: {
                if (currentFragment != FRAGMENT_ABOUT) {
                    fragment = new AboutFragment(this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_ABOUT;
                }
                break;
            }
            case R.id.menu_search_by_area: {
                if (currentFragment != FRAGMENT_SEARCH_BY_AREA) {
                    fragment = new SearchByAreaFragment(this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_SEARCH_BY_AREA;
                }
                break;
            }
            case R.id.menu_rating: {
                if (currentFragment != FRAGMENT_FEEDBACK) {
                    fragment = new FeedbackFragment(this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_FEEDBACK;
                }
                break;
            }
            case R.id.menu_account: {
                if (auth.getCurrentUser() != null) {
                    if (currentFragment != FRAGMENT_MY_INFORMATION) {
                        fragment = new MyInfoFragment(this, this);
//                      replaceFragment(fragment);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        currentFragment = FRAGMENT_MY_INFORMATION;
                    }
                } else {
                    if (currentFragment != FRAGMENT_ACCOUNT) {
                        fragment = new LoginFragment(this);
//                    replaceFragment(fragment);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        currentFragment = FRAGMENT_ACCOUNT;
                    }
                }
                break;
            }
            default: {
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Wait for the drawer to close completely before changing fragment
            drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (fragment != null) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        replaceFragment(fragment, null);
                    }
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    super.onDrawerStateChanged(newState);
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (HomeFragment.currentState == 0) {
                super.onBackPressed();
            } else {
                HomeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                HomeFragment.currentState = 0;
            }
        }
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void changeTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onNestedClick(MarkerModel markerModel, String name) {
        fragment = new HomeFragment(this, this);

        Bundle bundle = new Bundle();
        bundle.putParcelable("marker", markerModel);
        bundle.putString("name", name);
        replaceFragment(fragment, bundle);

        currentFragment = FRAGMENT_HOME;
    }

    @Override
    public void onDirectionClick(String name) {
        Intent intent = new Intent(getApplicationContext(), DirectionActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSharePlace(Marker marker) {
        // Get the place data from the marker
        String placeName = marker.getTitle();
        LatLng latLng = marker.getPosition();

        // Create a Uri that contains the place data
        String locationUri = "http://maps.google.com/maps?q=" + latLng.latitude + "," + latLng.longitude;

        // Create an Intent to share the place data
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ghé thăm " + marker.getTitle() + " trường VKU " + locationUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, placeName);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onLearnResultClick() {

    }

    @Override
    public void onLogout() {
        if (auth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTimeTableClick() {
        if (currentFragment != FRAGMENT_WEEKLY_SCHEDULE) {
            fragment = new WeekScheduleFragment(this);
            replaceFragment(fragment, null);
            drawerLayout.closeDrawer(GravityCompat.START);
            currentFragment = FRAGMENT_WEEKLY_SCHEDULE;
        }
    }
}