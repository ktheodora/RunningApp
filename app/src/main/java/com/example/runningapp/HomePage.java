package com.example.runningapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        dbHandler db_handler = new dbHandler(this);
        System.out.println("TEMP --------------"+db_handler.userTemp);
        //Firebase test
        User us = new User("2","1","Guillaume","toto@gmail.com","BETROM",5,5,5,5,5);
        db_handler.addUser(us);

        db_handler.getUser("2");

        if(db_handler.flag == 1){
            System.out.println("TEMP --------------"+db_handler.userTemp);
        }

    }
}
