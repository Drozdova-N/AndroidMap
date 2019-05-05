package com.example.dnina.navigator;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import static android.support.v4.content.ContextCompat.getSystemService;

public class LocationUser implements LocationListener {

    private String MYTAG = "LocationUser";
    private  LocationManager locationManager;

    private  static Location locationUser ;
    public  LocationUser(LocationManager manager)
    {
        this.locationManager = manager;
    }
    public Location getLocation() {

        String locationProvider = getEnabledLocationProvider();

        if (locationProvider == null) {
            return null;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

       // Location locationUser = null;
        try {
            // This code need permissions (Asked above ***)
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            locationUser = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // With Android API >= 23, need to catch SecurityException.
        catch (SecurityException e) {
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return locationUser;
    }

    private String getEnabledLocationProvider() {

        // Criteria to find location provider.
        Criteria criteria = new Criteria();

        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider == null) return null;
        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
       MapView.putMarker(latLng, true);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
