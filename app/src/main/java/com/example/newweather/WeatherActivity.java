package com.example.newweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newweather.gson.Weather;
import com.example.newweather.util.HttpUtil;
import com.example.newweather.util.Utility;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {


    private LinearLayout weatherInfoLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private TextView  windscText;

    private TextView  winddirText;

    private Button refreshWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        Log.d("WeatherActivity", "onCreate execute");
        // 初始化各控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        //R.java可以看成是把主程序和res文件夹中资源文件夹联系起来的一个桥梁。
        //比如使用Button这个组件，首先需要在res/layout文件夹里编写，然后赋予它一个id，这样R.java里面就会自动生成一个ID与其对应。
        //代码中setContentView(R.layout.activity_main)作用是设置界面布局，并设置了该Activity的关联视图集根；
        //titleCity = (TextView) findViewById(R.id.title_city);从视图集根遍历找到id为title_city的视图，并将它转化为TextView类型
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        windscText = (TextView) findViewById(R.id.windsc_text);
        winddirText = (TextView) findViewById(R.id.winddir_text);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        refreshWeather.setOnClickListener(new View.OnClickListener() {//使用匿名内部类实现监听器
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.refresh_weather:
                        System.out.println("使用内部类实现点击事件");
                        requestWeather(weatherId);
                        break;
                    default:
                        break;
                }
            }
        });


    }



    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&2b12dc7ac8054f2a96d02ba5f2c2c052";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //Log.e("WeatherActivity",responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = "上次更新时间 " + weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature;
        String weatherInfo = weather.now.more.info+"(实时)";
        String Windsc = weather.now.windsc+"级";
        String Winddir = weather.now.winddir;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        windscText.setText(Windsc);
        winddirText.setText(Winddir);
        weatherInfoLayout.setVisibility(View.VISIBLE);
    }

}


