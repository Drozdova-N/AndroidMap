package com.example.dnina.navigator;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dnina.navigator.activity.ListMyPlace;
import com.example.dnina.navigator.activity.ListMyRoute;
import com.example.dnina.navigator.activity.LoginActivity;
import com.example.dnina.navigator.adapters.AdapterPlace;
import com.example.dnina.navigator.adapters.AdapterRoute;
import com.example.dnina.navigator.database.FirebaseRequest;
import com.example.dnina.navigator.network_module.NetworkModule;
import com.example.dnina.navigator.network_module.RouteRequest;
import com.example.dnina.navigator.network_module.RouteResponse;
import com.example.dnina.navigator.util.MyLatLng;
import com.example.dnina.navigator.util.MyPlace;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.PolyUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FirebaseRequest.CallbackUpdateRoute,
        AdapterRoute.CallbackAddRoute, AdapterPlace.CallbackAddPlace,
        NavigationView.OnNavigationItemSelectedListener {

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


    private AutocompleteSupportFragment autocompleteFragmentA;
    private AutocompleteSupportFragment autocompleteFragmentB;
    private String originStr = "";
    private String destinationStr = "";

    private MyPlace start = new MyPlace();
    private MyPlace finish = new MyPlace();


    private static final String MYTAG = "MAIN_ACTIVITY";
    private static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    private LocationUser locationUser;
    private Location currentLocation;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    static Route myRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        FirebaseRequest firebaseRequest = new FirebaseRequest();
        firebaseRequest.registerCallBack(this);


        AdapterRoute adapterRoute = new AdapterRoute();
        adapterRoute.registerCallBack(this);

        AdapterPlace adapterPlace = new AdapterPlace();
        adapterPlace.registerCallBack(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {
                TextView tvUserEmail = findViewById(R.id.email_user_menu);
                tvUserEmail.setText(user.getEmail());
            }
        });
        loadView();

        // отрисовка карты
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_View);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = new MapView(googleMap);
                showLocationUser();
                addClickMarker();
            }
        });

        autocompleteFragmentA.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                originStr = place.getName();
                start = new MyPlace(place.getName(), MyLatLng.convertLatLngInMyLatLng(place.getLatLng()));

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
                finish = new MyPlace(place.getName(), MyLatLng.convertLatLngInMyLatLng(place.getLatLng()));
            }

            @Override
            public void onError(@NonNull Status status) {
                destinationStr = "";
            }
        });

        saveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myRoute.isSave()) {
                    saveRoute.setImageResource(R.drawable.starfalse);
                    myRoute.setSave(false);
                    FirebaseRequest.removeRoute(myRoute);
                } else {
                    saveRoute.setImageResource(R.drawable.startrue);
                    myRoute.setSave(true);
                    FirebaseRequest.addRoute(myRoute);
                }
            }
        });

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(autocompleteFragmentA.a.getText().length() == 0) && !(autocompleteFragmentB.a.getText().length() == 0)) {
                    showRoute(autocompleteFragmentA.a.getText().toString(), autocompleteFragmentB.a.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "введите адресс", Toast.LENGTH_LONG);
                    MapView.clearMap();
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

    public static void startNewActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
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

        autocompleteFragmentA.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG));


        autocompleteFragmentB.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG));

    }

    private void showRoute(final String origin, String destination) {

        List<LatLng> listLatLng;
        Route route = FirebaseRequest.searchRoute(origin, destination);
        if (route != null) {

            myRoute = route;
            createRouteOnMap(myRoute);

        } else {
            RouteRequest request = NetworkModule.getRouteRequest();
            Call<RouteResponse> call = request.getRout(origin,
                    destination, false,
                    true, "ru");
            call.enqueue(new Callback<RouteResponse>() {
                @Override
                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                    if (response.isSuccessful()) {
                        RouteResponse route = response.body();
                        List<LatLng> listLatLng = PolyUtil.decode(route.getPoints(0));
                        List<MyLatLng> myLatLng = (MyLatLng.convertListLatLngInMyLatLng(listLatLng));
                        myRoute = new Route(myLatLng, route.getDistance(0), route.getTravelTime(0), autocompleteFragmentA.a.getText().toString()
                                , autocompleteFragmentB.a.getText().toString());
                        createRouteOnMap(myRoute);

                    } else
                        Toast.makeText(MainActivity.this, "некорректные данные", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(Call<RouteResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "нет соединения:(", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void createRouteOnMap(final Route route) {
        myRoute = route;
        slidingPanel.collapsePanel();
        autocompleteFragmentA.setText(route.getOrigin());
        autocompleteFragmentB.setText(route.getDestination());
        initialization(route);
        map.drawRoute(MyLatLng.convertListMyLatLngInLatLng(route.getPoints()), getResources().getDisplayMetrics().widthPixels);
        distance.setText(route.getDistance());
        durationInTraffic.setText(route.getDuration());
        tvRoute.setText(autocompleteFragmentA.a.getText().toString() + " - " + autocompleteFragmentB.a.getText().toString());
        if (route.isSave())
            saveRoute.setImageResource(R.drawable.startrue);
        else saveRoute.setImageResource(R.drawable.starfalse);
        slidingLayoutInfo.showPanel();
        slidingLayoutInfo.expandPanel();


    }

    private void initialization(Route route) {
        start = FirebaseRequest.getPlace(route.getPoints().get(0));
        if (start == null) start = new MyPlace(route.getOrigin(), route.getPoints().get(0));
        finish = FirebaseRequest.getPlace(route.getPoints().get(route.getPoints().size() - 1));
        if (finish == null)
            finish = new MyPlace(route.getDestination(), route.getPoints().get(route.getPoints().size() - 1));
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
//            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
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
            ListMyRoute.startNewActivity(this);
        } else if (id == R.id.nav_place) {

            ListMyPlace.start(this);

        } else if (id == R.id.nav_sign_out) {

            mAuth.signOut();
            LoginActivity.startNewActivity(MainActivity.this);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void callBackUpdateRoute(Route r) {
        if (r.equals(myRoute)) {
            createRouteOnMap(r);
        }

    }

    @Override
    public void callBackUpdatePlace(MyPlace p) {
        if (myRoute != null) {
            MapView.updateMarker(p);
            createRouteOnMap(myRoute);
        }
    }
        @Override
        public void callBackAddRoute (Route r){

            createRouteOnMap(r);
        }

        @Override
        public void callBackAddPlace (MyPlace place){
            start = place;
            MapView.addMarker(place);
            autocompleteFragmentA.setText(place.getNamePlace());
            autocompleteFragmentB.setText("");
            MapView.clearMap();
            myRoute = null;
            slidingLayoutInfo.collapsePanel();
            slidingLayoutInfo.hidePanel();

        }

        public void addClickMarker () {
            map.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    initialization(myRoute);
                    if (marker.getTag().equals("A")) {

                        createAlertDialog(start);
                    } else if (marker.getTag().equals("B")) {
                        createAlertDialog(finish);
                    }
                    return true;
                }
            });
        }

        public void createAlertDialog ( final MyPlace place){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            if (FirebaseRequest.searchPlace(place.getLatLng())) {
                builder.setTitle("Удаление");
                builder.setMessage("удалить выбранную точку ? ");
                builder.setCancelable(true);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        place.setSave(false);
                        FirebaseRequest.removePlace(place);
                        MapView.updateMarker(place);
                    }
                });
            } else {
                builder.setTitle("Добавление");
                builder.setMessage("добавить выбранную точку ? ");
                builder.setCancelable(true);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        place.setSave(true);
                        FirebaseRequest.addPlace(place);
                        MapView.updateMarker(place);
                    }
                });

            }

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }





