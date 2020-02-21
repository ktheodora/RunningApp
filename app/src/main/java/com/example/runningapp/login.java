package com.example.runningapp;

import android.content.Intent;
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
    private Button login;
    private dbHandler db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        db = new dbHandler(this);

        Name = (EditText) findViewById(R.id.UsernameField);
        Password = (EditText) findViewById(R.id.PasswordField);
        login = (Button) findViewById(R.id.loginfield);

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

    }

    public boolean checkPwd(String username, String password) {
        db.getUser(username);
        User user = db.getLoggedUser();
        String encrypt = db.md5(password);
        return (user.getPwd()).equals(encrypt);
    }
}
