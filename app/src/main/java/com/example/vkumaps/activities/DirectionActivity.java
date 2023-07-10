package com.example.vkumaps.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vkumaps.R;
import com.example.vkumaps.adapters.DirectionAdapter;
import com.example.vkumaps.models.MarkerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    private LinearLayout recommend, history;
    private ImageView back;
    private EditText start, end;
    private RecyclerView rv_recommend, rv_history;
    private DirectionAdapter adapter;
    private List<String> list;
    private FirebaseFirestore firestore;

    private TextWatcher editTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Không cần xử lý trước sự thay đổi
        }

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
                    end.requestFocus();
                } else if (end.isFocused()) {
                    end.setText(text);
                    findDirection(start.getText().toString().trim(), end.getText().toString().trim());
                }
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
                    Toast.makeText(DirectionActivity.this, startDir + " " + endDir, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initView() {
        recommend = findViewById(R.id.recommend);
        history = findViewById(R.id.history);
        start = findViewById(R.id.start);
        start.addTextChangedListener(editTextWatcher);
        start.requestFocus();
        end = findViewById(R.id.end);
        end.addTextChangedListener(editTextWatcher);
        rv_recommend = findViewById(R.id.rv_recommend);
        rv_recommend.setHasFixedSize(true);
        rv_recommend.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_history = findViewById(R.id.rv_history);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });
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
}