package com.example.vkumaps.activities;

import static com.example.vkumaps.utils.Utils.listHistory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.adapters.DirectionAdapter;
import com.example.vkumaps.adapters.HistoryAdapter;
import com.example.vkumaps.dialog.WarningDeleteHistoryDialog;
import com.example.vkumaps.listener.DialogListener;
import com.example.vkumaps.models.MarkerModel;
import com.example.vkumaps.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class DirectionActivity extends AppCompatActivity implements DialogListener {
    private LinearLayout recommend, history;
    private ImageView back;
    private EditText start, end;
    private RecyclerView rv_recommend, rv_history;
    private DirectionAdapter adapter;
    private List<String> list;
    private FirebaseFirestore firestore;
    private TextView delete;
    private HistoryAdapter historyAdapter;
    private WarningDeleteHistoryDialog dialog;

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
                listHistory = Paper.book().read("history");
                boolean checkExit = false;
                int n = 0;
                if (listHistory != null) {
                    if (listHistory.size() > 0) {
                        for (int i = 0; i < listHistory.size() - 1; i++) {
                            if (text.equals(listHistory.get(i))) {
                                checkExit = true;
                                n = i;
                            }
                        }
                    }
                } else {
                    listHistory = new ArrayList<>();
                }
                if (checkExit) {
                    listHistory.remove(n);
                }
                listHistory.add(text);
                Paper.book().write("history", listHistory);

                showHistory();
                recommend.setVisibility(View.GONE);
                history.setVisibility(View.VISIBLE);
            }
        });
    }

    private void findDirection(String s_start, String s_end) {
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
        delete = findViewById(R.id.delete);
        dialog=new WarningDeleteHistoryDialog(this,this);
        listHistory = Paper.book().read("history");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listHistory!=null){
                    dialog.showDialog();
                }else {
                    Toast.makeText(getApplicationContext(),"Lịch sử đang rỗng",Toast.LENGTH_SHORT).show();
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
                if (end.isFocused()){
                    end.setText(text);
                }else if(start.isFocused()){
                    start.setText(text);
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDeleteClick(String text) {
                for (int i = 0; i <= listHistory.size()-1; i++) {
                    if (listHistory.get(i).equals(text)) {
                        listHistory.remove(i);
                    }
                }
                Paper.book().write("history", listHistory);
                historyAdapter.setmList(listHistory);
                rv_history.setAdapter(historyAdapter);
            }
        });
        showHistory();
    }

    private void showHistory() {
        if (listHistory != null) {
            List<String> temp = new ArrayList<>();
            for (int i = listHistory.size() - 1; i >= 0; i--) {
                temp.add(listHistory.get(i));
            }
            historyAdapter = new HistoryAdapter(temp);
            historyAdapter.setListener(new HistoryAdapter.ItemHistoryListener() {
                @Override
                public void onItemClick(String text) {

                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDeleteClick(String text) {
                    for (int i = 0; i <= listHistory.size() - 1; i++) {
                        if (listHistory.get(i).equals(text)) {
                            listHistory.remove(i);
                        }
                    }
                    historyAdapter.setmList(listHistory);
                    rv_history.setAdapter(historyAdapter);
                }
            });
            rv_history.setAdapter(historyAdapter);

            history.setVisibility(View.VISIBLE);
            recommend.setVisibility(View.GONE);
        } else {
            history.setVisibility(View.GONE);
            recommend.setVisibility(View.VISIBLE);
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
        listHistory.clear();
        Paper.book().delete("history");
        historyAdapter.setmList(new ArrayList<>());
        rv_history.setAdapter(historyAdapter);
        dialog.close();
        Toast.makeText(this,"Đã xóa",Toast.LENGTH_SHORT).show();
    }
}