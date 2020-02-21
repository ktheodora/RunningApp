package com.example.runningapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class routeDetails extends AppCompatActivity {

    private TextView AvgSpeed;
    private TextView Dist;
    private TextView StTime;
    private TextView Duration;
    private TextView StPoint;
    private TextView EndPoint;
    private TextView Steps;
    private dbHandler dbHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routedetails_view);

        dbHandler = new dbHandler(this);

        AvgSpeed = findViewById(R.id.avgSpeed);
        Dist = findViewById(R.id.dist);
        StTime = findViewById(R.id.strtTime);
        Duration = findViewById(R.id.duration);
        StPoint = findViewById(R.id.strtpoint);
        EndPoint = findViewById(R.id.endpoint);
        Steps = findViewById(R.id.steps);

        String routeID = getIntent().getStringExtra("routeID");
        Route route = dbHandler.getRoute(routeID);

        AvgSpeed.setText(String.valueOf(route.getAvgSpeed()));
        Dist.setText(String.valueOf(route.getDistance()));
        StTime.setText(String.valueOf(route.getStarting_time()));
        StPoint.setText(String.valueOf(route.getStartpoint()));
        EndPoint.setText(String.valueOf(route.getEndpoint()));
        Steps.setText(String.valueOf(route.getSteps()));
        Duration.setText(String.valueOf(route.getDuration()));


    }



}
