package com.example.vkumaps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.vkumaps.R;
import com.example.vkumaps.databinding.ActivityBrowserBinding;

public class BrowserActivity extends AppCompatActivity{
    private ActivityBrowserBinding binding;
    private Toolbar toolbar;
    private static final String DOMAIN = "vku.udn.vn/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_browser);

        binding.webViewMain.getSettings().setJavaScriptEnabled(true);
        binding.webViewMain.getSettings().setDomStorageEnabled(true);

        Intent intent = getIntent();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String url = "";
        String title = "";
        if (intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
            title = intent.getStringExtra("title");
            Log.d("VKU", "onCreate: " + url);
            binding.webViewMain.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();

                    // Lấy tên miền của url được tải vào WebView
                    String domain = request.getUrl().parse(url).getHost();

                    // Kiểm tra nếu tên miền của url là tên miền được cho phép
                    if (domain.equals(DOMAIN)) {
                        //cho phép tải trang web
                        return false;
                    } else {
                        //không cho phép tải trang web
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        startActivity(intent);
                        return true;
                    }
                }
            });
            binding.webViewMain.loadUrl(url);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}