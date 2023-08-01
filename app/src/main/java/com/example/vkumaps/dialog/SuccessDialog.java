package com.example.vkumaps.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.example.vkumaps.R;
import com.example.vkumaps.fragment.FeedbackFragment;

public class SuccessDialog {
    private Context context;
    private DialogSuccessListener listener;
    private Dialog dialog;
    public SuccessDialog(Context context, DialogSuccessListener listener){
        this.context=context;
        this.listener=listener;
    }
    public void showDialog(){
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv=dialog.findViewById(R.id.tv_ok);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        dialog.create();
        dialog.show();
    }
    public void close(){
        dialog.dismiss();
        listener.onDismiss();
    }
}
