package com.example.vkumaps.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.DataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SearchByAreaAdapter extends RecyclerView.Adapter<SearchByAreaAdapter.MyViewHolder> {
    private final List<DataModel> mList;
    private List<String> list;
    private ChangeFragmentListener listener;

    public SearchByAreaAdapter(List<DataModel> mList, ChangeFragmentListener listener) {
        this.mList = mList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_each_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataModel model = mList.get(position);
        holder.textView.setText(model.getItemText());

        list = mList.get(position).getNestedList();

        boolean isExpandable = model.isExpandable();

        holder.expanderLinearLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        Glide.with(holder.itemView.getContext()).load(model.getIconUrl()).into(holder.icon);
        if (isExpandable) {
            holder.img.startAnimation(animation(-90));
            holder.img.setRotation(180f);
        } else {
            holder.img.startAnimation(animation(90));
            holder.img.setRotation(90f);
        }
        holder.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.nestedRecyclerView.setHasFixedSize(true);
        NestedAdapter adapter = new NestedAdapter(list,listener);
        holder.nestedRecyclerView.setAdapter(adapter);
        holder.linearLayout.setOnClickListener(v -> {
            list = model.getNestedList();
            notifyItemChanged(holder.getAdapterPosition());
            model.setExpandable(!model.isExpandable());
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linearLayout;
        private final LinearLayout expanderLinearLayout;
        private final TextView textView;
        private ImageView icon;
        private final ImageView img;
        private final RecyclerView nestedRecyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            expanderLinearLayout = itemView.findViewById(R.id.expandable_layout);
            textView = itemView.findViewById(R.id.tv);
            img = itemView.findViewById(R.id.img);
            icon = itemView.findViewById(R.id.item_img);
            nestedRecyclerView = itemView.findViewById(R.id.child_rv);
        }
    }

    private RotateAnimation animation(float from) {
        RotateAnimation animation = new RotateAnimation(from, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(400);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        return animation;
    }
}
