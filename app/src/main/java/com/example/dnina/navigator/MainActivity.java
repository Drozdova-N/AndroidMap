package com.example.dnina.navigator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Long.decode;

public class MainActivity extends AppCompatActivity {

    private GoogleMap map;
    private Button btnGO;
    private static final String MYTAG = "MAIN_ACTIVITY";
    private static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    private EditText destination;
    private EditText origin;
    private LocationUser locationUser;

    private Location currentLocation;
    private String ssss = "59.999451,30.366708";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        origin = findViewById(R.id.origin);
        destination = findViewById(R.id.destination);
//        if (currentLocation != null) origin.setText(getLocationStr(currentLocation));
        destination.setText(ssss);
        btnGO = findViewById(R.id.btn_GO);

        // отрисовка карты
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_View);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                showLocationUser();
                if (currentLocation != null) origin.setText(getLocationStr(currentLocation));
            }
        });


        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin.getText() != null && destination != null) {
                    showRoute();
                }
            }
        });
    }

    private String getLocationStr(Location ll) {
        return "" + ll.getLatitude() + "," + ll.getLongitude();
    }

    private void showRoute() {
        String baseURL = "https://maps.googleapis.com/";

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(baseURL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofitBuilder.build();

        final RouteRequest request = retrofit.create(RouteRequest.class);
        Call<RouteResponse> call =request.getRout(origin.getText().toString(),
                destination.getText().toString(),
                true,
                "ru" ,"transit");




        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                RouteResponse route = response.body();
                List<LatLng> listLL = PolyUtil.decode(response.body().getPoints());
                PolylineOptions polyline = new PolylineOptions();
                polyline.width(4f).color(Color.red(2));
                LatLngBounds.Builder llBounds = new LatLngBounds.Builder();

                for (int i = 1; i < listLL.size() - 1; i++) {

                    polyline.add(listLL.get(i));
                    llBounds.include(listLL.get(i));
                }

                MarkerOptions start = new MarkerOptions().position(listLL.get(1)).title("A");
                MarkerOptions finish = new MarkerOptions().position(listLL.get(listLL.size() - 1)).title("B");
                map.addMarker(start);
                map.addMarker(finish);
                map.addPolyline(polyline);
                int sizeRoute = getResources().getDisplayMetrics().widthPixels;
                LatLngBounds latLngBounds = llBounds.build();
                CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds , sizeRoute , sizeRoute , 25) ;
                map.moveCamera(track);

            }


            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {

            }
        });

    }

    private void showLocationUser() {
        askPermissions();
        locationUser = new LocationUser((LocationManager) getSystemService(LOCATION_SERVICE));
        currentLocation = locationUser.getLocation();

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(360)                // Sets the orientation of the camera to north
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            // Add Marker to Map
            MarkerOptions option = new MarkerOptions();
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.cat2);
            //   option.setIcon(bitmapDescriptor);
            option.title("My Location");
            option.snippet(latLng.toString());
            option.position(latLng);
            Marker currentMarker = map.addMarker(option);
            currentMarker.showInfoWindow();
            currentMarker.setIcon(bitmapDescriptor);

        } else {
            LatLng latLng = new LatLng(60, 100);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }
    }

    private void askPermissions() {

        // With API> = 23, you have to ask the user for permission to view their location.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // The Permissions to ask user.
                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                // Show a dialog asking the user to allow the above permissions.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Show current location on Map.
                    // this.showMyLocation();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}
