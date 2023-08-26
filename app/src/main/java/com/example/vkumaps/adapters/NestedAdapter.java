package com.example.vkumaps.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.MarkerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.MyViewHolder> {
    private final List<String> mList;
    private ChangeFragmentListener listener;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public NestedAdapter(List<String> mList, ChangeFragmentListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nested, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTv.setText(mList.get(position));
        holder.mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String areaName = holder.mTv.getText().toString().trim();
                firestore.collection("Marker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isSearched = false;
                            MarkerModel markerModel = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                List<String> contains = (List<String>) document.get("contains");
                                if (document.get("contains") != null) {
                                    for (String name : (List<String>) document.get("contains")) {
                                        if (areaName.equals(name)) {
                                            isSearched = true;
                                            markerModel = document.toObject(MarkerModel.class);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (isSearched) {
                                listener.onNestedClick(markerModel, areaName);
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Hiện chưa có thông tin tọa độ của địa điểm này!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.nestedItemTv);
        }
    }
}
