package com.example.runningapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//implements AdapterView.OnItemSelectedListener

public class settings extends AppCompatActivity {
    private Button update, back;
    double Income = 0, Budget = 0,Rent =0,Bills =0,Insurance =0;
    String username, newTreshold ;
    dbHandler peopleDB;
    static String USERPREF = "USER"; // or other value
    Spinner spinner;
    User usr;
    Map<String, Double> cats;
    List<String> categories = new ArrayList<>();
    private ImageButton menuBtn;
    menuHandler MenuHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        peopleDB = new dbHandler(this);

        /*
        Bundle b = getIntent().getExtras();
        //we pass all the arguments cause they need to be passed again on the homepage
        //in order to be displayed
        if (b != null)
            username = b.getString("username");

        usr = peopleDB.getLoggedUser();

        cats = peopleDB.getThresholds(username);
        categories.clear();
        for (Map.Entry<String, Double> entry : cats.entrySet()) {
            categories.add(entry.getKey()+"- max €:" + String.valueOf(entry.getValue()) );
        }

        spinner= (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> datAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categories);
        datAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(datAdapter);
        spinner.setOnItemSelectedListener(this);

        update=(Button)findViewById(R.id.finButton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updated(Income,Budget, Rent,Bills,Insurance);
                updateCats();
            }
        });

        back =(Button)findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuHandler.goToHomePage();
            }
        });
        MenuHandler = new menuHandler(updateDetail.this, username);
        menuBtn  = (ImageButton) findViewById(R.id.menuLines);
        menuBtn  = (ImageButton) findViewById(R.id.menuLines);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                PopupMenu popup = new PopupMenu(updateDetail.this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return MenuHandler.onMenuItemClick(item);
                    }
                });
                popup.inflate(R.menu.drawermenu);
                popup.show();
            }
        });*/

    }
