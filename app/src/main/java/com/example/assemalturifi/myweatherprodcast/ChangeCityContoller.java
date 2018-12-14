package com.example.assemalturifi.myweatherprodcast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityContoller extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private ImageButton backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city_contoller);

        upViews();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();// will finish the activity
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String newCity = editText.getText().toString();

                Intent intent = new Intent(ChangeCityContoller.this, WeatherControl.class);

                intent.putExtra("NewCity", newCity);
                startActivity(intent);

                return false;
            }
        });
    }

    private void upViews() {
        editText = findViewById(R.id.queryET);
        textView = findViewById(R.id.getWeatherTV);
        backbtn = findViewById(R.id.backBtn);

    }

}
