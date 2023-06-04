package com.example.vkumaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.adapters.NewsAdapter;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.viewModel.NewsViewModel;

public class EventFragment extends Fragment {
    private ChangeFragmentListener listener;
    NewsViewModel newsViewModel;
    public EventFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_event, container, false);
        // Inflate the layout for this fragment
        listener.changeTitle("Sự kiện");

        RecyclerView rc=rootView.findViewById(R.id.rc_news);
        rc.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager1=new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        rc.setLayoutManager(layoutManager1);

        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        newsViewModel.newsModelMutableLiveData().observe(getActivity(),newsModel -> {
            if (newsModel.isSuccess()){
                NewsAdapter adapter=new NewsAdapter(newsModel.getResult());
                rc.setAdapter(adapter);
            }
        });
        return rootView;
    }
}