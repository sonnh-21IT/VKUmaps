package com.example.vkumaps.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.activities.MainActivity;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class FeedbackFragment extends Fragment {
    private ChangeFragmentListener listener;
    private float userRate = 0;
    public FeedbackFragment(ChangeFragmentListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_feedback, container, false);
        // Inflate the layout for this fragment
        listener.changeTitle("Đánh giá / Góp ý");
        final AppCompatButton rateNowBtn = rootView.findViewById(R.id.rateNowBtn);
        final AppCompatButton rateLaterBtn = rootView.findViewById(R.id.rateLaterBtn);
        final RatingBar ratingBar = rootView.findViewById(R.id.ratingBar);
        final ImageView ratingImage = rootView.findViewById(R.id.ratingImage);
        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save database...

                //success notifications

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rateLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finishAndRemoveTask();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                if (rating <= 1) {
                    ratingImage.setImageResource(R.drawable.one_star);
                }
                else if (rating <= 2) {
                    ratingImage.setImageResource(R.drawable.two_star);
                }
                else if (rating <= 3) {
                    ratingImage.setImageResource(R.drawable.three_star);
                }
                else if (rating <= 4) {
                    ratingImage.setImageResource(R.drawable.four_star);
                }
                else if (rating <= 5){
                    ratingImage.setImageResource(R.drawable.five_star);
                }

                //animate emoji image
                animateImage(ratingImage);

                //selected rating by user
                userRate = rating;
            }
        });
        return rootView;
    }
    private void animateImage(ImageView ratingImage) {
        ScaleAnimation scaleAnimation= new ScaleAnimation(0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }
}