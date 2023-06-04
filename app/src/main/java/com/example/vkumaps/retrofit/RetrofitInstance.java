package com.example.vkumaps.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;
    private static final String domain="192.168.111.141";
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("http://"+domain+"/vkunews/").addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
