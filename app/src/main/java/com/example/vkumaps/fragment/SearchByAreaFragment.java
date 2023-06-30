package com.example.vkumaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.adapters.SearchByAreaAdapter;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.DataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchByAreaFragment extends Fragment {
    private ChangeFragmentListener listener;
    private RecyclerView recyclerView;
    private ConstraintLayout loadView;
    private List<DataModel> mList;
    private SearchByAreaAdapter adapter;
    private FirebaseFirestore firestore;
    public SearchByAreaFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search_by_area, container, false);
        listener.changeTitle("Tra cứu khu vực");
        loadView = view.findViewById(R.id.loader_view_search);
        loadView.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.rc_main);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Area")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId();
                                String iconUrl = document.get("icon").toString();
                                firestore.collection("Area").document(name).collection("contains").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<String> nestedList1 = new ArrayList<>();
                                            for (QueryDocumentSnapshot nestedDocument : task.getResult()) {
                                                nestedList1.add(nestedDocument.getId());
                                            }

                                            // Thêm DataModel vào danh sách mList
                                            mList.add(new DataModel(nestedList1, name, iconUrl));
                                            Collections.sort(mList);
                                            if (!mList.isEmpty()) {
                                                // Cập nhật adapter và hiển thị danh sách
                                                adapter = new SearchByAreaAdapter(mList,listener);
                                                recyclerView.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                                loadView.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                            } else {
                                                // Hiển thị thông báo hoặc thực hiện hành động phù hợp khi danh sách rỗng
                                                Toast.makeText(getContext(), "Danh sách trống", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}