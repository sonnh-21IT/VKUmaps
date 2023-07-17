package com.example.vkumaps.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private List<String> mList;
    private ItemHistoryListener listener;
    public HistoryAdapter(List<String> mList){
        this.mList=mList;
    }
    public void setListener(ItemHistoryListener listener) {
        this.listener = listener;
    }
    public void setmList(List<String> mList){
        this.mList=mList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTv.setText(mList.get(position));
        holder.mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(holder.mTv.getText().toString().trim());
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteClick(holder.mTv.getText().toString().trim());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView mTv;
        private ImageView img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv=itemView.findViewById(R.id.history);
            img=itemView.findViewById(R.id.clear_text);
        }
    }
    public interface ItemHistoryListener {
        void onItemClick(String text);
        void onDeleteClick(String text);
    }
}
