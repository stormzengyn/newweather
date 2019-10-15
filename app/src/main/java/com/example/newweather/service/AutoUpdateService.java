package com.example.newweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.newweather.gson.Weather;
import com.example.newweather.util.HttpUtil;
import com.example.newweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        //AlarmManager闹钟服务，AlarmManager是Android中常用的一种系统级别的提示服务，在特定的时刻为我们广播一个指定的Intent。简单的说就是我们设定一个时间，然后在该时间到来时，AlarmManager为我们广播一个我们设定的Intent
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        int anHour = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        int oneminute = 60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + oneminute;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);//取消闹铃
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        //set(int type，long startTime，PendingIntent pi)；该方法用于设置一次性闹钟，第一个参数表示闹钟类型，
        // 第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。
        //AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2
        //PendingIntent pi： 绑定了闹钟的执行动作，比如发送一个广播、给出提示等等。PendingIntent是Intent的封装类。
        //如果是通过广播来实现闹钟提示的话，PendingIntent对象的获取就应该采用 PendingIntent.getBroadcast(Context c,int i,Intent intent,int j)方法；
        return super.onStartCommand(intent, flags, startId);
    }

    //创建流程：
    //  创建Intent用来告诉定时器触发后它要做什么，Intent可以是启动activity、启动Service、发送广播。
    //	创建时间值用来告诉定时器什么时候触发。
    //	创建PendingIntent（等待Intent）用来包裹创建好的Intent。
    //	取得AlarmManager系统服务，强制转型并且实例化
    //	用AlarmManager实例以set方法，添加类型、时间值、PendingIntent参数

    /**
     * 更新天气信息。
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&2b12dc7ac8054f2a96d02ba5f2c2c052";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        //其中Editor是一个修改SharedPreferences数据的接口
                        //editor.putString("key名称"，“你想要保存的数据”)
                        editor.putString("weather", responseText);
                        editor.apply();
                        Log.e("AutoUpdateService", "Execute AutoUpdateService");
                        //在编辑中调用 Editor 的提交方法 apply（），这将原子地执行所请求的修改，替换当前在SharedPreferences中的所有内容
                        //当在同一时间有两个 editors 修改 preferences的内容时，以最后一个修改的为准。
                        //与 commit()方法不同的是 commit()方法是同步地储存在磁盘上，apply()是先储存在内存中，然后异步的储存在磁盘上且不会有任何失败的提示；如果apply()异步的内容尚未储存带磁盘上，另一个editor调用了commit()同步提交储存，这个操作将会等待另一个commit()操作完成后才会真正执行。
                        //由于SharedPreferences 在一个进程中是单例的，所以如果你不关心commit()成功与否的，完全可以用apply()来代替。
                        //你不需要担心Android组件生命周期及其与apply（）写入磁盘的交互。该框架确保来自apply（）的在线磁盘写入在切换状态之前完成
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }


}