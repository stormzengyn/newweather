package com.example.newweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //为了保存软件的设置参数，Android平台为我们提供了一个SharedPreferences接口，它是一个轻量级的存储类，
        //特别适合用于保存软件配置参数。使用SharedPreferences保存数据，其背后是用xml文件存放数据，文件存放在/data/data/<package name>/shared_prefs目录下。
        //每个应用都有一个默认的配置文件preferences.xml，使用getDefaultSharedPreferences获取
        //Preference直译为偏好，建议翻译为首选项。一些配置数据，一些我们上次点击选择的内容，我们希望在下次应用调起的时候依然有效，
        //无须用户再一次进行配置或选择。Android提供preference这个键值对的方式来处理这种情况，自动保存这些数据，并立时生效。

        //不同activity间的数据传输方法主要有两种一种是用Bundle传输数据，一种是用SharedPreferences。
        //两者的区别，一般来讲SharedPreferences用来存储轻型数据，保存在xml里，可以持久保存。反观Bundle可以传输很多中数据，但是不持久。
        if (prefs.getString("weather", null) != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            //有缓存时直接跳转到WeatherActivity中去处理天气信息并展示出来
            startActivity(intent);
            finish();
        }
    }
}
