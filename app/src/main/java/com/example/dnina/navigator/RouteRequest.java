package com.example.dnina.navigator;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RouteRequest {

    @GET("maps/api/directions/json?key=AIzaSyBrKbH7sPHZP81OVGoTPEd9ZaxingYENiw")
    Call<RouteResponse> getRout(@Query(value = "origin") String position,
                                @Query(value = "destination") String destination,
                                @Query("sensor") boolean sensor,
                                @Query("language") String language);
//                                @Query("mode") String mode);

}
