package com.example.administrator.coolweather20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.coolweather20.gson.Forecast;
import com.example.administrator.coolweather20.gson.Weather;
import com.example.administrator.coolweather20.service.AutoUpdateService;
import com.example.administrator.coolweather20.util.HttpUtil;
import com.example.administrator.coolweather20.util.Utility;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private  TextView titleUpdateTime;
    private  TextView degreeText;
    private  TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private  TextView pm25Text;
    private TextView comforText;
    private  TextView carWashText;
    private  TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;
    private Button navButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorview=getWindow().getDecorView();
            decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        drawerLayout=(DrawerLayout)findViewById(R.id.draw_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comforText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=preferences.getString("weather",null);
        final  String weatherId;
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherRespone(weatherString);
            weatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
             weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        String  bingPic=preferences.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else {
            loadBingPic();
        }
    }

    private void loadBingPic() {
        String  requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
             final  String bingPic=response.body().string();
             SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
             editor.putString("bing_pic",bingPic);
             editor.apply();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                 }
             });

            }
        });
    }

    public void requestWeather( final  String weatherId) {
        String  weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+
                "&key=4f34050390ae4beb9b479386a0739f1f";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                }
            });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
             final  String responseText=response.body().string();
             final  Weather weather=Utility.handleWeatherRespone(responseText);
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if(weather!=null&&"ok".equals(weather.status)){
                         SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                         editor.putString("weather",responseText);
                         editor.apply();
                         showWeatherInfo(weather);
                     }else {
                         Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                     }
                   swipeRefreshLayout.setRefreshing(false);
                 }
             });
            }
        });
        loadBingPic();

    }

    private void showWeatherInfo(Weather weather) {
        if(weather!=null&&"ok".equals(weather.status)){
            String  cityName=weather.basic.cityName;
            String updateTime=weather.basic.update.updateTime.split(" ")[1];
            String degree =weather.now.temperature+"℃";
            String weatherInfo=weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for(Forecast forecast:weather.forecastList){
                View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
                TextView dateText=(TextView)view.findViewById(R.id.date_text);
                TextView infoText=(TextView)view.findViewById(R.id.info_text);
                TextView maxText=(TextView)view.findViewById(R.id.max_text);
                TextView minText=(TextView)view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if(weather.aqi!=null){
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);

            }
            String comfort ="舒适度："+weather.suggestion.comfort.info;
            String  carWash="洗车指数："+weather.suggestion.carWash.info;
            String sport="运动指数："+weather.suggestion.sport.info;
            comforText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            Intent intent=new Intent(WeatherActivity.this, AutoUpdateService.class);
            startService(intent);
        }else{
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
        }

    }
}
