package com.example.runningapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDB_Handler {

    public FirebaseDatabase database;

    public FirebaseDB_Handler(){
        database = FirebaseDatabase.getInstance();


    }
}
