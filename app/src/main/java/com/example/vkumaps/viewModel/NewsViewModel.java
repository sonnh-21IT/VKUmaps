package com.example.vkumaps.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vkumaps.models.NewsModel;
import com.example.vkumaps.repository.NewsRepository;

public class NewsViewModel extends ViewModel {
    private NewsRepository newsRepository;

    public NewsViewModel(){
        newsRepository = new NewsRepository();
    }
    public MutableLiveData<NewsModel> newsModelMutableLiveData(){
        return newsRepository.getNews();
    }
}
