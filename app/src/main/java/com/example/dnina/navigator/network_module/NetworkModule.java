package com.example.dnina.navigator.network_module;

import com.example.dnina.navigator.network_module.RouteRequest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkModule {

    private static String baseURL = "https://maps.googleapis.com/";

    private  static  OkHttpClient.Builder httpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS);

    private  static  Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(baseURL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create());
    private  static  Retrofit retrofit = retrofitBuilder.build();

    private static RouteRequest request;

    public static RouteRequest getRouteRequest() {
        return retrofit.create(RouteRequest.class);
    }


}
