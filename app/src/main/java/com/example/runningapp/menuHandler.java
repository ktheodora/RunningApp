package com.example.runningapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class menuHandler  {
    String usr;
    Context ctx;
    static String USERPREF = "USER"; // or other values
    dbHandler peopleDB;

    public menuHandler(Context Ctx) {
        ctx = Ctx;
        peopleDB = new dbHandler(ctx);
    }
    public menuHandler(Context Ctx, String User) {
        ctx = Ctx;
        peopleDB = new dbHandler(ctx);
        usr = User;
    }


    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menulink_home:
                goToHomePage();
                return true;

            case  R.id.menulink_stats:
                goToStats();
                return true;

            case  R.id.menulink_routes:
                goToRoutes();
                return true;

            case  R.id.menulink_sets:
                goToSettings();
                return true;
            case R.id.menulink_logout:
                logout();
                return true;
            default:
                return false;
        }
    }

    public void goToHomePage() {
        Intent myIntent = new Intent(ctx, homepage.class);
        ctx.startActivity(myIntent);
    }

    public void goToStats() {
        Intent myIntent = new Intent(ctx, statistics.class);
        ctx.startActivity(myIntent);
    }

    public void goToRoutes() {
        Intent myIntent = new Intent(ctx, routeList.class);
        ctx.startActivity(myIntent);
    }

    public void goToSettings() {
        Intent myIntent = new Intent(ctx, settings.class);
        ctx.startActivity(myIntent);
    }

    public void showToast(String text) {
        Toast t = Toast.makeText(ctx,text,Toast.LENGTH_SHORT);
        t.show();
    }

    public void logout() {
        //delete current user editor values
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(USERPREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        //then redirect to initial activity
        showToast("Logging you out.. Bye!");
        Intent myIntent = new Intent(ctx, login.class);
        ctx.startActivity(myIntent);
    }

}