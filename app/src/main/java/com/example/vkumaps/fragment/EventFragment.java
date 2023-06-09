package com.example.vkumaps.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.activities.BrowserActivity;
import com.example.vkumaps.adapters.NewsAdapter;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.listener.ItemNewsClickListener;
import com.example.vkumaps.models.News;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment implements ItemNewsClickListener {
    private List<News> listNews;
    private NewsAdapter adapter;
    private final ChangeFragmentListener listener;
    private ConstraintLayout loadView;

    public EventFragment(ChangeFragmentListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        // Inflate the layout for this fragment
        listener.changeTitle("Sự kiện");
        loadView = rootView.findViewById(R.id.loader_view);
        RecyclerView rc = rootView.findViewById(R.id.rc_news);
        rc.setVisibility(View.GONE);
        loadView.setVisibility(View.VISIBLE);
        rc.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rc.setLayoutManager(layoutManager);
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        listNews = new ArrayList<>();
        adapter = new NewsAdapter(listNews, this);
        rc.setAdapter(adapter);

        fireStore.collection("newstable").orderBy("created", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            News newsModel = document.toObject(News.class);
                            listNews.add(newsModel);
                            adapter.notifyDataSetChanged();
                            loadView.setVisibility(View.GONE);
                            rc.setVisibility(View.VISIBLE);
                        }
                    }
                });

        return rootView;
    }

    @Override
    public void onItemClick(News news) {
        Intent intent = new Intent(requireContext(), BrowserActivity.class);
        intent.putExtra("url", news.getUrl());
        intent.putExtra("title", news.getTitle());
        startActivity(intent);
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