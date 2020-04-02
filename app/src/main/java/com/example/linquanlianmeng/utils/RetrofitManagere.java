package com.example.linquanlianmeng.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManagere {
    private static final RetrofitManagere ourInstance = new RetrofitManagere();
    private final Retrofit mRetrofit;

    public static RetrofitManagere getInstance() {
        return ourInstance;
    }

    private RetrofitManagere() {
        //创建retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constans.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit(){
        return mRetrofit;
    }
}