/*
    private void updated(double Income, double budget, double Rent, double Bills, double Insurance)
    {
        boolean b = false;
        EditText IncomeView   = (EditText)findViewById(R.id.entInc);
        //Only if value of view changes we update it, orelse we pass the initial again
        if (!TextUtils.isEmpty(IncomeView.getText())) {
            Income = Double.parseDouble(IncomeView.getText().toString());
            usr.setIncome(Income);
            b = true;
        }

        EditText BudgetView   = (EditText)findViewById(R.id.entBud);
        if (!TextUtils.isEmpty(BudgetView.getText())){
            budget = Double.parseDouble(BudgetView.getText().toString());
            usr.setBudget(budget);
            b = true;
        }

        EditText BillsView   = (EditText)findViewById(R.id.entBills);
        if (!TextUtils.isEmpty(BillsView.getText())){
            Bills = Double.parseDouble(BillsView.getText().toString());
            usr.setBills(Bills);
            b = true;
        }

        EditText RentView  = (EditText)findViewById(R.id.entRen);
        if (!TextUtils.isEmpty(RentView.getText())) {
            Rent = Double.parseDouble(RentView.getText().toString());
            usr.setRent(Rent);
            b = true;
        }

        EditText InsuranceView   = (EditText)findViewById(R.id.entIns);
        if (!TextUtils.isEmpty(InsuranceView.getText())) {
            Insurance = Double.parseDouble(InsuranceView.getText().toString());
            usr.setInsurance(Insurance);
            b = true;
        }

        if (!b) {
            // if user didn't enter any details but pressed button
            Toast t = Toast.makeText(updateDetail.this,
                    "No values entered!", Toast.LENGTH_LONG);
            t.show();
        }
        else if ((usr.getIncome() - usr.getRent() + usr.getBills() + usr.getInsurance()) < usr.getBudget() ) {
            Toast t = Toast.makeText(updateDetail.this,
                    "Budget cannot be more than income - stable expenses. " +
                            "Please decrease budget or increase income", Toast.LENGTH_LONG);
            t.show();
        }
        else {
            int isUpdated = peopleDB.updateUser(usr);
            if (isUpdated == 0) {
                Toast t = Toast.makeText(updateDetail.this,
                        "Problem with Database Update", Toast.LENGTH_LONG);
                t.show();
            }
            else {
                Toast t = Toast.makeText(updateDetail.this,
                        "Changes Saved", Toast.LENGTH_LONG);
                t.show();

            }
        }

    }

    public void updateCats () {
        EditText entCat = (EditText) findViewById(R.id. entCat);
        EditText entThres = (EditText) findViewById(R.id.entThres);
        boolean b = false;
        dbHandler db = new dbHandler(this);
        if (!TextUtils.isEmpty(entCat.getText())) {
            if(TextUtils.isEmpty(entThres.getText())) {
                Toast t = Toast.makeText(updateDetail.this,
                        "Please enter threshold for new category", Toast.LENGTH_LONG);
                t.show();
            }
            else {//both values are given
                b = true;
            }
        }
        if (!TextUtils.isEmpty(entThres.getText())) {
            if(TextUtils.isEmpty(entCat.getText())) {
                Toast t = Toast.makeText(updateDetail.this,
                        "Please enter name for new category", Toast.LENGTH_LONG);
                t.show();
            }
            else {//both values are given
                b = true;
            }
        }

        if (b) {
            String newCat = entCat.getText().toString();
            Double newThres = Double.parseDouble(entThres.getText().toString());
            double sum = 0;
            Map<String, Double> map = peopleDB.getThresholds(username);
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                sum += entry.getValue();
            }
            if((sum + newThres) > usr.getBudget()) {
                Toast t = Toast.makeText(updateDetail.this,
                        "Total of thresholds are more than budget." +
                                "Please increase budget or decrease another threshold" + newCat, Toast.LENGTH_LONG);
                t.show();
            }
            else if (db.categoryExists(username,newCat)) {
                Toast t = Toast.makeText(updateDetail.this,
                        "Category name already exists" , Toast.LENGTH_LONG);
                t.show();
            }else {
                db.addNewCategory(username, newCat, newThres);
                Toast t = Toast.makeText(updateDetail.this,
                        "Succesful addition of category " + newCat, Toast.LENGTH_LONG);
                t.show();
                MenuHandler.goToDetails();
            }
        }

        EditText updatedThres = (EditText) findViewById(R.id.newThres);
        if (!TextUtils.isEmpty(updatedThres.getText())) {
            //only if a threshold is given
            Double upThres = Double.parseDouble(updatedThres.getText().toString());
            String spinnerItem = spinner.getSelectedItem().toString();
            String[] splitter = spinnerItem.split("-");
            String updatedCat = splitter[0];
            double sum = 0;
            Map<String, Double> map = peopleDB.getThresholds(username);
            double oldThres = map.get(updatedCat);
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                sum += entry.getValue();

            }
            if(((sum - oldThres) +upThres) > usr.getBudget()) {
                Toast t = Toast.makeText(updateDetail.this,
                        "Total of thresholds with " + updatedCat +" are more than budget." +
                                "Please increase budget or decrease another threshold" , Toast.LENGTH_LONG);
                t.show();
            }
            else {
                //because we are displaying another name
                db.updateCategory(username, updatedCat, upThres);
                Toast t = Toast.makeText(updateDetail.this,
                        "Successful update of " + updatedCat + " threshold", Toast.LENGTH_LONG);
                t.show();
                MenuHandler.goToDetails();
            }
        }
        db.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*if (parent.getId() == R.id.spinner) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
        }*/



   /* @Override
    public void onNothingSelected(AdapterView<?> parent) {
        EditText newThres = (EditText) findViewById(R.id.newThres);
        newTreshold = newThres.getText().toString();
        if (newTreshold.isEmpty()) {
            Toast.makeText(parent.getContext(), "Please enter threshold amount" , Toast.LENGTH_LONG).show();
        }
    } */
}
