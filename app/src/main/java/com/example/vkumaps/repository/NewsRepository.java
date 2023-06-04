package com.example.vkumaps.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.vkumaps.models.News;
import com.example.vkumaps.models.NewsModel;
import com.example.vkumaps.retrofit.RetrofitInstance;
import com.example.vkumaps.retrofit.VKUMapsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private VKUMapsApi vkuMapsApi;

    public NewsRepository(){
        vkuMapsApi= RetrofitInstance.getRetrofit().create(VKUMapsApi.class);
    }
    public MutableLiveData<NewsModel> getNews(){
        MutableLiveData<NewsModel> data = new MutableLiveData<>();
        vkuMapsApi.getNews().enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                data.setValue(response.body());
                Log.d("VKU",response.body().toString());
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                Log.d("VKU",t.getMessage());
            }
        });
        return data;
    }
}
