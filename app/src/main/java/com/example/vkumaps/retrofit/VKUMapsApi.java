package com.example.vkumaps.retrofit;

import com.example.vkumaps.models.NewsModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VKUMapsApi {
    @GET("news.php")
    Call<NewsModel> getNews();
}
