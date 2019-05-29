package com.example.dnina.navigator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.dnina.navigator.network_module.NetworkModule;
import com.example.dnina.navigator.network_module.RouteRequest;
import com.example.dnina.navigator.network_module.RouteResponse;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Test extends AppCompatActivity {


    private GoogleMap map;
    private MapView mapView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                mapView = new MapView(map);
            }
        });

        RouteRequest request = NetworkModule.getRouteRequest();
        final Call<RouteResponse> responseCall = request.getRout("Институтский пр-кт д.9", "площадь Востания",
                true,
                true, "ru");
        responseCall.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {

                RouteResponse routeResponse = response.body();
                for (int i = 0; i < routeResponse.size(); i++) {
                    List<LatLng> listLL = PolyUtil.decode(routeResponse.getPoints(i));
                    mapView.drawRoute(listLL, getResources().getDisplayMetrics().widthPixels, i);
                    System.out.println();
                    System.out.println("Маршрут № " + i + 1);
//                    System.out.println(listLL.toString());
                    for (int k = 0; k < listLL.size() / 8; k++) {
                        System.out.println(listLL.get(i).toString());
                    }
                    System.out.println("Количество точек : " + listLL.size());
                    System.out.println("Время в пути : " + routeResponse.getTravelTime(i)
                            + " расстояние : " + routeResponse.getDistance(i));

                }

            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {

            }
        });
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, Test.class);
        activity.startActivity(intent);
    }


}
