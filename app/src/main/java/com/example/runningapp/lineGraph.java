package com.example.iseeproject;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.itextpdf.text.pdf.parser.Line;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.view.animation.Animation;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class lineGraph {

    //private LineChart lineChart;
    //private ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();


    ArrayList<String> xAxes = new ArrayList<>();

    dbHandler peopleDB ;
    User userr;

    public lineGraph(User user,dbHandler db) {
        peopleDB = db;
        userr = user;
    }

    public void setWeekGraphStyle(LineChart lineChart, ArrayList<ILineDataSet> lineDataSets) {

        ArrayList<Entry> xyCoord = calculateWeekAxes();
        if (!xyCoord.isEmpty()) {
            LineDataSet lineDataSet = new LineDataSet(xyCoord, "Current Week Expenses");
            lineDataSet.setDrawCircles(true);
            lineDataSet.setColor(Color.BLACK);
            lineChart.setPinchZoom(true);
            lineChart.setDrawBorders(true);
            lineChart.resetZoom();
            lineChart.setScaleXEnabled(true);
            lineChart.setScaleYEnabled(true);
            lineChart.setBackgroundColor(Color.parseColor("#EFEBE9"));
            lineDataSets.add(lineDataSet);
            //removes xaxes
            lineChart.setData(new LineData(lineDataSets));

            lineChart.setVisibleYRange(0, getMaxY(xyCoord), YAxis.AxisDependency.LEFT);

            //because of 7 days of week
            lineChart.setVisibleXRange(0, 6);

            lineChart.setTouchEnabled(true);
            lineChart.setDragEnabled(true);

            String[] week = {"Mon", "Tue", "Wed", "Thurs", "Fri", "Sat", "Sun"};

            for (int i = 0; i < week.length ; i++) {
                Description d = new Description();
                d.setText(week[i]);
                Entry xy = xyCoord.get(i);
                d.setPosition(xy.getX(), xy.getY());
                d.setTextAlign(Paint.Align.CENTER);
                d.setTextColor(Color.BLACK);
                d.setTextSize(10);
                lineChart.setDescription(d);
            }
        }

    }

    public void setMonthGraphStyle(LineChart lineChart, ArrayList<ILineDataSet> lineDataSets) {

        ArrayList<Entry> xyCoord = calculateMonthAxes();
        if (!xyCoord.isEmpty()) {
            LineDataSet lineDataSet = new LineDataSet(xyCoord, "Monthly Expenses");
            lineDataSet.setDrawCircles(true);
            lineDataSet.setColor(Color.BLACK);
            lineDataSet.setLineWidth(1);

            lineDataSets.add(lineDataSet);
            //removes xaxes
            lineChart.setData(new LineData(lineDataSets));

            lineChart.setVisibleYRange(0, getMaxY(xyCoord), YAxis.AxisDependency.LEFT);

            //because of 4 weeks of month
            lineChart.setVisibleXRange(0, 31);

            //lineChart.setVisibleYRangeMaximum(100F, YAxis.AxisDependency.LEFT);
            lineChart.setDrawBorders(true);
            lineChart.setTouchEnabled(true);
            lineChart.setDragEnabled(false);
            lineChart.setPinchZoom(true);
            lineChart.resetZoom();
            lineChart.setScaleXEnabled(true);
            lineChart.setScaleYEnabled(true);
            lineChart.setBackgroundColor(Color.parseColor("#EFEBE9"));

        }
    }

    public void setCatGraphStyle(PieChartView pieChartView) {

        Map<String, Double> xyCoord = calculateCatAxes();
        if (!xyCoord.isEmpty()) {
            //first getting the sum of all expenses to be dividing with
            List<Expenses> expenses = peopleDB.getAllExpenses(userr);
            double sum = 0.0;
            for (Expenses exp : expenses) {
                sum += exp.getPrice();
            }
            //getting random colors to color the pieces of the chart
            Random rand = new Random();
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);

            List<SliceValue> pie_Data = new ArrayList<>();
            long percent = 0, percentsum = 0;
            double div;
            String description = "";
            for (Map.Entry<String,Double> entry : xyCoord.entrySet()) {
                if (entry.getValue() > 0){
                    div = Math.round((entry.getValue().doubleValue() / sum) * 100);
                    percent = (long) div;
                    percentsum += percent;
                    description = entry.getKey() + " : " +
                            String.valueOf(entry.getValue()) + "€ \n (" + String.valueOf(percent) + "%)";
                    r = rand.nextInt(255);
                    g = rand.nextInt(255);
                    b = rand.nextInt(255);
                    pie_Data.add(new SliceValue(percent, Color.rgb(r, g, b)).setLabel(description));
                }
            }
            //pie_Data.add(new SliceValue(40, Color.rgb(r,g,b)).setLabel("yolo"));

            PieChartData pieChartData = new PieChartData(pie_Data);
            pieChartData.setHasLabels(true).setValueLabelTextSize(14);
            pieChartData.setHasCenterCircle(true).setCenterText1("Expenses per category %").setCenterText1FontSize(15).setCenterText1Color(Color.BLACK);
            pieChartView.setPieChartData(pieChartData);

        }
    }

    public ArrayList<Entry> calculateWeekAxes() {
        ArrayList<Entry> xyCoord = new ArrayList<>();

        //setting up the calendar dates of this week
        LocalDate now = LocalDate.now();

        TemporalField fieldISO2 = WeekFields.of(Locale.GERMANY).dayOfWeek();
        System.out.println(now.with(fieldISO2, 1));

        //getting the expenses and their dates
        if (peopleDB.expensesExist(userr)) {
            List<Expenses> expenses = peopleDB.getAllExpenses(userr);
            double daysum = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            formatter = formatter.withLocale(Locale.GERMANY);
            LocalDate expdate;

            for (int i = 1; i <= 7; i++) {
                //i presents monday for 1, tuesday for 2 etc
                LocalDate d = now.with(fieldISO2, i);//get week date
                for (Expenses exp : expenses) {
                    //search all expenses and add to the one of this day of the week
                    expdate = LocalDate.parse(exp.getExpenseTime(), formatter);
                    if (d.isEqual(expdate)) {
                        daysum += exp.getPrice();
                    }
                }
                //after all expenses of the day are added,
                //add this as a new entry to the LineGraph
                float x = (float) (i - 1);//bc x starts from 1
                float y = (float) daysum;
                xyCoord.add(new Entry(x, y));
                daysum = 0;//make daysum 0 again for the next day
            }
        }
        return xyCoord;
    }

    public ArrayList<Entry> calculateMonthAxes() {
        ArrayList<Entry> xyCoord = new ArrayList<>();

        //setting up the calendar dates of this week
        LocalDate now = LocalDate.now();
        //creating a year month object containing information
        YearMonth yearMonthObject = YearMonth.of(2019, now.getMonth().getValue());

        //getting the expenses and their dates
        if (peopleDB.expensesExist(userr)) {
            List<Expenses> expenses = peopleDB.getAllExpenses(userr);
            double daysum = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            formatter = formatter.withLocale(Locale.GERMANY);
            LocalDate expdate, d;

            for (int i = 1; i <= yearMonthObject.lengthOfMonth(); i++) {
                //i presents monday for 1, tuesday for 2 etc
                for (Expenses exp : expenses) {
                    //search all expenses and add to the one of this day of the month
                    d =  yearMonthObject.atDay(i);//get week date
                    d.format(formatter);
                    expdate = LocalDate.parse(exp.getExpenseTime(), formatter);
                    if (d.isEqual(expdate)) {
                        daysum += exp.getPrice();
                    }
                }
                //after all expenses of the day are added,
                //add this as a new entry to the LineGraph
                float x = (float) (i - 1);//bc x starts from 1
                float y = (float) daysum;
                xyCoord.add(new Entry(x, y));
                daysum = 0;//make daysum 0 again for the next day
            }
        }
        return xyCoord;
    }

    public Map<String, Double> calculateCatAxes() {
        Map<String, Double> xyCoord = new HashMap<String, Double>();

        //getting the expenses and their dates
        if (peopleDB.expensesExist(userr)) {
            Map<String, Double> map = peopleDB.getThresholds(userr.getUsername());
            //we have to get only the current month's expenses,
            //therefore we will call the menuHandler method sortMonthExpenses
            List<Expenses> expenses = peopleDB.getAllExpenses(userr);

            LocalDate now = LocalDate.now();
            //creating a year month object containing information
            YearMonth yearMonthObject = YearMonth.of(2019, now.getMonth().getValue());
            LocalDate firstMonthDate = yearMonthObject.atDay(1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

            for(Iterator<Expenses> it = expenses.iterator(); it.hasNext();) {
                Expenses exp = it.next();
                LocalDate expdate = LocalDate.parse(exp.getExpenseTime(), formatter);
                //if it is earlier than the current month
                if (expdate.isBefore(firstMonthDate)) {
                    it.remove();
                }
            }

            //and then we calculate the sums for the remaining expenses
            double catsum = 0;
            for (Map.Entry<String,Double> entry : map.entrySet()) {
                for (Expenses exp : expenses) {
                    //search all expenses and add to the one of this category
                    if (exp.getCategory().contentEquals(new StringBuffer(entry.getKey()))) {
                        catsum += exp.getPrice();
                    }
                }
                //after all expenses of the day are added,
                //add this as a new entry to the LineGraph
                xyCoord.put(entry.getKey(),catsum);
                catsum = 0;
            }
        }
        return xyCoord;
    }

    //mini function for returning back the maximum y coord of every set of points
    public float getMaxY(ArrayList<Entry> xyCoord) {
        float maxY = 0;
        for ( Entry xy : xyCoord) {
            if (xy.getY() > maxY) {
                maxY =xy.getY();
            }
        }
        //we set a 20% extra of the maximum y coord so that we make sure the graph is always visible
        return (maxY + (maxY*20)/100);
    }
}
