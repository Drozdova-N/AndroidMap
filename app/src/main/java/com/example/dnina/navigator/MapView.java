package com.example.dnina.navigator;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.dnina.navigator.database.FirebaseRequest;
import com.example.dnina.navigator.util.MyLatLng;
import com.example.dnina.navigator.util.MyPlace;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapView {

    private static GoogleMap map;
    private static Marker markerStart;
    private static Marker markerFinish;
    private static Marker myLocation;

    private static Marker marker;

    private static BitmapDescriptor bitmapPlaceNotSave;
    private static BitmapDescriptor bitmapPlaceSave;
    private static Polyline polylineFinal;

    public MapView(GoogleMap googleMap) {
        map = googleMap;
        bitmapPlaceNotSave = BitmapDescriptorFactory.fromResource(R.drawable.placeholder_not_save);
        bitmapPlaceSave = BitmapDescriptorFactory.fromResource(R.drawable.placeholder_save);

    }

    public GoogleMap getMap() {

        return map;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void drawRoute(List<LatLng> listPoint, int size) {

        if (polylineFinal != null) polylineFinal.remove();
        if (markerStart != null && markerFinish != null) {
            markerStart.remove();
            markerFinish.remove();
        }
        PolylineOptions polyline = new PolylineOptions();
        polyline.width(12f).color(0xffab274f);
        LatLngBounds.Builder llBounds = new LatLngBounds.Builder();

        for (int i = 0; i < listPoint.size(); i++) {

            polyline.add(listPoint.get(i));
            llBounds.include(listPoint.get(i));

        }
        MarkerOptions start = new MarkerOptions().position(listPoint.get(0)).title("A");
        MarkerOptions finish = new MarkerOptions().position(listPoint.get(listPoint.size() - 1)).title("B");
        markerStart = map.addMarker(start);
        markerFinish = map.addMarker(finish);
        markerStart.setTag("A");
        markerFinish.setTag("B");

        if(FirebaseRequest.searchPlace(MyLatLng.convertLatLngInMyLatLng(listPoint.get(0)))){
            markerStart.setIcon(bitmapPlaceSave);
        } else   markerStart.setIcon(bitmapPlaceNotSave);

        if(FirebaseRequest.searchPlace(MyLatLng.convertLatLngInMyLatLng(listPoint.get(listPoint.size()-1)))){
            markerFinish.setIcon(bitmapPlaceSave);
        } else   markerFinish.setIcon(bitmapPlaceNotSave);


        polylineFinal = map.addPolyline(polyline);
        LatLngBounds latLngBounds = llBounds.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        map.moveCamera(track);


    }


    public static void putMarker(LatLng ll, boolean myImage) {


        if (myLocation != null) {
            myLocation.remove();
        } else {
            cameraPosition(ll);
        }

        // Add Marker to Map
        MarkerOptions option = new MarkerOptions();
        option.snippet(ll.toString());
        option.position(ll);
        myLocation = map.addMarker(option);
        myLocation.setTag("");
        if (myImage) {
            option.title("My Location");
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.cat2);
            myLocation.setIcon(bitmapDescriptor);
        }
        myLocation.showInfoWindow();


    }


    public static void addMarker(MyPlace place)

    {
        if(marker!= null) marker.remove();

        LatLng ll = MyLatLng.convertMyLatLngInLatLng(place.getLatLng());
        BitmapDescriptor bitmapDescriptor;
        cameraPosition(ll);
        MarkerOptions option = new MarkerOptions();
        option.snippet(ll.toString());
        option.position(ll);
        if (place.isSave()) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.placeholder_save);
        } else
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.placeholder_not_save);
        marker = map.addMarker(option);
        marker.setIcon(bitmapDescriptor);
        marker.setTag("A");
    }


    public static void updateMarker(MyPlace place) {
        addMarker(place);
    }


    public static void clearMap() {

        if (polylineFinal != null) polylineFinal.remove();
        if (markerStart != null && markerFinish != null) {
            markerStart.remove();
            markerFinish.remove();
        }
    }

    public static void cameraPosition(LatLng ll) {

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ll)             // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(360)                // Sets the orientation of the camera to north
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static void clearMyLocation() {
        if (myLocation != null)
            myLocation.remove();
    }

    //для теста ////
    public void drawRoute(List<LatLng> listPoint, int size, int color) {


        PolylineOptions polyline = new PolylineOptions();

        polyline.width(8f).color(0xffab274f);
        if (color == 0) polyline.color(0xffab274f);
        if (color == 1) polyline.color(0xFF1B138B);
        if (color == 2) polyline.color(0xFFE6C408);
        if (color == 3) polyline.color(0xFF9D0ED6);

        LatLngBounds.Builder llBounds = new LatLngBounds.Builder();

        for (int i = 0; i < listPoint.size(); i++) {

            polyline.add(listPoint.get(i));
            llBounds.include(listPoint.get(i));
            map.addMarker(new MarkerOptions().position(listPoint.get(i)));
        }

        MarkerOptions start = new MarkerOptions().position(listPoint.get(0)).title("A");
        MarkerOptions finish = new MarkerOptions().position(listPoint.get(listPoint.size() - 1)).title("B");

        markerStart = map.addMarker(start);
        markerFinish = map.addMarker(finish);
        polylineFinal = map.addPolyline(polyline);

        map.addPolyline(polyline);
        LatLngBounds latLngBounds = llBounds.build();
        // LatLngBounds latLngBounds = llBounds.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        map.moveCamera(track);
    }



}
