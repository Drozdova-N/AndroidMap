package com.example.dnina.navigator.network_module;
import com.example.dnina.navigator.network_module.RouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RouteRequest {

    @GET("maps/api/directions/json?key=AIzaSyBrKbH7sPHZP81OVGoTPEd9ZaxingYENiw&departure_time=now")
    Call<RouteResponse> getRout(@Query(value = "origin") String position,
                                @Query(value = "destination") String destination,
                                @Query(value = "alternatives") boolean alternatives,
                                @Query("sensor") boolean sensor,
                                @Query("language") String language);
//                                @Query("mode") String mode);

}
