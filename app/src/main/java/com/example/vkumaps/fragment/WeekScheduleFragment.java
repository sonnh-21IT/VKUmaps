package com.example.vkumaps.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WeekScheduleFragment extends Fragment {
    private final ChangeFragmentListener listener;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public WeekScheduleFragment(ChangeFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_week_schedule, container, false);
        listener.changeTitle("Lịch học tuần này");

        if (user != null) {
            // Người dùng đã đăng nhập
            showPersonalSchedule();
        } else {
            // Người dùng chưa đăng nhập
            // Hiển thị lịch mặc định và thông báo lỗi
            showDefaultSchedule();
        }

        return rootView;
    }

    private void showPersonalSchedule() {
    }

    private void showDefaultSchedule() {
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