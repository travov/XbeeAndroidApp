package com.example.xbeeandroidapp.controller;
import com.example.xbeeandroidapp.api.XbeeWebApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller {

    private static String BASE_URL;
    private static volatile XbeeWebApi api;

    private Controller() {
    }

    public static XbeeWebApi getApi(){
        if (BASE_URL != null) {
            if (api == null) {
                synchronized (Controller.class) {
                    if (api == null) {
                        api = getRetrofitObj();
                    }
                }
            }
            return api;
        }
        else throw new IllegalArgumentException("Base url is null!");
    }

    public synchronized static void updateBaseUrl(String url) {
        BASE_URL = url;
        api = getRetrofitObj();
    }

    private static XbeeWebApi getRetrofitObj() {
        Gson gson = new GsonBuilder().setLenient().create();
        XbeeWebApi api;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        api = retrofit.create(XbeeWebApi.class);
        return api;
    }

}
