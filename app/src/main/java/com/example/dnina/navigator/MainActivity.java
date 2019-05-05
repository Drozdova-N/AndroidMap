package com.example.dnina.navigator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MapView map;
    private Button btnGO;
    private SlidingUpPanelLayout slidingPanel;
    private ImageView upArrow;
    private ImageView downArrow;
    private ImageView saveRoute;
    private SlidingUpPanelLayout slidingLayoutInfo;
    private TextView distance;
    private TextView durationInTraffic;
    private TextView tvRoute;

    private boolean isSave = false;

    private AutocompleteSupportFragment autocompleteFragmentA;
    private AutocompleteSupportFragment autocompleteFragmentB;
    private String originStr = "";
    private String destinationStr = "";


    private static final String MYTAG = "MAIN_ACTIVITY";
    private static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    private LocationUser locationUser;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadView();

        // отрисовка карты
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_View);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = new MapView(googleMap);
                showLocationUser();
            }
        });

        autocompleteFragmentA.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                originStr = place.getName();
            }

            @Override
            public void onError(@NonNull Status status) {
                originStr = "";

            }
        });
        autocompleteFragmentB.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destinationStr = place.getName();
            }

            @Override
            public void onError(@NonNull Status status) {
                destinationStr = "";
            }
        });


        saveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSave) {
                    saveRoute.setImageResource(R.drawable.starfalse);
                    isSave = false;
                }
                else  {
                    saveRoute.setImageResource(R.drawable.startrue);
                    isSave = true;
                }
            }
        });

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(autocompleteFragmentA.a.getText().length() == 0) && !(autocompleteFragmentB.a.getText().length() == 0)) {
                    showRoute(originStr, destinationStr);
                } else {
                    Toast.makeText(MainActivity.this, "введите адресс", Toast.LENGTH_LONG);
                    MapView.cleareMap();
                    slidingLayoutInfo.collapsePanel();
                    slidingLayoutInfo.hidePanel();
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

    private void loadView() {
        btnGO = findViewById(R.id.btn_GO);
        slidingPanel = findViewById(R.id.sliding_layout);
        upArrow = findViewById(R.id.up_arrow);
        downArrow = findViewById(R.id.down_arrow);
        durationInTraffic = findViewById(R.id.duration_in_traffic);
        distance = findViewById(R.id.distance);
        slidingLayoutInfo = findViewById(R.id.sliding_layout_info);
        slidingLayoutInfo.hidePanel();
        saveRoute = findViewById(R.id.save_route);
        tvRoute = findViewById(R.id.tv_route);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBrKbH7sPHZP81OVGoTPEd9ZaxingYENiw");
        }

        autocompleteFragmentA = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentA);
        autocompleteFragmentB = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentB);

        autocompleteFragmentA.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragmentB.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

    }


    private String getLocationStr(Location ll) {
        return "" + ll.getLatitude() + "," + ll.getLongitude();
    }

    private void showRoute(final String origin, String destination) {


        RouteRequest request = NetworkModule.getRouteRequest();
        Call<RouteResponse> call = request.getRout(origin,
                destination,
                true, "ru");
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.isSuccessful()) {
                    slidingPanel.collapsePanel();
                    RouteResponse route = response.body();
                    List<LatLng> listLL = PolyUtil.decode(route.getPoints());
                    map.drawRoute(listLL, getResources().getDisplayMetrics().widthPixels);
                    distance.setText(route.getDistance());
                    durationInTraffic.setText(route.getTravelTime());
                    tvRoute.setText("" + originStr + " - " + destinationStr);
                    slidingLayoutInfo.showPanel();
                    slidingLayoutInfo.expandPanel();

                } else
                    Toast.makeText(MainActivity.this, "некорректные данные", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "нет соединения:(", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showLocationUser() {
        askPermissions();
        locationUser = new LocationUser((LocationManager) getSystemService(LOCATION_SERVICE));
        currentLocation = locationUser.getLocation();

        if (currentLocation != null) {

            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.putMarker(latLng, true);
        } else {
            LatLng latLng = new LatLng(60, 100);
            map.putMarker(latLng, false);
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
                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
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
                    showLocationUser();

                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_route) {
            ListMyRoute.start(this);
        } else if (id == R.id.nav_place) {
            ListMyPlace.start(this);

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_manage) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
