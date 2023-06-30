package com.example.vkumaps.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.MarkerModel;

import java.util.List;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.MyViewHolder> {
    private final List<String> mList;
    private ChangeFragmentListener listener;

    public NestedAdapter(List<String> mList,ChangeFragmentListener listener) {
        this.mList = mList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_item , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTv.setText(mList.get(position));
        holder.mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onNestedClick(new MarkerModel());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.nestedItemTv);
        }
    }
}
