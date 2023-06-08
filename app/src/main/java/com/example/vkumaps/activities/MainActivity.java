package com.example.vkumaps.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import com.example.vkumaps.fragment.HomeFragment;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChangeFragmentListener {
    private DrawerLayout drawerLayout;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_ADMISSION = 1;
    private static final int FRAGMENT_EVENT = 2;
    private static final int FRAGMENT_WEEKLY_SCHEDULE = 3;
    private int currentFragment = FRAGMENT_HOME;
    private Toolbar toolbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        int nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        AppCompatDelegate.setDefaultNightMode(nightMode);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

//        if (auth.getCurrentUser() != null){
//            findViewById(R.id.nav_account).setVisibility(View.GONE);
//            findViewById(R.id.nav_logout).setVisibility(View.VISIBLE);
//        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new HomeFragment(this));
        transaction.commit();
        currentFragment = FRAGMENT_HOME;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_maps) {
            if (currentFragment != FRAGMENT_HOME) {
                replaceFragment(new HomeFragment(this));
                currentFragment = FRAGMENT_HOME;
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_admissions) {
            if (currentFragment != FRAGMENT_ADMISSION) {
                replaceFragment(new AdmissionsFragment(this));
                currentFragment = FRAGMENT_ADMISSION;
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_event) {
            if (currentFragment != FRAGMENT_EVENT) {
                replaceFragment(new EventFragment(this));
                currentFragment = FRAGMENT_EVENT;
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_week) {
            if (currentFragment != FRAGMENT_WEEKLY_SCHEDULE) {
                //code..
            }
        } else if (id == R.id.nav_account) {
            openAccount();
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_permission) {
            openSettingPermission();
        } else if(id == R.id.nav_call){
            openCall();
        } else if(id == R.id.nav_mail){
            openEMail();
        }
        return true;
    }

    private void logout() {
        if(auth.getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void openAccount() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void openEMail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"daotao@vku.udn.vn"});
        startActivity(intent);
    }

    private void openCall() {
        String phoneNumber = " 02366552688"; // Số điện thoại cần gọi
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void openSettingPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (HomeFragment.currentstate == 0) {
                super.onBackPressed();
            } else {
                HomeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                HomeFragment.currentstate = 0;
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
}