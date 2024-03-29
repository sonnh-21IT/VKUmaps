package com.example.vkumaps.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vkumaps.R;
import com.example.vkumaps.adapters.SliderAdapter;
import com.example.vkumaps.helpers.PrefManager;

public class OnBoardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;

    Button btn, btnNext;

    SliderAdapter sliderAdapter;

    TextView[] dots;
    private int currentItem;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        int nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        AppCompatDelegate.setDefaultNightMode(nightMode);

        PrefManager prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            // Ứng dụng đã được cài đặt lần đầu trước đó, thực hiện hành động tương ứng (ví dụ: chuyển đến màn hình chính)
            launchHomeScreen();
            finish();
        } else {
            // Ứng dụng được cài đặt lần đầu, hiển thị màn hình giới thiệu hoặc hướng dẫn
            // Tiếp tục với các bước cài đặt lần đầu
        }

        //hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        btn = findViewById(R.id.get_started_btn);
        btnNext = findViewById(R.id.next_btn);
        addDots(0);

        viewPager.addOnPageChangeListener(changeListener);

        //Call Adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnBoardingActivity.this, MainActivity.class));
                prefManager.setFirstTimeLaunch(false);
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentItem >= 0 && currentItem < 2) {
                    viewPager.setCurrentItem(currentItem + 1);
                }
            }
        });
    }

    private void launchHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Kết thúc Activity chính
    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            int color = ContextCompat.getColor(getApplicationContext(), R.color.grey);
            dots[i].setTextColor(color);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.status));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDots(position);
            currentItem = position;
            if (position == 0) {
                btn.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                btn.setVisibility(View.INVISIBLE);
            } else {
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.slide_animation);
                btn.setAnimation(animation);
                btn.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}