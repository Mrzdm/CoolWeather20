package com.example.administrator.coolweather20.util;

import android.text.TextUtils;

import com.example.administrator.coolweather20.db.City;
import com.example.administrator.coolweather20.db.County;
import com.example.administrator.coolweather20.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/24 0024.
 */

public class Utility  {
    public  static  boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0; i<allProvinces.length(); i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();

                }
                return  true;
                }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return  false;
    }
    public  static  boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCites=new JSONArray(response);
                for(int i=0; i<allCites.length(); i++){
                    JSONObject cityObject=allCites.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return  true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return  false;
    }
    public  static  boolean handleCountyResponse(String  response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties=new JSONArray(response);
                for(int i=0; i<allCounties.length(); i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setCityId(cityId);
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();

                }
                return  true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return  false;
    }
}
