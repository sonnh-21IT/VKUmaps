package com.example.vkumaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;

public class FavoriteFragment extends Fragment {
    private ChangeFragmentListener listener;
    public FavoriteFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listener.changeTitle("Favorite");
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }
}