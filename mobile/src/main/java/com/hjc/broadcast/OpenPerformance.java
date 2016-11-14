package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hujiachun684 on 16/5/30.
 * 接收广播,打开性能服务开关
 * 性能文件夹
 */
public class OpenPerformance extends BroadcastReceiver{
    private SharedPreferences preferences;
    @Override
    public void onReceive(Context context, Intent intent) {

        String package_str = intent.getStringExtra(Constants.KEY_PACKAGE);
        int interval = Integer.parseInt(intent.getStringExtra("INTERVAL"));
        String appVersion = intent.getStringExtra("VERSION");
        preferences = Settings.getDefaultSharedPreferences(context);
        String str = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(new Date().getTime()));

        new File(Constants.PERFORMANCE_PATH + package_str.split(":")[0] + "_" + str).mkdirs();
        preferences.edit().putBoolean(Settings.KEY_PERFORMANCE, true)
                .putString(Settings.KEY_PACKAGE, package_str).putInt(Settings.KEY_INTERVAL, interval)
                .putString(Settings.KEY_TIME, str)
                .putString(Settings.KEY_VERSION, appVersion)
                .commit();
        Toast.makeText(context, "性能任务已开启", Toast.LENGTH_SHORT).show();
    }
}
