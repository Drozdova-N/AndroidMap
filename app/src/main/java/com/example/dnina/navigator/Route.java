package com.example.dnina.navigator;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.dnina.navigator.util.MyLatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Route {

    private List<MyLatLng> points;
    private String distance;
    private String duration;
    private String origin;
    private String destination;

    private String key;
    private boolean isSave = false;


    public Route(List<MyLatLng> points, String distance, String duration, String origin, String destination) {
        this.points = points;
        this.distance = distance;
        this.duration = duration;
        this.origin = origin;
        this.destination = destination;


    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Route() {
        points = new ArrayList<>();
    }

    public void setPoints(List<MyLatLng> points) {
        this.points = points;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public List<MyLatLng> getPoints() {
        return points;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getRout() {
        return this.origin + " - " + this.destination;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(origin, route.origin) &&
                Objects.equals(destination, route.destination);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(origin, destination);
    }
}
