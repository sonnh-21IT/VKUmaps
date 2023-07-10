package com.example.vkumaps.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.vkumaps.R;

public class DirectionActivity extends AppCompatActivity {
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        initView();
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
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });
    }
}