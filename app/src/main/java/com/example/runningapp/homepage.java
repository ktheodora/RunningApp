package com.example.runningapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class homepage extends AppCompatActivity implements SensorEventListener {

    String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";
    String API_TOKEN = "20773309acdcb2327c3e622c91d0bd3f";
    double lat;
    double lon;

    TextView city;
    TextView degrees;
    TextView rateView;
    TextView clothesView;

    int mAzimuth;
    private SensorManager mSensorManager;
    private ImageView compassImage;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    // record the angle turned of the compass picture
    private float DegreeStart = 0f;

    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.homepage_view);
        dbHandler db_handler = new dbHandler(this);
        db_handler.setCtx(this);

        //System.out.println("TEMP --------------"+db_handler.userTemp);

        city = (TextView) findViewById(R.id.city);
        degrees = findViewById(R.id.degrees);
        rateView = findViewById(R.id.humidity);
        clothesView = findViewById(R.id.outfit);

        mQueue = Volley.newRequestQueue(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compassImage = (ImageView) findViewById(R.id.compass);

        startCompass();

       /* double longitude = 0;
        double latitude = 0;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                System.out.println(latitude);
                System.out.println(longitude);
            }
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String postalCode = "";
        String countryCode = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            // Display the address, the city and the country of the infraction using the latitude and longitude
            postalCode = addresses.get(0).getPostalCode();
            countryCode = addresses.get(0).getCountryCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(postalCode);
        System.out.println(countryCode);

*/
        retrieveWeather("264371", "GR");

        //Firebase test
        //User us = new User("2","1","Guillaume","toto@gmail.com","BETROM",5,5,5,5,5);
        //db_handler.addUser(us);

        //db_handler.getUser("2");

        Button showStatsbtn = (Button) findViewById(R.id.showStatsbtn);
        showStatsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(homepage.this, statistics.class);
                startActivity(myIntent);
            }
        });

    }

    public void retrieveWeather(String cityid, String countryCode){
        String url = BASE_URL + "id=" + cityid + "&appid=" + API_TOKEN;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    DecimalFormat df = new DecimalFormat("0.00");

                    System.out.println(response);

                    JSONArray weather_tab = response.getJSONArray("weather");
                    JSONObject weather = weather_tab.getJSONObject(0);
                    String main = weather.getString("main");
                    System.out.println(main);

                    String cityLabel = response.getString("name");

                    JSONObject mainTemp = response.getJSONObject("main");
                    String temp = mainTemp.getString("temp");
                    double tempInCelsius = Double.valueOf(temp) - 273.15;

                    String humidity = mainTemp.getString("humidity");

                    city.setText(cityLabel);
                    degrees.setText(df.format(tempInCelsius) + " °C");
                    rateView.setText(humidity + " %");

                    switch(main){
                        case "Thunderstorm":
                            clothesView.setText("Don't run today");
                        case "Drizzle":
                            clothesView.setText("Pants + Sweat with hood");
                        case "Rain":
                            clothesView.setText("Rain clothes");
                        case "Snow":
                            clothesView.setText("Snow clothes");
                        case "Clear":
                            clothesView.setText("Short + Sweater or T-shirt");
                        case "Clouds":
                            clothesView.setText("Short + Sweater");
                        default:
                            clothesView.setText("Pants + Sweater");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Json error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        mQueue.add(jor);
    }


    public void startCompass(){
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)
                    || (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null)) {
                noSensorAlert();
            } else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        } else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the compass.")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop(){
        if (haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else{
            if(haveSensor){
                mSensorManager.unregisterListener(this, mRotationV);
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        startCompass();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        System.out.println("compass changed");
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0])+360)%360;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
            mLastAccelerometerSet = true;
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }

        if (mLastMagnetometerSet && mLastAccelerometerSet){
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360 ) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        compassImage.setRotation(-mAzimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}