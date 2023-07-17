package com.example.vkumaps.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vkumaps.R;
import com.example.vkumaps.activities.DirectionActivity;
import com.example.vkumaps.listener.DialogListener;
import com.example.vkumaps.utils.Utils;

import io.paperdb.Paper;

public class WarningDeleteHistoryDialog {
    private Context context;
    private Dialog dialog;
    private DialogListener listener;
    public WarningDeleteHistoryDialog(Context context, DialogListener listener){
        this.context=context;
        this.listener=listener;
    }
    public void showDialog(){
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView img=dialog.findViewById(R.id.close);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        TextView tvCancel=dialog.findViewById(R.id.cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
        TextView tvClear=dialog.findViewById(R.id.clear);
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClear();
            }
        });

        dialog.setCancelable(false);

        dialog.create();
        dialog.show();
    }
    public void close(){
        dialog.dismiss();
    }
}
