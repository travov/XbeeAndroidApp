package com.example.xbeeandroidapp.api;

import com.example.xbeeandroidapp.model.ResponseObject;
import com.example.xbeeandroidapp.model.XbeeDevice;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface XbeeWebApi {

    @GET("devices")
    Call<List<XbeeDevice>> getDevices(@Query("active") Boolean active);

    @POST("discovery")
    Call<Void> startDiscoveryProcess(@Query("timeout") Integer timeout);

    @PUT("sampling")
    Call<Void> setSampling(@Query("mac") String XBee64BitAddress, @Query("rate") String rate);

    @GET("changeDetection")
    Call<ResponseObject<String, Set<Integer>>> getChangeDetection(@Query("mac") String XBee64BitAddress);

    @PUT("changeDetection")
    Call<Void> setChangeDetection(@Query("mac") String XBee64BitAddress, @Body Set<Integer> lines);

    @PUT("wr")
    Call<ResponseObject> write(@Query("mac") String XBee64BitAddress);

    @PUT(".")
    Call<Void> setNodeIdentifier(@Query("mac") String XBee64BitAddress, @Query("newId") String newId);

    @GET("dio")
    Call<ResponseObject> getDioValue(@Query("mac") String XBee64BitAddress, @Query("index") int index);

    @GET("param")
    Call<ResponseObject> getParameter(@Query("mac") String XBee64BitAddress, @Query("at") String param);

    @PUT("param")
    Call<Void> setParameter(@Query("mac") String XBee64BitAddress, @Query("at") String param, @Query("value") String value);

    @GET("reach")
    Call<Void> checkIfReachable();

}
