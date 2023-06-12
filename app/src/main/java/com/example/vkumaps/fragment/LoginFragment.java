package com.example.vkumaps.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.activities.MainActivity;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {
    LinearLayout login_gg;
    View root;
    private GoogleSignInClient client;
    private ActivityResultLauncher<Intent> signInLauncher;
    private static final int RC_SIGN_IN = 1234;
    private ChangeFragmentListener listener;
    public LoginFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listener.changeTitle("Đăng nhập");
        root = inflater.inflate(R.layout.fragment_login, container, false);

        // Xóa thông tin đăng nhập lưu trữ của phiên trước
        GoogleSignIn.getClient(getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestId()
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(getContext(), options);

        login_gg = root.findViewById(R.id.login_gg);
        login_gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = client.getSignInIntent();
                signInLauncher.launch(i);
            }
        });

        signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } else {
                // Xử lý lỗi đăng nhập không thành công
                Toast.makeText(requireContext(), "Đăng nhập không thành công.", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //Lấy email đã được chọn
            String email = account.getEmail();
            //Kiểm tra email
            if (email != null && email.endsWith("@vku.udn.vn")) {
                root.findViewById(R.id.constraintLayout3).setVisibility(View.GONE);
                root.findViewById(R.id.loader_view).setVisibility(View.VISIBLE);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                // Xóa thông tin đăng nhập lưu trữ của phiên trước
                GoogleSignIn.getClient(getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                // Hiển thị dialog thông báo lỗi
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Lỗi đăng nhập");
                builder.setMessage("Đăng nhập chỉ được phép với email @vku.udn.vn");
                builder.setPositiveButton("Đồng ý", null);
                builder.show();
            }
        } catch (ApiException e) {
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}