package com.jihoon.weatherapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    EditText cityInput;
    Button searchBtn;
    TextView resultText;
    ImageView weatherIcon;

    String API_KEY = "9abd248405ce9c535f276c235461f0d9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        searchBtn = findViewById(R.id.searchBtn);
        resultText = findViewById(R.id.resultText);
        weatherIcon = findViewById(R.id.weatherIcon);


        weatherIcon.setVisibility(ImageView.GONE);

        searchBtn.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                getWeather(city);
            }
        });
    }

    private void getWeather(String city) {
        new Thread(() -> {
            try {
                String finalCity = city;

                // 일본어 → 영어 변환
                if (finalCity.equals("東京")) finalCity = "Tokyo";
                else if (finalCity.equals("大阪")) finalCity = "Osaka";
                else if (finalCity.equals("ソウル")) finalCity = "Seoul";

                String encodedCity = URLEncoder.encode(finalCity, "UTF-8");

                String urlString =
                        "https://api.openweathermap.org/data/2.5/weather?q="
                                + encodedCity +
                                "&appid=" + API_KEY +
                                "&units=metric&lang=ja";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                StringBuilder result = new StringBuilder();
                int data;
                while ((data = reader.read()) != -1) result.append((char) data);

                JSONObject json = new JSONObject(result.toString());

                if (!json.has("main")) {
                    runOnUiThread(() -> resultText.setText("都市が見つかりません"));
                    return;
                }

                String temp = json.getJSONObject("main").getString("temp");
                String humidity = json.getJSONObject("main").getString("humidity");
                String weather = json.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("description");

                String icon = json.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("icon");
                String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";

                String finalCityResult = finalCity;

                runOnUiThread(() -> {
                    resultText.setText(
                            "都市: " + finalCityResult +
                                    "\n気温: " + temp + "℃" +
                                    "\n湿度: " + humidity + "%" +
                                    "\n天気: " + weather
                    );

                    Glide.with(this)
                            .load(iconUrl)
                            .into(weatherIcon);

                    weatherIcon.setVisibility(ImageView.VISIBLE);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> resultText.setText("エラーが発生しました"));
            }
        }).start();
    }
}

