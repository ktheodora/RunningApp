package com.example.runningapp;

import android.graphics.Point;
import android.location.Location;

import java.sql.Time;
import java.util.Date;

public class Route {
    private String routeID, userID;
    private float distance, avgSpeed, steps;
    private Date starting_time;
    private Date duration;
    private String startpoint, endpoint;

    public Route (String RouteID, String UserID, float Distance, float AvgSpeed, Date Starting_time, Date Duration, String Startpoint, String Endpoint, float Steps) {
        this.userID = UserID;
        this.routeID = RouteID;
        this.avgSpeed = AvgSpeed;
        this.distance = Distance;
        this.starting_time = Starting_time;
        this.duration = Duration;
        this.startpoint = Startpoint;
        this.endpoint = Endpoint;
        this.steps = Steps;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public float getSteps() {
        return steps;
    }

    public void setSteps(float steps) {
        this.steps = steps;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Date getStarting_time() {
        return starting_time;
    }

    public void setStarting_time(Date starting_time) {
        this.starting_time = starting_time;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public String getStartpoint() {
        return startpoint;
    }

    public void setStartpoint(String startpoint) {
        this.startpoint = startpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
