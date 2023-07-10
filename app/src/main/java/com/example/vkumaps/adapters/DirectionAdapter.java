package com.example.vkumaps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;

import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.MyViewHolder> {
    private Context context;
    private List<String> mList;
    private ItemClickListener itemClickListener;

    public DirectionAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.direction_item , parent , false);
        return new DirectionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTv.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.directionItemTv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(mTv.getText().toString().trim());
                    }
                }
            });
        }
    }
    public interface ItemClickListener {
        void onItemClick(String text);
    }
}
