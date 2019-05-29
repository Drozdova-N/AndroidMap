package com.example.dnina.navigator.database;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.example.dnina.navigator.util.MyLatLng;
import com.example.dnina.navigator.util.MyPlace;
import com.example.dnina.navigator.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseRequest {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser user;
    private static FirebaseDatabase database;
    private static DatabaseReference reference;
    private static CallbackUpdateRoute callbackUpdateRoute;


    private static ArrayList<Route> selectAllRoute = new ArrayList<>();
    private static ArrayList<MyPlace> selectAllPlace = new ArrayList<>();


    public FirebaseRequest() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        selectAllRoute();
        selectAllPlace();


    }

    public interface CallbackUpdateRoute {
        void callBackUpdateRoute(Route r);

        void callBackUpdatePlace(MyPlace p);
    }


    public void registerCallBack(CallbackUpdateRoute callback) {
        this.callbackUpdateRoute = callback;
    }

    public static void addRoute(Route route) {

        route.setKey(reference.child(user.getUid()).child("Routes").push().getKey());
        reference.child(user.getUid()).child("Routes").child(route.getKey()).setValue(route);

    }


    public static void removeRoute(Route route) {
        reference.child(user.getUid()).child("Routes").child(route.getKey()).removeValue();
        route.setSave(false);
        callbackUpdateRoute.callBackUpdateRoute(route);

    }

    public static Route searchRoute(final String origin, final String destination) {

        for (Route route : selectAllRoute) {

            if (route.getOrigin().equals(origin) && route.getDestination().equals(destination)) {
                return route;
            }
        }
        return null;
    }

    private static void selectAllRoute() {
        selectAllRoute.clear();
        reference.child(user.getUid()).child("Routes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                selectAllRoute.add(dataSnapshot.getValue(Route.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void addPlace(MyPlace place) {
        place.setKey(reference.child(user.getUid()).child("Place").push().getKey());
        reference.child(user.getUid()).child("Place").child(place.getKey()).setValue(place);
        callbackUpdateRoute.callBackUpdatePlace(place);

    }

    public static void removePlace(MyPlace place) {
        reference.child(user.getUid()).child("Place").child(place.getKey()).removeValue();
        selectAllPlace.remove(place);
        place.setSave(false);
        callbackUpdateRoute.callBackUpdatePlace(place);


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean searchPlace(MyLatLng latLng) {
        for (MyPlace place : selectAllPlace) {

            if (place.getLatLng().equals(latLng)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static MyPlace getPlace(MyLatLng latLng) {
        for (MyPlace place : selectAllPlace) {

            if (place.getLatLng().equals(latLng)) {
                return place;
            }
        }
        return null;
    }

    private static void selectAllPlace() {
        selectAllPlace.clear();
        reference.child(user.getUid()).child("Place").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                selectAllPlace.add(dataSnapshot.getValue(MyPlace.class));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                MyPlace place = dataSnapshot.getValue(MyPlace.class);
//                int index = getIndex(place);
//                selectAllPlace.remove(index);


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private static int getIndex(MyPlace place) {
        int index = -1;
        for (int i = 0; i < selectAllPlace.size(); i++) {
            if (selectAllPlace.get(i).getKey().equals(place.getKey()))
                index = i;

        }
        return index;
    }
}


