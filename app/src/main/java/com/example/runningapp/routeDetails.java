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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routedetails_view);

        AvgSpeed = findViewById(R.id.avgSpeed);
        Dist = findViewById(R.id.dist);
        StTime = findViewById(R.id.strtTime);
        Duration = findViewById(R.id.duration);
        StPoint = findViewById(R.id.strtpoint);
        EndPoint = findViewById(R.id.endpoint);
        Steps = findViewById(R.id.steps);



    }



}
