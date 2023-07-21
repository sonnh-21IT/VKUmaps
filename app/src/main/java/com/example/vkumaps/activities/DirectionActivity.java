package com.example.vkumaps.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.adapters.DirectionAdapter;
import com.example.vkumaps.adapters.HistoryAdapter;
import com.example.vkumaps.dialog.WarningDeleteHistoryDialog;
import com.example.vkumaps.fragment.HomeFragment;
import com.example.vkumaps.listener.DialogListener;
import com.example.vkumaps.models.MarkerModel;
import com.example.vkumaps.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class DirectionActivity extends AppCompatActivity implements DialogListener {
    private LinearLayout recommend, history;
    private ImageView back, swap;
    private EditText start, end;
    private RecyclerView rv_recommend, rv_history;
    private DirectionAdapter adapter;
    private List<String> list;
    private FirebaseFirestore firestore;
    private TextView delete;
    private HistoryAdapter historyAdapter;
    private WarningDeleteHistoryDialog dialog;
    private ConstraintLayout directionDialog;

    private TextWatcher editTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Không cần xử lý trước sự thay đổi
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Xử lý khi nội dung của EditText thay đổi
            String input = s.toString().trim();
            if (!input.isEmpty()) {
                // Hiển thị RecyclerView và tải dữ liệu từ Firestore
                history.setVisibility(View.GONE);
                recommend.setVisibility(View.VISIBLE);
                loadMarkersFromFirestore(input);
            } else {
                // Ẩn RecyclerView khi không có nội dung trong EditText
                history.setVisibility(View.VISIBLE);
                recommend.setVisibility(View.GONE);
                list.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Không cần xử lý sau sự thay đổi
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        Paper.init(getApplicationContext());
        initView();

        firestore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
    }

    private void loadMarkersFromFirestore(String input) {
        history.setVisibility(View.GONE);
        recommend.setVisibility(View.VISIBLE);
        firestore.collection("Marker")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        List<String> searchResults = searchPhrases(list, input);
        adapter = new DirectionAdapter(getApplicationContext(), searchResults);
        rv_recommend.setAdapter(adapter);
        adapter.setItemClickListener(new DirectionAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                clickItemList(text);
            }
        });
    }
    private void findDirection(String s_start, String s_end) {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        directionDialog.setVisibility(View.VISIBLE);
        firestore.collection("Marker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String startDir = null;
                    String endDir = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (s_start.equals(document.getId().trim())) {
                            MarkerModel model = document.toObject(MarkerModel.class);
                            startDir = model.getSubname();
                        }
                        if (s_end.equals(document.getId().trim())) {
                            MarkerModel model = document.toObject(MarkerModel.class);
                            endDir = model.getSubname();
                        }
                    }
                    if (startDir != null && endDir != null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("startPoint", startDir);
                        intent.putExtra("endPoint", endDir);
                        startActivity(intent);
                    } else {
                        opeDialog("Địa điểm bạn chọn không tồn tại!");
                    }
                }
            }
        });
    }

    private void opeDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DirectionActivity.this);
        builder.setTitle("Lỗi");
        builder.setMessage(s);
        builder.setIcon(R.drawable.ic_error);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void initView() {
        recommend = findViewById(R.id.recommend);
        history = findViewById(R.id.history);
        start = findViewById(R.id.start);
        start.requestFocus();
        end = findViewById(R.id.end);
        rv_recommend = findViewById(R.id.rv_recommend);
        rv_recommend.setHasFixedSize(true);
        rv_recommend.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_history = findViewById(R.id.rv_history);
        rv_history.setHasFixedSize(true);
        rv_history.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        back = findViewById(R.id.back);
        swap = findViewById(R.id.swap);
        delete = findViewById(R.id.delete);
        directionDialog=findViewById(R.id.direction_dialog);
        directionDialog.setVisibility(View.GONE);
        dialog=new WarningDeleteHistoryDialog(this,this);
      
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = start.getText().toString().trim();
                String s2 = end.getText().toString().trim();
                if (!s1.isEmpty() || !s2.isEmpty()) {
                    start.setText(s2);
                    end.setText(s1);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.listHistory!=null||Utils.listHistory.size()<=0){
                    View immView = getCurrentFocus();
                    if (immView != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    dialog.showDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Lịch sử đang rỗng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (getIntent().getStringExtra("name") != null) {
            end.setText(getIntent().getStringExtra("name"));
        }
        start.addTextChangedListener(editTextWatcher);
        end.addTextChangedListener(editTextWatcher);
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        historyAdapter.setListener(new HistoryAdapter.ItemHistoryListener() {
            @Override
            public void onItemClick(String text) {
                clickItemList(text);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDeleteClick(String text) {
                for (int i = 0; i <= Utils.listHistory.size() - 1; i++) {
                    if (Utils.listHistory.get(i).equals(text)) {
                        Utils.listHistory.remove(i);
                    }
                }
                Paper.book().write("history", Utils.listHistory);
                historyAdapter.setmList(Utils.listHistory);
                rv_history.setAdapter(historyAdapter);
            }
        });
        showHistory();
    }

    private void showHistory() {
        Utils.listHistory = Paper.book().read("history");
        if (Utils.listHistory != null) {
            List<String> temp = new ArrayList<>();
            for (int i = Utils.listHistory.size() - 1; i >= 0; i--) {
                temp.add(Utils.listHistory.get(i));
            }
            historyAdapter.setmList(temp);
            rv_history.setAdapter(historyAdapter);

            history.setVisibility(View.VISIBLE);
            recommend.setVisibility(View.GONE);
        }
    }

    private List<String> searchPhrases(List<String> phraseList, String keyword) {
        List<String> searchResults = new ArrayList<>();
        for (String phrase : phraseList) {
            if (phrase.toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(phrase);
            }
        }
        return searchResults;
    }

    @Override
    public void onClear() {
        Utils.listHistory.clear();
        Paper.book().delete("history");
        historyAdapter.setmList(new ArrayList<>());
        rv_history.setAdapter(historyAdapter);
        dialog.close();
        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
    }
    public void clickItemList(String text){
        if (start.isFocused()) {
            start.setText(text);
            if (!end.getText().toString().equals("")) {
                findDirection(start.getText().toString().trim(), end.getText().toString().trim());
            } else {
                end.requestFocus();
            }
        } else if (end.isFocused()) {
            end.setText(text);
            findDirection(start.getText().toString().trim(), end.getText().toString().trim());
        }
        Utils.listHistory = Paper.book().read("history");
        boolean checkExit = false;
        int n = 0;
        if (Utils.listHistory != null) {
            if (Utils.listHistory.size() > 0) {
                for (int i = 0; i < Utils.listHistory.size() - 1; i++) {
                    if (text.equals(Utils.listHistory.get(i))) {
                        checkExit = true;
                        n = i;
                    }
                }
            }
        } else {
            Utils.listHistory = new ArrayList<>();
        }
        if (checkExit) {
            Utils.listHistory.remove(n);
        }else {
            Utils.listHistory.add(text);
        }
        Paper.book().write("history", Utils.listHistory);

        showHistory();
        recommend.setVisibility(View.GONE);
        history.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        directionDialog.setVisibility(View.GONE);
    }
}