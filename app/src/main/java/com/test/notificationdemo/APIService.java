package com.test.notificationdemo;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("0001")
    Call<PriceItem> getData(@Query("id") int id);
}
