package com.example.vkumaps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.example.vkumaps.R;


public class SliderAdapter extends PagerAdapter {

    Context context;

    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int imagesArray[] ={
            R.raw.onboardscreen1,
            R.raw.onboardscreen2,
            R.raw.onboardscreen
    };

    int introTitle[] ={
            R.string.intro_title_1,
            R.string.intro_title_2,
            R.string.intro_title_3
    };
    int introContent[] ={
            R.string.intro_content_1,
            R.string.intro_content_2,
            R.string.intro_content_3
    };


    @Override
    public int getCount() {
        return introContent.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_sliding_intro,container, false);

        LottieAnimationView imageView = view.findViewById(R.id.slider_img);
        TextView introTitleTv=view.findViewById(R.id.intro_title);
        TextView introContentTv=view.findViewById(R.id.intro_content);

        imageView.setAnimation(imagesArray[position]);
        introContentTv.setText(introContent[position]);
        introTitleTv.setText(introTitle[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}

