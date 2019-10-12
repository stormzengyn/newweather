package com.example.newweather.util;

import android.text.TextUtils;

import com.example.newweather.db.City;
import com.example.newweather.db.County;
import com.example.newweather.db.Province;
import com.example.newweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            //JSONObject的数据是用 {  } 来表示的
            //而JSONArray，顾名思义是由JSONObject构成的数组，用  [ { } , { } , ......  , { } ]  来表示
            JSONObject jsonObject=new JSONObject(response);
            //实例化一个JSONObject对象，也就是将json字符串response转化为JSONObject对象
            //response是String类型，里面是从服务器中获得的json天气数据
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            //对jsonObject采用getJSONArray方法，定位到jsonObject中的HeWeather,jsonObject内容为{"HeWeather":[{...}]}
            //HeWeather后面是一个数组（有[]）,所以用getJSONArray方法得到数组
            //JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");这一句得到的jsonArray内容为[{...}]
            String weatherContent=jsonArray.getJSONObject(0).toString();
            //用getJSONObject方法对jsonArray获取它数组中JSONObject对象，getJSONObject(0)即取这个数组的第0个对象
            //jsonArray内容为[{...}]，里面只有一个对象{ }，故 weatherContent内容为{ }
            return new Gson().fromJson(weatherContent,Weather.class);
            //Gson提供了fromJson()方法来实现从Json相关对象到Java实体的方法
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}


