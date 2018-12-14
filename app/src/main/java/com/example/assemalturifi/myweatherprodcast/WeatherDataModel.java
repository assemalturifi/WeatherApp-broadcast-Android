package com.example.assemalturifi.myweatherprodcast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel  {


    private String temperature;
    private int condition;
    private String city;
    private String iconName;

    public static WeatherDataModel fromJson(JSONObject jsonObject) {

        try {
            WeatherDataModel weatherDataModel = new WeatherDataModel();

            weatherDataModel.city = jsonObject.getString("name");
            weatherDataModel.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherDataModel.iconName = updateWeatherIcon(weatherDataModel.condition);

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;

            int roundValue = (int)Math.rint(tempResult);
            weatherDataModel.temperature = Integer.toString(roundValue);

            return weatherDataModel;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
    // TODO: Uncomment to this to get the weather image name from the condition:
    private static String updateWeatherIcon ( int condition){

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    // TODO: Create getter methods for temperature, city, and icon name:


    public String getTemperature() {
        return temperature+"°";
    }

    public String getCity() {
        return city;
    }

    public String getIconName() {
        return iconName;
    }
}
