package com.example.runningapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Button save, back;
    float kilos = 0, targetD = 0,targetW =0, targetM =0;
    String username = "", email = "",  password= "" , language = "English";
    dbHandler peopleDB;
    EditText KilosView, TargetDView, TargetWView, TargetMView, UsernameView, EmailView,  PasswordView ;
    static String USERPREF = "USER"; // or other value
    Spinner LangSpinner;
    User usr;
    ImageButton menuBtn;
    menuHandler MenuHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        KilosView = (EditText) findViewById(R.id.settingskilos_text);
        TargetDView = (EditText) findViewById(R.id.settingstargetd_text);
        TargetWView = (EditText) findViewById(R.id.settingstargetw_text);
        TargetMView = (EditText) findViewById(R.id.settingstargetm_text);

        UsernameView = (EditText) findViewById(R.id.etUsname);
        PasswordView = (EditText) findViewById(R.id.etPassword);
        EmailView = (EditText) findViewById(R.id.entEmail);

        save = (Button) findViewById(R.id.saveBtn);
        back = (Button) findViewById(R.id.backBtn);

        Bundle b = getIntent().getExtras();

        peopleDB = new dbHandler(this);
        usr =peopleDB.getLoggedUser();

        if (b != null) {

            if (b.containsKey("createAccount")) {
                //Show dialog box with app rules
                AlertDialog.Builder bx1 = new AlertDialog.Builder(settings.this);
                bx1.setTitle("Welcome to The Running App!");
                bx1.setMessage("\n-Make sure to enter your daily, weekly & monthly km goal, " +
                        "as well as your current weight.\n" +
                        "\n-To start using the Running App," +
                        "\nnavigate to Menu -> Home.\n"
                         );
                bx1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    }
                });

                AlertDialog alertDialog = bx1.create();
                alertDialog.show();
            }
        }

        LangSpinner = (Spinner) findViewById(R.id.settingslang_text);
        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("French");
        ArrayAdapter<String> datAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,languages );
        datAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LangSpinner.setAdapter(datAdapter);
        LangSpinner.setOnItemSelectedListener(this);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updated(kilos, targetD,targetW, targetM , username, email , password);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuHandler.goToHomePage();
            }
        });

        MenuHandler = new menuHandler(settings.this, username);
        menuBtn  = (ImageButton) findViewById(R.id.menuLines);
        menuBtn  = (ImageButton) findViewById(R.id.menuLines);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                PopupMenu popup = new PopupMenu(settings.this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return MenuHandler.onMenuItemClick(item);
                    }
                });
                popup.inflate(R.menu.menu_main);
                popup.show();
            }
        });

    }

    private void updated(float Kilos, float TargetD, float TargetW,float TargetM, String Username, String Email, String Password)
    {
        boolean b = false;
        //Only if value of view changes we update it, orelse we pass the initial again
        if (!TextUtils.isEmpty(KilosView.getText())) {
            Kilos = Float.parseFloat(KilosView.getText().toString());
            usr.setKg(Kilos);
            b = true;
        }

        if (!TextUtils.isEmpty(TargetDView.getText())) {
            TargetD = Float.parseFloat(TargetDView.getText().toString());
            usr.setKmgoal_daily(TargetD);
            b = true;
        }

        if (!TextUtils.isEmpty(TargetWView.getText())) {
            TargetW = Float.parseFloat(TargetWView.getText().toString());
            usr.setKmgoal_weekly(TargetW);
            b = true;
        }

        if (!TextUtils.isEmpty(TargetMView.getText())) {
            TargetM = Float.parseFloat(TargetMView.getText().toString());
            usr.setKmgoal_monthly(TargetM);
            b = true;
        }

        if (!TextUtils.isEmpty(UsernameView.getText())) {
            Username = UsernameView.getText().toString();
            usr.setUserID(Username);
            b = true;
        }

        if (!TextUtils.isEmpty(PasswordView.getText())) {
            Password = PasswordView.getText().toString();
            usr.setPwd(peopleDB.md5(Password));
            b = true;
        }

        if (!TextUtils.isEmpty(EmailView.getText())) {
            Email = EmailView.getText().toString();
            usr.setEmail(Email);
            b = true;
        }


        if (!b) {
            // if user didn't enter any details but pressed button
            Toast t = Toast.makeText(settings.this,
                    "No values entered!", Toast.LENGTH_LONG);
            t.show();
        }
        else {
            peopleDB.updateUser(usr);
            Toast t = Toast.makeText(settings.this,
                        "Changes Saved", Toast.LENGTH_LONG);
                t.show();
            if(LangSpinner.isSelected()) {
                String spinnerItem = LangSpinner.getSelectedItem().toString();
                SharedPreferences settings = getSharedPreferences("LANG", 0);

                SharedPreferences.Editor editor = settings.edit();
                String lang ;
                if (spinnerItem.contentEquals("English")) {
                    lang = "en";
                    editor.putInt("language", 1);
                }
                else {
                    lang = "fr";
                    editor.putInt("language", 0);
                }
                editor.commit();
                Locale myLocale = new Locale(lang);
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
            }
            MenuHandler.goToSettings();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*if (parent.getId() == R.id.spinner) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
