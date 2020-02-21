package com.example.runningapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class routeDetails extends FragmentActivity implements OnMapReadyCallback {

    private TextView AvgSpeed;
    private TextView Dist;
    private TextView StTime;
    private TextView Duration;
    private TextView StPoint;
    private TextView EndPoint;
    private TextView Steps;
    private dbHandler dbHandler;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routedetails_view);

        // dbHandler = new dbHandler(this);

        AvgSpeed = findViewById(R.id.avgSpeed);
        Dist = findViewById(R.id.dist);
        StTime = findViewById(R.id.strtTime);
        Duration = findViewById(R.id.duration);
        StPoint = findViewById(R.id.strtpoint);
        EndPoint = findViewById(R.id.endpoint);
        Steps = findViewById(R.id.steps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String routeID = getIntent().getStringExtra("routeID");
        // Route route = dbHandler.getRoute(routeID);

        /*
        AvgSpeed.setText(String.valueOf(route.getAvgSpeed()));
        Dist.setText(String.valueOf(route.getDistance()));
        StTime.setText(String.valueOf(route.getStarting_time()));
        StPoint.setText(String.valueOf(route.getStartpoint()));
        EndPoint.setText(String.valueOf(route.getEndpoint()));
        Steps.setText(String.valueOf(route.getSteps()));
        Duration.setText(String.valueOf(route.getDuration()));

         */


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng point = new LatLng(37.98, 23.72);
        LatLng point2 = new LatLng(39.00, 23.72);
        mMap.addMarker(new MarkerOptions().position(point).title("start"));
        mMap.addMarker(new MarkerOptions().position(point2).title("finish"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,12));
    }

}
