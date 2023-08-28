package com.example.vkumaps.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.example.vkumaps.R;

public class FinishDialog {
    private Context context;
    private DialogFinishListener listener;
    private Dialog dialog;

    public FinishDialog(Context context, DialogFinishListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_finish_dir);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv = dialog.findViewById(R.id.tv_finish);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel_dir);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFinish();
            }
        });
        dialog.create();
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }

    public interface DialogFinishListener {
        void onFinish();
    }
}
