package com.example.administrator.coolweather20.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/24 0024.
 */

public class County extends DataSupport {
    private  int id;
    private  String countyName;
    private  String weatherId;
    private int cityId;
    public  void setId(int id){
        this.id=id;
    }
    public  void setCountyName(String countyName){
        this.countyName=countyName;
    }
    public  void  setWeatherId(String weatherId){
        this.weatherId=weatherId;
    }
    public  void setCityId(int cityId){
        this.cityId=cityId;
    }
    public  int getId(){
        return  id;
    }
    public  int getCityId(){
        return  cityId;
    }
    public  String getCountyName(){
        return countyName;
    }
    public  String getWeatherId(){
        return  weatherId;
    }
}
