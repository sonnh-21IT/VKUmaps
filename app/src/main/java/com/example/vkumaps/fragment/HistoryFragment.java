package com.example.vkumaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;

public class HistoryFragment extends Fragment {
    private ChangeFragmentListener listener;
    public HistoryFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listener.changeTitle("History");
        return inflater.inflate(R.layout.fragment_history, container, false);
    }
}