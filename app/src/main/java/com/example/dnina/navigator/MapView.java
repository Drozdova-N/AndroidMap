package com.example.dnina.navigator;

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

import java.util.List;

public class MapView {

    private GoogleMap map;


    public MapView(GoogleMap googleMap) {
        map = googleMap;
    }

    public GoogleMap getMap() {

        return map;
    }

    public void drawRoute(List<LatLng> listPoint, int size) {
        map.clear();
        PolylineOptions polyline = new PolylineOptions();
        polyline.width(10f).color(0xffab274f);
        LatLngBounds.Builder llBounds = new LatLngBounds.Builder();

        for (int i = 0; i < listPoint.size(); i++) {

            polyline.add(listPoint.get(i));
            llBounds.include(listPoint.get(i));

        }

        MarkerOptions start = new MarkerOptions().position(listPoint.get(0)).title("A");
        MarkerOptions finish = new MarkerOptions().position(listPoint.get(listPoint.size() - 1)).title("B");
        start.snippet(listPoint.get(0).toString());
        finish.snippet(listPoint.get(listPoint.size() - 1).toString());
        map.addMarker(start);
        map.addMarker(finish);
        map.addPolyline(polyline);
        LatLngBounds latLngBounds = llBounds.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        map.moveCamera(track);
    }

    public void putMarker(LatLng ll, boolean myImage) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ll)             // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(360)                // Sets the orientation of the camera to north
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        // Add Marker to Map
        MarkerOptions option = new MarkerOptions();
        option.snippet(ll.toString());
        option.position(ll);
        Marker currentMarker = map.addMarker(option);
        if (myImage) {
            option.title("My Location");
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.cat2);
            currentMarker.setIcon(bitmapDescriptor);
        }
        currentMarker.showInfoWindow();

    }

}
