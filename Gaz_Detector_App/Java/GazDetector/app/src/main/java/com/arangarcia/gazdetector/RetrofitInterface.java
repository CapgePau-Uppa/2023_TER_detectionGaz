package com.arangarcia.gazdetector;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface RetrofitInterface {

    @POST("arangarcia/addAlert")
    Call<sendResult> executeAddAlert(@Body HashMap<String, String> map);

    @POST("arangarcia/getAlert")
    Call<ArrayList<alertPojo>> executeGetAlerts(@Body HashMap<String, String> map);
}
