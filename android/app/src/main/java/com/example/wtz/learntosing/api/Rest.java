package com.example.wtz.learntosing.api;

import com.example.wtz.learntosing.BuildConfig;
import com.example.wtz.learntosing.api.music.GetMP3Service;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public final class Rest {
    private static Rest sRestClient;
    private static RestAdapter sRestAdapter;

    private GetMP3Service mGetMP3Service;
    private Rest() {
    }

    public static Rest api() {
        if (sRestClient == null) {
            sRestClient = new Rest();

            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(10, TimeUnit.SECONDS);
            client.setConnectTimeout(10, TimeUnit.SECONDS);

            //Exclude ActiveAndroid Model class,we should call excludeFieldsWithoutExposeAnnotation().
//            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

            RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ?
                    RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
            RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(BuildConfig.BASE_URL)
                    .setLogLevel(logLevel)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                        }
                    }).setConverter(new GsonConverter(gson))
                    .setClient(new OkClient(client));

            sRestAdapter = builder.build();
        }
        return sRestClient;
    }

    public GetMP3Service getMP3Service(){
        if (mGetMP3Service==null){
            mGetMP3Service=sRestAdapter.create(GetMP3Service.class);
        }
        return mGetMP3Service;
    }


}
