package com.example.vkumaps.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vkumaps.R;
import com.example.vkumaps.databinding.ActivityBrowserBinding;
import com.example.vkumaps.helpers.PDFRendererHelper;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView pdfViewer, imgViewer;
    private TextView btnPrev, btnNext, currentPage, getStarted;
    private int currentPageIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        int nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        AppCompatDelegate.setDefaultNightMode(nightMode);
        setContentView(R.layout.activity_intro);
        initView();
    }

    private void initView() {
        pdfViewer = findViewById(R.id.pdfViewer);
        btnNext = findViewById(R.id.next);
        btnPrev = findViewById(R.id.prev);
        imgViewer = findViewById(R.id.imgViewer);
        currentPage = findViewById(R.id.current_page);
        pdfViewer.setVisibility(View.GONE);
        getStarted=findViewById(R.id.get_started);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        getStarted.setOnClickListener(this);
        currentPage.setText((currentPageIndex + 2) + "/3");
    }

    private void renderPDFPage(int pageIndex) {
        PDFRendererHelper.renderPDF(this, pageIndex, new PDFRendererHelper.OnPDFRenderListener() {
            @Override
            public void onPDFRendered(Bitmap bitmap) {
                // Hiển thị bitmap lên ImageView
                pdfViewer.setImageBitmap(bitmap);
            }

            @Override
            public void onPDFRenderError(String errorMessage) {
                // Xử lý lỗi khi hiển thị PDF
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prev: {
                currentPageIndex--;
                break;
            }
            case R.id.next: {
                currentPageIndex++;
                break;
            }
            case R.id.get_started: {
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
                finish(); // Kết thúc activity hiện tại
                break;
            }
            default:
                break;
        }
        if (currentPageIndex < -1) {
            currentPageIndex = 1;
        }
        if (currentPageIndex > 2) {
            currentPageIndex = 0;
        }
        if (currentPageIndex != 0 && currentPageIndex != 1) {
            pdfViewer.setVisibility(View.GONE);
            currentPage.setText("1/3");
        } else {
            pdfViewer.setVisibility(View.VISIBLE);
            renderPDFPage(currentPageIndex);
            currentPage.setText((currentPageIndex + 2) + "/3");
        }

    }
}