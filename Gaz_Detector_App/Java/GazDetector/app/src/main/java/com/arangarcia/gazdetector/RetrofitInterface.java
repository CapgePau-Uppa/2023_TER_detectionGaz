package com.arangarcia.gazdetector;

import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface RetrofitInterface {

    @POST("arangarcia/addAlert")
    Call<sendResult> executeAddAlert(@Body HashMap<String, String> map);

    @POST("arangarcia/getAlert")
    Call<receiveResult> executeGetAlerts(@Body HashMap<String, String> map);
}
