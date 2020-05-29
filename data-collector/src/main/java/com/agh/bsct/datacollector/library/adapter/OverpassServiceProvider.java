package com.agh.bsct.datacollector.library.adapter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class OverpassServiceProvider {
    private static OverpassService service;

    public static OverpassService get() {
        if (service == null) {
            service = createService();
        }

        return service;
    }

    private static OverpassService createService() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://overpass-api.de")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(OverpassService.class);
    }
}