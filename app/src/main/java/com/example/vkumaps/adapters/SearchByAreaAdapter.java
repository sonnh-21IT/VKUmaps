package com.example.vkumaps.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkumaps.R;
import com.example.vkumaps.models.DataModel;

import java.util.List;

public class SearchByAreaAdapter extends RecyclerView.Adapter<SearchByAreaAdapter.MyViewHolder> {
    private List<DataModel> mList;
    private List<String> list;

    public SearchByAreaAdapter(List<DataModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_each_item , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataModel model = mList.get(position);
        holder.textView.setText(model.getItemText());

        list=mList.get(position).getNestedList();

        boolean isExpandable = model.isExpandable();
        holder.expanderLinearLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        if (isExpandable){
            holder.img.setRotation(180f);
        }else{
            holder.img.setRotation(90f);
        }
        holder.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.nestedRecyclerView.setHasFixedSize(true);
        NestedAdapter adapter = new NestedAdapter(list);
        holder.nestedRecyclerView.setAdapter(adapter);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = model.getNestedList();
                notifyItemChanged(holder.getAdapterPosition());
                model.setExpandable(!model.isExpandable());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout linearLayout,expanderLinearLayout;
        private TextView textView;
        private ImageView icon,img;
        private RecyclerView nestedRecyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.linear_layout);
            expanderLinearLayout=itemView.findViewById(R.id.expandable_layout);
            textView=itemView.findViewById(R.id.tv);
            img=itemView.findViewById(R.id.img);
            nestedRecyclerView=itemView.findViewById(R.id.child_rv);
        }
    }
}
