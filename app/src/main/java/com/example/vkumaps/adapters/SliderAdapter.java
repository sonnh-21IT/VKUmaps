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

    int imagesLogoArray[] = {
            R.drawable.logo,
            R.drawable.ic_notification,
            R.drawable.ic_notification
    };

    int imagesArray[] ={
            R.raw.onboardscreen1,
            R.raw.onboardscreen2,
            R.raw.onboardscreen3
    };

    int headingArray[] ={
            R.string.first_slide,
            R.string.second_slide,
            R.string.third_slide
    };

    int descriptionArray[] ={
            R.string.description_1,
            R.string.description_2,
            R.string.description_2
    };


    @Override
    public int getCount() {
        return headingArray.length;
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

        ImageView imageViewLogo = view.findViewById(R.id.img_logo);
        LottieAnimationView imageView = view.findViewById(R.id.slider_img);
        TextView heading = view.findViewById(R.id.heading);
        TextView description = view.findViewById(R.id.description);

        imageViewLogo.setImageResource(imagesLogoArray[position]);
        imageView.setAnimation(imagesArray[position]);
        heading.setText(headingArray[position]);
        description.setText(descriptionArray[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}

