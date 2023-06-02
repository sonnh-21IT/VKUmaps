package com.example.vkumaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vkumaps.fragment.FavoriteFragment;
import com.example.vkumaps.fragment.HistoryFragment;
import com.example.vkumaps.fragment.HomeFragment;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChangeFragmentListener {
    private DrawerLayout drawerLayout;
    private static final int FRAGMENT_HOME=0;
    private static final int FRAGMENT_FAVORITE=1;
    private static final int FRAGMENT_HISTORY=2;
    private int currentFragment=FRAGMENT_HOME;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment(this));
        currentFragment=FRAGMENT_HOME;
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_home){
            if (currentFragment!=FRAGMENT_HOME){
                replaceFragment(new HomeFragment(this));
                currentFragment=FRAGMENT_HOME;
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }else if(id==R.id.nav_favorite){
            if (currentFragment!=FRAGMENT_FAVORITE){
                replaceFragment(new FavoriteFragment(this));
                currentFragment=FRAGMENT_FAVORITE;
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }else if(id==R.id.nav_history){
            if (currentFragment!=FRAGMENT_HISTORY){
                replaceFragment(new HistoryFragment(this));
                currentFragment=FRAGMENT_HISTORY;
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,fragment);
        transaction.commit();
    }

    @Override
    public void changeTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
        Toast.makeText(getApplicationContext(),title,Toast.LENGTH_SHORT).show();
    }
}