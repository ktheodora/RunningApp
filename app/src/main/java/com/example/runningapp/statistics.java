package com.example.runningapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import lecho.lib.hellocharts.view.PieChartView;

public class statistics extends AppCompatActivity {

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
    private Button enterExpbtn, weekbtn, catbtn, monbtn,allexpense;
    dbHandler peopleDB;
    static String USERPREF = "USER"; // or other values
    SharedPreferences sharedpreferences;
    LineChart lineChart1,lineChart2,lineChart3;
    ArrayList<ILineDataSet> lineDataSets1 = new ArrayList<>();
    ArrayList<ILineDataSet> lineDataSets2 = new ArrayList<>();
    ArrayList<ILineDataSet> lineDataSets3 = new ArrayList<>();
    com.example.iseeproject.lineGraph lg;
    PieChartView pieChartView;
    int choice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_view);

        dbhandler = new dbHandler(this);

        userr = dbhandler.getLoggedUser();

        MenuHandler = new menuHandler(statistics.this, userr.getUserID());
        menuBtn = (ImageButton) findViewById(R.id.menuLines);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(statistics.this, v);
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

        //we set the calendar view in the ui
        myCalendar = Calendar.getInstance();

        datepickFrom = (EditText) findViewById(R.id.selectDate1);
        datepickTo = (EditText) findViewById(R.id.selectDate2);
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
                                                DatePickerDialog mDatePicker = new DatePickerDialog(statistics.this, date1, myCalendar
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
                                              DatePickerDialog mDatePicker = new DatePickerDialog(statistics.this, date2, myCalendar
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
                else {

                }

                lineChart1 = (LineChart)findViewById(R.id.kmrunChart);
                lineChart2 = (LineChart)findViewById(R.id.caloriesChart);
                lg.setWeekGraphStyle(lineChart1,lineDataSets2);
                lg.setMonthGraphStyle(lineChart2,lineDataSets3);
                lg.setCatGraphStyle(pieChartView);

                //get sum of money spent in expenses this month
                double sum=0;
                    LocalDate now = LocalDate.now();
                    //creating a year month object containing information
                    YearMonth yearMonthObject = YearMonth.of(2019, now.getMonth().getValue());
                    LocalDate firstMonthDate = yearMonthObject.atDay(1);

                    formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

                    ArrayList<Route> allRoutez = peopleDB.getAllRoutes(userr);
                    for (Route r  : allRoutez) {
                        LocalDate date = r.getStarting_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        //if it is in the current month
                        if (date.isAfter(firstMonthDate) || date.isEqual(firstMonthDate)) {
                            sum += r.getDuration().getTime();
                        }
                    }
                    //if user has entered at least one expense

                TextView durSumView = (TextView) findViewById(R.id.durSumText);

                durSumView.setText(String.valueOf(sum));

            }
        });



        TextView speedView = (TextView) findViewById(R.id.avgspeedview);
        String welcometext = "Welcome, " + userr.getName();
        speedView.setText(welcometext);

        TextView stepsView = (TextView) findViewById(R.id.avgstepsview);
        stepsView.setText(String.valueOf(userr.getBudget()));

        TextView incomeView = (TextView) findViewById(R.id.kcal);
        incomeView.setText(String.valueOf(userr.getIncome()));

        TextView stableExpenses =(TextView) findViewById(R.id.StableExpenses);
        Double stableSum = userr.getBills() + userr.getRent() + userr.getInsurance();
        stableExpenses.setText(String.valueOf(stableSum));

        TextView expensesView = (TextView) findViewById(R.id.TotalExpenses);


    }

    private void updateLabel(EditText datepick) {
        String myFormat = "MM-dd-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        datepick.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}