package com.example.dnina.navigator.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyLatLng {

    private Double latitude;
    private Double longitude;

    public MyLatLng() {

    }

    public MyLatLng(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static ArrayList<MyLatLng> convertListLatLngInMyLatLng(List<LatLng> listLatLng) {
        ArrayList<MyLatLng> myLatLngList = new ArrayList<>();

        for (LatLng ll : listLatLng) {

            myLatLngList.add(new MyLatLng(ll.latitude, ll.longitude));
        }

        return myLatLngList;
    }

    public static ArrayList<LatLng> convertListMyLatLngInLatLng(List<MyLatLng> listLatLng) {
        ArrayList<LatLng> latLngList = new ArrayList<>();

        for (MyLatLng ll : listLatLng) {

            latLngList.add(new LatLng(ll.latitude, ll.longitude));
        }

        return latLngList;
    }

    public static LatLng convertMyLatLngInLatLng(MyLatLng myLatLng) {
        return new LatLng(myLatLng.latitude, myLatLng.longitude);
    }

    public static MyLatLng convertLatLngInMyLatLng(LatLng latLng) {
        return new MyLatLng(latLng.latitude, latLng.longitude);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyLatLng myLatLng = (MyLatLng) o;
        return Objects.equals(latitude, myLatLng.latitude) &&
                Objects.equals(longitude, myLatLng.longitude);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(latitude, longitude);
    }
}
