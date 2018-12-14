package com.example.assemalturifi.myweatherprodcast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WeatherControl extends AppCompatActivity {

    //binding the views and buttons, member variable
    private ImageButton changeCityBtn;
    private TextView temperatureLabel;
    private ImageView weatherImage;
    private TextView cityLabel;

    //for the location provider

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;//GPS_Provider because we are using the emulator,
    // if you are using a physical device , and have requested the access course in the manifest file,
    // then you should set the LocationProvider to networkProvider instead.
    // Network provider means that requesting location infofrom cell towers and wifi networks


    LocationManager locationManager;// will start or stop location updates
    LocationListener locationListener;// if the location has changed, IT DOES THE CHECKING FOR UPDATES ON THE DEVICE LOCATION

    final int REQUEST_CODE=123;// for the permision code
    // Time between location updates -5 seconds
    final long MIN_TIME = 5000;
    // Distance between location updates 1km
    final float MIN_DISTANCE = 1000;

    final String FORLOG = "Weather";


    // for the API's

    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final String APP_ID = "ce8ead9923ad7a060500119ff151d4c3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_control);


        upView();

        changeCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherControl.this,ChangeCityContoller.class);

                startActivity(intent);
            }
        });


    }

    private void upView() {
        cityLabel = findViewById(R.id.locationTV);
        weatherImage = findViewById(R.id.weatherSymbol);
        temperatureLabel = findViewById(R.id.tempTV);
        changeCityBtn = findViewById(R.id.changeCityBtn);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(FORLOG, "onResume() called");


        Intent intent = getIntent();
        String theNewCity = intent.getStringExtra("NewCity");

        if(theNewCity!=null){
            getWeatherForNewCity(theNewCity);

        }
        else{
            Log.d(FORLOG, "Getting weather for current location");

            getWeatherForCurrentLocation();

        }
    }
    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsDoSomeNetworkingForAPI(params);

    }

    public void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);// this line of code that holds of a location manager and assign that location manager object
        // to mLocationmanager member variable

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(FORLOG, "onLocationChanged() callBack is received");

                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d(FORLOG, "longitude is: "+longitude);
                Log.d(FORLOG, "latitude is: "+latitude);


                RequestParams params = new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);

                letsDoSomeNetworkingForAPI(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {// when you enable the app to access the location

            }

            @Override
            public void onProviderDisabled(String provider) {// when you disable the app to access the location

                Log.d(FORLOG, "onProviderDisabled( ) callBack received");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            //listening if the the permission had been given
            //requesting the location permission from the user
            Log.d(FORLOG, "requesting the permissin");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;


        }
        //HERE THE LOCATIONMANAGER STARTS requesting updates
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);

    }

    //cheching if the user granted the permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(FORLOG, "onRequestPermissionsResult(): Permission granted");
                getWeatherForCurrentLocation();
            }
            else{
                Log.d(FORLOG, "Permission denied =(");

            }

        }
    }

    // This is the actual networking code. Parameters are already configured.This method requesting data from the webpage
    public void letsDoSomeNetworkingForAPI(RequestParams params) {

        // AsyncHttpClient belongs to the loopj dependency.
        AsyncHttpClient client = new AsyncHttpClient();// to get the weather data we need to perform HTTP request

        // Making an HTTP GET request by providing a URL and the parameters.
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(FORLOG, "Success! JSON: " + response.toString());

                WeatherDataModel weatherData =  WeatherDataModel.fromJson(response);
                updateUI(weatherData);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e(FORLOG, "Fail " + e.toString());
                Log.d(FORLOG, "Status code " + statusCode);

                Toast.makeText(WeatherControl.this, "Request Failed",Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null)
            locationManager.removeUpdates(locationListener);
    }

    private void updateUI(WeatherDataModel weatherDataModel) {
        temperatureLabel.setText(weatherDataModel.getTemperature());
        cityLabel.setText(weatherDataModel.getCity());

        int resourceId = getResources().getIdentifier(weatherDataModel.getIconName(), "drawable", getPackageName());

        weatherImage.setImageResource(resourceId);

    }

}
