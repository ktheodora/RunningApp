package com.example.runningapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class routeList extends AppCompatActivity {

    ListView routelistview;
    dbHandler dbhandler;
    String usr = "";
    User userr;
    String username;
    SearchView sv;
    private ImageButton menuBtn;
    menuHandler MenuHandler;
    EditText datepickFrom, datepickTo;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date1,date2;
    ArrayList<Route> route_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routelist_view);
        dbhandler = new dbHandler(this);

        userr = dbhandler.getLoggedUser();

        MenuHandler = new menuHandler(routeList.this);
        menuBtn = (ImageButton) findViewById(R.id.menuLines);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(routeList.this, v);
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

        Button back = (Button) findViewById(R.id.baaackBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuHandler.goToHomePage();
            }

        });

        dbhandler = new dbHandler(this);

        //we set the calendar view in the ui
        myCalendar = Calendar.getInstance();

        datepickFrom = (EditText) findViewById(R.id.selectDate11);
        datepickTo = (EditText) findViewById(R.id.selectDate22);
        date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datepickFrom);
            }
        };

        date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datepickTo);
            }

        };

        datepickFrom.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // TODO Auto-generated method stub
                                                DatePickerDialog mDatePicker = new DatePickerDialog(routeList.this, date1, myCalendar
                                                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                                                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                                                mDatePicker.show();
                                            }
                                        }

        );

        datepickTo.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              // TODO Auto-generated method stub
                                              DatePickerDialog mDatePicker = new DatePickerDialog(routeList.this, date2, myCalendar
                                                      .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                                              mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                                              mDatePicker.show();
                                          }
                                      }

        );

        Button go = (Button) findViewById(R.id.showBtn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbhandler = new dbHandler(getApplicationContext());
                route_list = dbhandler.getAllRoutes(userr);

                //now we check the date and filter the list
                String dateFrom = datepickFrom.getText().toString();
                String dateTo = datepickTo.getText().toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                LocalDate dFrom = null, dTo = null;
                String strDateRegEx = "\\d{2}-\\d{2}-\\d{4}";
                //we check if user has selected a date from the calendar
                if (dateFrom.matches(strDateRegEx)) {
                    dFrom = LocalDate.parse(dateFrom, formatter);
                }
                if (dateTo.matches(strDateRegEx)) {
                    dTo = LocalDate.parse(dateTo, formatter);
                }
                for (Route route : route_list) {
                    //LocalDate expDate = LocalDate.parse(route.getStarting_time(), formatter);
                    //if it is before the earliest date the user chooses
                    LocalDate date = route.getStarting_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (dFrom != null && (date.isBefore(dFrom))) {
                        route_list.remove(route);
                    }
                    //if it is after the latest day the user chooses
                    if (dTo != null && (date.isAfter(dTo))) {
                        route_list.remove(route);
                    }
                    //if it is inside the date boundaries, keep it in the list
                }
                //if in the end none of the expenses fit the selected dates or categories
                if (route_list.isEmpty()) {
                    MenuHandler.showToast("No results for the selected criteria");
                }
                //if it's not empty will show results orelse delete current expenses results
                filteredExpenses(route_list);
            }
        });

        routelistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showDeleteDialog(position);
                //MenuHandler.showToast("You clicked an expense of position" + String.valueOf(position));
                return true;
            }
        });

        routelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Route rou = route_list.get(position);
                Intent myIntent = new Intent(routeList.this, routeDetails.class);
                Bundle b = new Bundle();
                b.putString("routeID",rou.getRouteID());
                myIntent.putExtras(b); //Put your id to your next Intent
                startActivity(myIntent);
            }

        });
    }

    private void updateLabel(EditText datepick) {
        String myFormat = "MM-dd-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        datepick.setText(sdf.format(myCalendar.getTime()));
    }

    public void filteredExpenses(final ArrayList<Route> roulist){

        ArrayList<HashMap<String,String>> myMapList = new ArrayList<>();

        for(int i=0; i<roulist.size();i++){
            HashMap<String,String> myMap = new HashMap<>();
            myMap.put("Date",roulist.get(i).getStarting_time().toString());
            myMap.put("Duration",roulist.get(i).getDuration().toString());
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String[] parts = roulist.get(i).getStartpoint().split("|");
            double lat = Double.parseDouble(parts[0]);
            double lot = Double.parseDouble(parts[1]);
            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(lat,lot, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getAdminArea();

            myMap.put("Location",cityName);

            myMapList.add(myMap);
        }

        final ListAdapter adapter = new SimpleAdapter(routeList.this,myMapList,R.layout.row,
                new String[]{"Date","Duration","Location"},
                new int[]{R.id.rowdate_text,R.id.rowdur_text,R.id.rowloc_text});

        routelistview.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void showDeleteDialog(final int pos) {
        AlertDialog deleteAlert = new AlertDialog.Builder(this)
                .setTitle("Remove Route")
                .setMessage("Are you sure you want to delete this route?")
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        //choosing from the current category list
                        Route rou = route_list.get(pos);
                        if(dbhandler.removeRoute(rou)) {
                            MenuHandler.showToast("Succesful removal of route");
                        }
                        else {
                            MenuHandler.showToast("Database issue, unable to remove route");
                        }
                        Intent myIntent = new Intent(routeList.this, routeList.class);
                        Bundle b = new Bundle();
                        b.putString("username",usr);
                        myIntent.putExtras(b); //Put your id to your next Intent
                        startActivity(myIntent);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();

    }
}
