package com.example.vkumaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.adapters.NewsAdapter;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {
    private FirebaseFirestore firestore;
    private List<News> listNews;
    private NewsAdapter adapter;
    private ChangeFragmentListener listener;
    public EventFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_event, container, false);
        // Inflate the layout for this fragment
        listener.changeTitle("Sự kiện");

        RecyclerView rc = rootView.findViewById(R.id.rc_news);
        rc.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        rc.setLayoutManager(layoutManager);
        firestore = FirebaseFirestore.getInstance();
        listNews = new ArrayList<>();
        adapter=new NewsAdapter(listNews);
        rc.setAdapter(adapter);

        firestore.collection("newstable").orderBy("id", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                News newsModel = document.toObject(News.class);
                                listNews.add(newsModel);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return rootView;
    }
}