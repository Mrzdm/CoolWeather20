package com.example.administrator.coolweather20.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.administrator.coolweather20.gson.Weather;
import com.example.administrator.coolweather20.util.HttpUtil;
import com.example.administrator.coolweather20.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int eightHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+eightHour;
         Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
         alarmManager.cancel(pi);
         alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        String  requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                   String  bingPic=response.body().string();
                   SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                   editor.putString("bing_pic",bingPic);
                   editor.apply();
            }
        });
    }

    private void updateWeather() {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String  weatherString=preferences.getString("weather",null);
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherRespone(weatherString);
            String weatherId=weather.basic.weatherId;
            final String  weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+
                    "&key=4f34050390ae4beb9b479386a0739f1f";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                  String  responseText=response.body().string();
                  Weather weather=Utility.handleWeatherRespone(responseText);
                  if(weather!=null&&"ok".equals(weather.status)){
                      SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                      editor.putString("sweather",responseText);
                      editor.apply();
                  }

                }
            });
        }
    }
}
