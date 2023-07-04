package com.example.vkumaps.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.example.vkumaps.fragment.AdmissionsFragment;
import com.example.vkumaps.fragment.EventFragment;
import com.example.vkumaps.fragment.FeedbackFragment;
import com.example.vkumaps.fragment.HomeFragment;
import com.example.vkumaps.fragment.LoginFragment;
import com.example.vkumaps.fragment.MyInfoFragment;
import com.example.vkumaps.fragment.SearchByAreaFragment;
import com.example.vkumaps.fragment.WeekScheduleFragment;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.listener.SharePlaceListener;
import com.example.vkumaps.models.MarkerModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChangeFragmentListener, SharePlaceListener {
    private DrawerLayout drawerLayout;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_ADMISSION = 1;
    private static final int FRAGMENT_EVENT = 2;
    private static final int FRAGMENT_SEARCH_BY_AREA = 3;
    private static final int FRAGMENT_WEEKLY_SCHEDULE = 4;
    private static final int FRAGMENT_ACCOUNT = 5;
    private static final int FRAGMENT_FEEDBACK = 6;
    private static final int FRAGMENT_MY_INFORMATION = 7;
    private int currentFragment = FRAGMENT_HOME;
    private FirebaseAuth auth;
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new HomeFragment(this,this));
        transaction.commit();
        currentFragment = FRAGMENT_HOME;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_main_maps: {
                if (currentFragment != FRAGMENT_HOME) {
                    fragment = new HomeFragment(this,this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_HOME;
                }
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
                if (auth.getCurrentUser()!=null){
                    if (currentFragment != FRAGMENT_MY_INFORMATION) {
                        fragment = new MyInfoFragment(this);
//                      replaceFragment(fragment);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        currentFragment = FRAGMENT_MY_INFORMATION;
                    }
                }else{
                    if (currentFragment != FRAGMENT_ACCOUNT) {
                        fragment = new LoginFragment(this);
//                    replaceFragment(fragment);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        currentFragment = FRAGMENT_ACCOUNT;
                    }
                }
                break;
            }
            case R.id.menu_timetable: {
                if (currentFragment != FRAGMENT_WEEKLY_SCHEDULE) {
                    fragment = new WeekScheduleFragment(this);
//                    replaceFragment(fragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    currentFragment = FRAGMENT_WEEKLY_SCHEDULE;
                }
                break;
            }
            case R.id.menu_logout: {
                logout();
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
                    if (fragment!=null){
                        drawerLayout.closeDrawer(GravityCompat.START);
                        replaceFragment(fragment,null);
                    }
                }
            });
        }
        return true;
    }

    private void logout() {
        if (auth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
//
//    private void openEMail() {
//        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setData(Uri.parse("mailto:"));
//        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"daotao@vku.udn.vn"});
//        startActivity(intent);
//    }
//
//    private void openCall() {
//        String phoneNumber = " 02366552688"; // Số điện thoại cần gọi
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse("tel:" + phoneNumber));
//        startActivity(intent);
//    }
//
//    private void openSettingPermission() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        startActivity(intent);
//    }

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

    private void replaceFragment(Fragment fragment,Bundle bundle) {
        if (bundle!=null){
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
    public void onNestedClick(MarkerModel markerModel,String name) {
        fragment = new HomeFragment(this,this);

        Bundle bundle = new Bundle();
        bundle.putParcelable("marker", markerModel);
        bundle.putString("name", name);
        replaceFragment(fragment, bundle);
        currentFragment = FRAGMENT_HOME;
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
        String uri = "https://www.google.com/maps/place/" + placeName + "/@" + latLng.latitude + "," + latLng.longitude;

        // Create an Intent to share the place data
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this place: " + uri);
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
}