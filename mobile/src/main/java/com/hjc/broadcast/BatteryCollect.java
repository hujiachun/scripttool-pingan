package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.ShellUtils;
import com.hjc.util.Util;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by hujiachun on 16/10/17.
 * 设备需要root
 */

public class BatteryCollect extends BroadcastReceiver{
    private String resultFilePath;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final SharedPreferences preferences = Settings.getDefaultSharedPreferences(context);
        resultFilePath = Constants.PERFORMANCE_PATH + preferences.getString(Settings.KEY_PACKAGE, "").split(":")[0]
                + "_" + preferences.getString(Settings.KEY_TIME, Constants.NA);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShellUtils.execCommand("dumpsys batterystats >>" + resultFilePath + "/battery.txt", true);
                try {
                    String uid = Util.getBatteryUid(preferences.getString(Settings.KEY_PACKAGE, Constants.DEFAULT_PACKAGE).split(":")[1]);

                    RandomAccessFile rafRcv = new RandomAccessFile(resultFilePath + "/battery.txt", "r");
                    String line = "";
                    while ((line = rafRcv.readLine()) != null){
                        if(line.contains("Uid " + uid)){
                            Log.e(Constants.TAG, line);
                            Log.e(Constants.TAG, line.split(": ")[1]);
                            return;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
