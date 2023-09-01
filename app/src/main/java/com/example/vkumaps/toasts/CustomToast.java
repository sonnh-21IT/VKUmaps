package com.example.vkumaps.toasts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vkumaps.R;

public class CustomToast extends Toast {
    public CustomToast(Context context) {
        super(context);
    }

    public static CustomToast makeText(Context context, CharSequence text, int duration) {
        CustomToast customToast = new CustomToast(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_toast, null);

        TextView textView = layout.findViewById(R.id.toast_content);
        textView.setText(text);

        customToast.setView(layout);
        customToast.setDuration(duration);

        return customToast;
    }
}