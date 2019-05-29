package com.example.dnina.navigator.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.dnina.navigator.util.MyPlace;
import com.example.dnina.navigator.R;
import com.example.dnina.navigator.adapters.AdapterPlace;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListMyPlace extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView isNotData;
    private ArrayList<MyPlace> places = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private AdapterPlace adapterPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_my_plase);
        recyclerView = findViewById(R.id.recycler_view_place);
        isNotData = findViewById(R.id.is_not_data);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(user.getUid()).child("Place");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterPlace = new AdapterPlace(places, this);
        recyclerView.setAdapter(adapterPlace);
        updateUI();

    }


    public void updateUI() {

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                places.add(dataSnapshot.getValue(MyPlace.class));
                adapterPlace.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                MyPlace place = dataSnapshot.getValue(MyPlace.class);
                int index = getIndex(place);
                places.remove(index);
                adapterPlace.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getIndex(MyPlace place) {
        int index = -1;
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getKey().equals(place.getKey()))
                index = i;

        }
        return index;
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, ListMyPlace.class);
        activity.startActivity(intent);

    }
}
