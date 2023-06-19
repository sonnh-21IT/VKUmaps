package com.example.vkumaps.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.vkumaps.R;
import com.example.vkumaps.databinding.ActivityBrowserBinding;

public class BrowserActivity extends AppCompatActivity {
    private ActivityBrowserBinding binding;
    private Toolbar toolbar;

    private ActionBar actionBar;
    private static final String DOMAIN = "vku.udn.vn";
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_browser);
        binding.webViewMain.setVisibility(View.GONE);
        binding.pageError.setVisibility(View.GONE);
        binding.loaderView.setVisibility(View.VISIBLE);
        binding.webViewMain.getSettings().setJavaScriptEnabled(true);
        binding.webViewMain.getSettings().setDomStorageEnabled(true);

        Intent intent = getIntent();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        if (intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
//            title = intent.getStringExtra("title");

            Log.d("VKU", "onCreate: " + url);
            binding.webViewMain.loadUrl(url);
            binding.webViewMain.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();

                    // Lấy tên miền của url được tải vào WebView
                    String domain = request.getUrl().parse(url).getHost();

                    // Kiểm tra nếu tên miền của url là tên miền được cho phép
                    if (domain.equals(DOMAIN)) {
                        //cho phép tải trang web
                        binding.webViewMain.loadUrl(url);
                        actionBar.setTitle("Sự kiện");
                        binding.webViewMain.setVisibility(View.GONE);
                        binding.pageError.setVisibility(View.GONE);
                        binding.loaderView.setVisibility(View.VISIBLE);
                        return false;
                    } else {
                        //không cho phép tải trang web
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        startActivity(intent);
                        return true;
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    actionBar.setTitle(view.getTitle());
                    binding.webViewMain.setVisibility(View.VISIBLE);
                    binding.loaderView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.toolbar_more:
//                Toast.makeText(this, "item click", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(this, toolbar.findViewById(R.id.toolbar_more));
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.po_copy_link) {
                            // Lấy nội dung cần copy
                            String textToCopy = url;

                            // Khởi tạo ClipboardManager từ context
                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                            // Tạo ClipData để chứa nội dung cần copy
                            ClipData clipData = ClipData.newPlainText("label", textToCopy);

                            // Sao chép ClipData vào clipboard
                            clipboardManager.setPrimaryClip(clipData);

                            return true;
                        }
                        if (itemId == R.id.po_open_as_default_browser) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }
                        if (itemId == R.id.po_share) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            String body = "Chia sẻ";
                            String sub = url;
                            intent.putExtra(Intent.EXTRA_TEXT, body);
                            intent.putExtra(Intent.EXTRA_TEXT, sub);
                            startActivity(Intent.createChooser(intent, "share using"));
                        }
                        return false;
                    }
                });

                popupMenu.show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK&&binding.webViewMain.canGoBack()){
//            binding.webViewMain.goBack();
//            return true;
//        }else{
//          return super.onKeyDown(keyCode, event);
//        }
//    }

    @Override
    public void onBackPressed() {
        if (binding.webViewMain.canGoBack()) {
            binding.webViewMain.goBack();
            actionBar.setTitle("Sự kiện");
            binding.webViewMain.setVisibility(View.GONE);
            binding.pageError.setVisibility(View.GONE);
            binding.loaderView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tool_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}