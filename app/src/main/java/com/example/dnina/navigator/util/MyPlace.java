package com.example.dnina.navigator.util;

import com.example.dnina.navigator.util.MyLatLng;

public class MyPlace {

    private String namePlace;
    private MyLatLng latLng;
    private String key;
    private boolean isSave = false;

    public MyPlace(String namePlace, MyLatLng latLng) {
        this.namePlace = namePlace;
        this.latLng = latLng;


    }

    public MyPlace() {

    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getNamePlace() {
        return namePlace;
    }


    public MyLatLng getLatLng() {
        return latLng;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNamePlace(String namePlace) {
        this.namePlace = namePlace;
    }

}
