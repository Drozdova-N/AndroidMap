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
import android.widget.ImageView;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
    private EditText destination;
    private EditText origin;
    private SlidingUpPanelLayout slidingPanel;
    private ImageView upArrow;
    private ImageView downArrow;


    private static final String MYTAG = "MAIN_ACTIVITY";
    private static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;


    private LocationUser locationUser;
    private Location currentLocation;
    private String ssss = "59.999451,30.366708";
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        origin = findViewById(R.id.origin);
        destination = findViewById(R.id.destination);
        btnGO = findViewById(R.id.btn_GO);
        slidingPanel = findViewById(R.id.sliding_layout);
        upArrow = findViewById(R.id.up_arrow);
        downArrow = findViewById(R.id.down_arrow);

        destination.setText(ssss);

        // отрисовка карты
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_View);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                mapView = new MapView(map);
                showLocationUser();
                if (currentLocation != null) origin.setText(getLocationStr(currentLocation));

            }
        });

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin.getText() != null && destination != null) {
                    showRoute();
                    slidingPanel.collapsePanel();
                }
                
            }
        });


        slidingPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                upArrow.setVisibility(View.VISIBLE);
                downArrow.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPanelCollapsed(View view) {
                downArrow.setVisibility(View.VISIBLE);
                upArrow.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onPanelExpanded(View view) {
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        });
    }

    private String getLocationStr(Location ll) {
        return "" + ll.getLatitude() + "," + ll.getLongitude();
    }

    private void showRoute(String origin , String destination) {

        RouteRequest request = NetworkModule.getRouteRequest();
        Call<RouteResponse> call = request.getRout(origin,
                destination,
                true, "ru");
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                RouteResponse route = response.body();
                List<LatLng> listLL = PolyUtil.decode(route.getPoints());
                mapView.drawRoute(listLL, getResources().getDisplayMetrics().widthPixels);

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
            mapView.putMarker(latLng);
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

                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}
