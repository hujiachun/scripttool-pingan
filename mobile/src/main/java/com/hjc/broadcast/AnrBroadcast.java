package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.service.MonkeyService;
import com.hjc.service.TimerService;
import com.hjc.util.Constants;
import com.hjc.util.ShellUtils;
import com.hjc.util.Util;

import java.io.File;
import java.io.IOException;

/**
 * Created by hujiachun684 on 16/5/10.
 */
public class AnrBroadcast extends BroadcastReceiver{
    String path, traces, traces2;
    int account;
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = Settings.getDefaultSharedPreferences(context.getApplicationContext());
        path = preferences.getString(Settings.KEY_LOGCAT_PATH, "");
        traces = preferences.getString(Settings.KEY_TRACES, "");
//        account = intent.getIntExtra(Constants.KEY_ACCOUNT, 0);
        traces2 = Util.getTracesTime();

        if(!traces.equals(traces2)){
            preferences.edit().putString(Settings.KEY_TRACES, traces2).commit();
            Thread thread = new AnrThread();
            thread.start();
        }

    }


    class AnrThread extends Thread{

        @Override
        public void run() {
            ShellUtils.execCommand("cat /data/anr/traces.txt >> " + path + "/" + "traces.txt", false);

            super.run();
        }
    }
}
