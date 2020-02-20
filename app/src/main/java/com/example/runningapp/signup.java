package com.example.runningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {

    dbHandler peopleDB;
    private Button finish, loginBtn;
    private EditText UserName;
    private EditText Name;
    private EditText Surname;
    private  EditText EmailAdress;
    private EditText Password;
    SharedPreferences sharedpreferences;

    private Map<String, Double> defThres;

    String username ,name, surname , password , email ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_view);

        routed();


        peopleDB = new dbHandler(this);
        defThres = new HashMap<String, Double>();
        finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if  (validate())
                    addData();
            }
        });

        loginBtn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(signup.this,
                        login.class);
                startActivity(myIntent);

            }
        });
    }

    private void routed(){

        UserName   = (EditText)findViewById(R.id.UsernameField);

        Password  = (EditText)findViewById(R.id.PwField);

        EmailAdress =  (EditText)findViewById(R.id.EmailField);

        Name = (EditText)findViewById(R.id.loginname_text);

        Surname   = (EditText)findViewById(R.id.loginsurname_text);

        finish=(Button)findViewById(R.id.signBtn);
        loginBtn = (Button)findViewById(R.id.loginBtn);
    }

    public void addData(){
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = UserName.getText().toString();
                name = Name.getText().toString();
                password = Password.getText().toString();
                email = EmailAdress.getText().toString();

                float kg = 0, height = 0, kmgoal_weekly = 0, kmgoal_daily = 0, kmgoal_monthly = 0;

                User user1 = new User(username,password,name,email, surname, kg, height,kmgoal_weekly,kmgoal_daily,kmgoal_monthly);

                peopleDB.addUser(user1);

                Toast.makeText(signup.this,"Data Successfully Inserted",Toast.LENGTH_LONG).show();
                //start new activity
                //user goes to the settings activity
                Intent myIntent = new Intent(signup.this,
                        settings.class);
                Bundle b = new Bundle();
                //storing logged in user
                peopleDB.getUser(username);

                //for displaying welcome alert on the screen
                b.putBoolean("createAccount", true);
                myIntent.putExtras(b); //Put your id to your next Intent

                SharedPreferences settings = signup.this.getSharedPreferences("LANG", 0);
                SharedPreferences.Editor editor = settings.edit();
                //store default language
                if (Locale.getDefault().getDisplayLanguage().contentEquals("fr")) {
                    editor.putInt("language", 1);
                }
                else {
                    editor.putInt("language", 0);
                }
                editor.commit();

                startActivity(myIntent);
                finish();

            }
        });
    }

    private boolean validate() {
        Boolean result = false;
        String usnReg = "^[a-zA-Z]+[a-zA-Z0-9_]*$";
        //String emailReg = "^(.+)@(.+)$";
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        String emailReg = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        // email regex is [word(inc. dot, underscore, minus symbols] @ [word] . [word (2-6 chars long)]


        if (TextUtils.isEmpty(UserName.getText()) || TextUtils.isEmpty(Name.getText())
                || TextUtils.isEmpty(Password.getText()) || TextUtils.isEmpty(EmailAdress.getText())
                || TextUtils.isEmpty(Surname.getText()) ) {
            Toast t = Toast.makeText(signup.this,
                    "All fields are required to proceed", Toast.LENGTH_LONG);
            t.show();
        }
        else if(!(UserName.getText().toString().matches(usnReg))) {
            //checking if username has a valid form
            Toast t = Toast.makeText(signup.this,
                    "Username must contain only alphanumeric or underscore characters and begin with a letter", Toast.LENGTH_LONG);
            t.show();
            //to overcome database problems
        }
        else if (!peopleDB.isUser(UserName.getText().toString())) {

            System.out.println("AAAAAAAAAAAA- c mort-----------");

            Toast t = Toast.makeText(signup.this,
                    "Username already taken", Toast.LENGTH_LONG);
            t.show();
        }
        else if (!(VALID_EMAIL_ADDRESS_REGEX .matcher(EmailAdress.getText().toString()).find())) {
            Toast t = Toast.makeText(signup.this,
                    "Not a valid email form", Toast.LENGTH_LONG);
            t.show();
        }
        else if((Password.getText().toString()).length() < 6) {
            Toast t = Toast.makeText(signup.this,
                    "Password must contain at least 6 characters", Toast.LENGTH_LONG);
            t.show();
        }
        else {
            result = true;
        }
        return result;
    }
}