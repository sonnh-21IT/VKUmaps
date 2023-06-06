package com.example.vkumaps.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vkumaps.databinding.ItemNewsBinding;
import com.example.vkumaps.models.News;

import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{
    private List<News> list;
    public NewsAdapter(List<News> list){
        this.list=list;
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
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        String formattedDate = formatter.format(news.getCreated());
        holder.binding.title.setText(news.getTitle());
        holder.binding.date.setText(news.getCreated());
        Glide.with(holder.itemView).load(list.get(position).getImg()).into(holder.binding.imgNews);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemNewsBinding binding;

        public MyViewHolder(ItemNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

