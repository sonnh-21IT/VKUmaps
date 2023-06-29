package com.example.vkumaps.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vkumaps.databinding.ItemNewsBinding;
import com.example.vkumaps.listener.ItemNewsClickListener;
import com.example.vkumaps.models.News;

import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{
    private final List<News> list;
    private final ItemNewsClickListener listener;
    public NewsAdapter(List<News> list,ItemNewsClickListener listener){
        this.list=list;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewsBinding newsBinding = ItemNewsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(newsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        News news = list.get(position);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = formatter.format(news.getCreated());
        holder.binding.title.setText(news.getTitle());
        holder.binding.date.setText(formattedDate);
        holder.binding.getRoot().setOnClickListener(view -> listener.onItemClick(news));
        Glide.with(holder.itemView).load(list.get(position).getImg()).into(holder.binding.imgNews);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemNewsBinding binding;

        public MyViewHolder(ItemNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

