package com.example.dnina.navigator;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ListMyRoute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_my_route);
    }

    public  static void start(Activity activity)
    {
        Intent intent = new Intent(activity, ListMyRoute.class);
        activity.startActivity(intent);
    }
}
