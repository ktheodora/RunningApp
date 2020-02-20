package com.example.runningapp;

import android.Manifest;
import android.content.Context;
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

    //Compass
    // device sensor manager
    private SensorManager SensorManage;
    // define the compass picture that will be use
    private ImageView compassimage;
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

        Intent myIntent = new Intent(homepage.this, homepage.class);
        startActivity(myIntent);

        System.out.println("TEMP --------------"+db_handler.userTemp);

        //Compass
        compassimage = (ImageView) findViewById(R.id.compass);
        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);

        city = (TextView) findViewById(R.id.city);
        degrees = findViewById(R.id.degrees);
        rateView = findViewById(R.id.humidity);
        clothesView = findViewById(R.id.outfit);

        mQueue = Volley.newRequestQueue(this);

        double longitude = 0;
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


        retrieveWeather(postalCode, countryCode);

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

    public void retrieveWeather(String cityid, String countryCode){
        String url = BASE_URL + "zip=" + cityid + "," + countryCode + "&appid=" + API_TOKEN;

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        //DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");
        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Not used
    }
}