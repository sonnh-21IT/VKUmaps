package com.example.vkumaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;

public class MyInfoFragment extends Fragment implements View.OnClickListener {
    private final ChangeFragmentListener listener;
    private final MyInfoListener myInfoListener;

    public MyInfoFragment(ChangeFragmentListener listener, MyInfoListener myInfoListener) {
        this.listener = listener;
        this.myInfoListener = myInfoListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listener.changeTitle("Thông tin của tôi");
        View rootView = inflater.inflate(R.layout.fragment_myinfor, container, false);
        rootView.findViewById(R.id.time_table).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.time_table:{
                myInfoListener.onTimeTableClick();
                break;
            }
            case R.id.learn_result:{
                myInfoListener.onLearnResultClick();
                break;
            }
            default:{
                break;
            }
        }
    }

    public interface MyInfoListener {
        void onLearnResultClick();

        void onLogout();

        void onTimeTableClick();
    }
}