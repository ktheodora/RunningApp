package com.example.runningapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.homepage_view);
        dbHandler db_handler = new dbHandler(this);
        db_handler.setCtx(this);

        System.out.println("TEMP --------------"+db_handler.userTemp);
        //Firebase test
        User us = new User("2","1","Guillaume","toto@gmail.com","BETROM",5,5,5,5,5);
        db_handler.addUser(us);

        db_handler.getUser("2");

        Button showStatsbtn = (Button) findViewById(R.id.showStatsbtn);
        showStatsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(homepage.this, statistics.class);
                startActivity(myIntent);
            }
        });


    }

}