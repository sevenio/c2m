package com.tvisha.click2magic.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tvisha.click2magic.constants.ApiEndPoint;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();


    private static final ApiClient ourInstance = new ApiClient();
    public static ApiClient getInstance() {
        return ourInstance;
    }

    public static Retrofit getClient() {
        return getInstance().getRetrofit();
    }

    private Retrofit retrofit = null;
    private Retrofit getRetrofit() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiEndPoint.BASE_PATH)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return retrofit;
    }
}
