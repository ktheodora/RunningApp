package com.example.runningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView infoView;
    private Button login;
    private int attempts = 3;
    String username, password, info;
    dbHandler db;
    private Button back;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        db = new dbHandler(this);

        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
        infoView = (TextView) findViewById(R.id.tvinfo);
        login = (Button) findViewById(R.id.login);
        back = (Button) findViewById(R.id.backbtn);


        //method will  be working when button is clicked
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SINCE NAME IS EDIT TEXT USER WILL  get whatever is entered through getText and
                // then it is converted to String same done for password
                if (TextUtils.isEmpty(Password.getText()) || TextUtils.isEmpty(Name.getText())) {
                    Toast t = Toast.makeText(login.this,
                            "Please provide all fields", Toast.LENGTH_LONG);
                    t.show();
                } else {
                    String password = Password.getText().toString();
                    String username = Name.getText().toString();

                    if (!(db.isUser(username) && checkPwd(username, password))) {
                        //reduce number of attempts
                        checkAttempts();
                    } else {
                        //if username is correct then go to homepage
                        Intent myIntent = new Intent(login.this, homepage.class);
                        Bundle b = new Bundle();
                        b.putString("username", username);
                        myIntent.putExtras(b); //Put your id to your next Intent
                        startActivity(myIntent);
                    }
                }
            }
        });
        //in case we have tried to login and locked the attempts previously
        Bundle b = getIntent().getExtras();
        if (b != null) {
            boolean state = b.getBoolean("state");
            attempts = b.getInt("attempts");
            login.setEnabled(state);
        }
        //reeenable after 5 mins
        if (!login.isEnabled()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    login.setEnabled(true);
                    attempts = 3;
                    Intent myIntent = new Intent(login.this, login.class);
                    startActivity(myIntent);
                }
            }, 1000);
        }

        info = "No of attempts left: " + attempts;
        infoView.setText(info);

        if (!login.isEnabled()) {
            login.setBackgroundColor(Color.RED);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeback();
            }
        });

    }

    public boolean checkPwd(String username, String password) {

        //User user = db.getUser(username);
       // String encrypt = db.md5(password);
        return true ;//(user.getPwd()).equals(encrypt);

    }

    public void checkAttempts() {
        if (attempts > 0) {
            attempts--;
            info = "No of attempts left: " + attempts;
            infoView.setText(info);
            Toast t = Toast.makeText(login.this,
                    "Password and Username don't match", Toast.LENGTH_LONG);
            t.show();
        }
        else {
            login.setEnabled(false);
            Toast t = Toast.makeText(login.this,
                    "Login Locked. Login enabled again in 60s", Toast.LENGTH_LONG);
            t.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    login.setEnabled(false);
                    attempts = 3;
                }
            }, 1000);
        }
    }

    private void routeback()
    {
        Intent intent = new Intent(login.this, login.class); //Second was main_activity.class ------
        Bundle b = new Bundle();
        b.putBoolean("state",login.isEnabled());
        b.putInt("attempts",attempts);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

    public void onBackPressed() {
        Intent intent = new Intent(login.this, login.class); //Second was main_activity.class ------
        Bundle b = new Bundle();
        b.putBoolean("state",login.isEnabled());
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

}