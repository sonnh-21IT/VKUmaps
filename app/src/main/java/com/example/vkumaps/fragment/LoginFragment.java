package com.example.vkumaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;

public class LoginFragment extends Fragment {
    private ChangeFragmentListener listener;
    public LoginFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listener.changeTitle("Đăng nhập");
        return inflater.inflate(R.layout.fragment_login, container, false);
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