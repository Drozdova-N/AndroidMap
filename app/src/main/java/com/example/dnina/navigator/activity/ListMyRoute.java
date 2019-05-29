package com.example.dnina.navigator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.dnina.navigator.R;
import com.example.dnina.navigator.Route;
import com.example.dnina.navigator.adapters.AdapterRoute;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;


public class ListMyRoute extends AppCompatActivity {


    private RecyclerView recyclerView;
    private TextView isNotData;

    private List<Route> routes = new ArrayList<>();
    private AdapterRoute adapterRoute;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    public static Route route;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_my_route);
        isNotData = findViewById(R.id.is_not_data);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(user.getUid()).child("Routes");
        recyclerView = findViewById(R.id.recycler_view_route);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterRoute = new AdapterRoute(routes, this);
        recyclerView.setAdapter(adapterRoute);
        updateUi();
        checkList();

    }

    public static void startNewActivity(Activity activity) {
        Intent intent = new Intent(activity, ListMyRoute.class);
        activity.startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateUi() {
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Route route = dataSnapshot.getValue(Route.class);
                route.setKey(dataSnapshot.getKey());
                routes.add(route);
                adapterRoute.notifyDataSetChanged();
                checkList();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Route route = dataSnapshot.getValue(Route.class);
                int index = getIndex(route);
                routes.remove(index);
                adapterRoute.notifyItemRemoved(index);
                checkList();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int getIndex(Route route) {
        for (Route r : routes) {

            if (r.equals(route)) {
                return routes.indexOf(r);
            }

        }
        return -1;
    }

    public void checkList() {
        if (routes.size() == 0) {
            isNotData.setVisibility(View.VISIBLE);

        } else {
            isNotData.setVisibility(View.GONE);

        }
    }


    public void closeListRoute() {
        this.finish();
    }
}

