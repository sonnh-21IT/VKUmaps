package com.example.vkumaps.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.activities.MainActivity;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyInfoFragment extends Fragment implements View.OnClickListener {
    private TextView name, clas, email, stuId, major;
    private LinearLayout logout;
    private final ChangeFragmentListener listener;
    private FirebaseUser user;
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        name = rootView.findViewById(R.id.infor_name);
        email = rootView.findViewById(R.id.infor_email);
        stuId = rootView.findViewById(R.id.infor_studentId);
        clas = rootView.findViewById(R.id.infor_class);
        major = rootView.findViewById(R.id.infor_major);
        logout = rootView.findViewById(R.id.infor_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog thông báo lỗi
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Đăng xuất");
                builder.setMessage("Bạn chắc chắn rằng bạn muốn đăng xuất không?");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện hành động khi người dùng chọn Không
                        dialog.dismiss(); // Đóng hộp thoại
                    }
                });
                builder.show();
            }
        });

        if (user != null){
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }

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