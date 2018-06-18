package com.richydave.quotes.network;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.richydave.quotes.Constant.ACCEPT;
import static com.richydave.quotes.Constant.BASE_URL;
import static com.richydave.quotes.Constant.CONTENT_TYPE;
import static com.richydave.quotes.Constant.JSON_APPLICATION;
import static com.richydave.quotes.Constant.REQUEST_TIMEOUT;


public class Api {

    private static Retrofit retrofit = null;

    private static OkHttpClient okHttpClient;

    private static Api instance;

    private WebService webService;

    public static Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    public Api() {

        retrofit = buildRetrofit();
    }

    public WebService getWebService() {
        if (webService == null) {
            webService = retrofit.create(WebService.class);
        }
        return webService;
    }

    private Retrofit buildRetrofit() {
        if (okHttpClient == null) {
            initOkHttpClient();
        }

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void initOkHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder()
                        .addHeader(ACCEPT, JSON_APPLICATION)
                        .addHeader(CONTENT_TYPE, JSON_APPLICATION);
                request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        okHttpClient = httpClient.build();
    }

}