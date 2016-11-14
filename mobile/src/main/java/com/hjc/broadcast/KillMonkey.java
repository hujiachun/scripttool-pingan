package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.service.EventService;
import com.hjc.service.MainService;
import com.hjc.service.MonkeyService;
import com.hjc.service.RadioService;
import com.hjc.service.TimerService;
import com.hjc.util.Constants;
import com.hjc.util.ShellUtils;
import com.hjc.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by hujiachun on 15/12/17.
 */
public class KillMonkey extends BroadcastReceiver{
    int pid = -1, timer = 0;

    String model;
        @Override
        public void onReceive(final Context context, Intent intent) {
             model = Build.MODEL;
            Log.e(Constants.TAG, model);
            Settings.getDefaultSharedPreferences(context).edit().putBoolean("monkey", false).commit();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(model.contains("MX")){
                            pid = getPid("com.hjc.scripttool");

                        }
                        else
                            pid = getPid("com.android.commands.monkey");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(pid == -1){
                        try {
                            while (timer < 300){
                                Thread.sleep(1000);

                                pid = getPid("com.android.commands.monkey");
                                if(pid != -1){
                                    break;
                                }
                                timer++;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(pid != -1){
                            kill();
                        }
                        else {
                            Log.e(Constants.TAG, "没有查到monkey进程");
                        }
                    }
                    else {
                        kill();
                    }
                }
            }).start();

            context.stopService(new Intent().setClass(context, TimerService.class));
            context.stopService(new Intent().setClass(context, MonkeyService.class));
            context.stopService(new Intent().setClass(context, RadioService.class));
//            context.stopService(new Intent().setClass(context, MainService.class));
            context.stopService(new Intent().setClass(context, EventService.class));
            Util.stopLogCat(context);

        }

    public int getPid(String tag) throws IOException {
        java.lang.Process p;
        p = Runtime.getRuntime().exec("ps ");
        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = bufferedReader2.readLine()) != null) {
            Log.e(Constants.TAG, line);
            if (line.contains(tag)) {
                Log.e(Constants.TAG, "monkey pid = "+ line);
                bufferedReader2.close();
                p.destroy();
                return Integer.parseInt(line.split("\\s+")[1]);
            }
        }
        bufferedReader2.close();
        p.destroy();
        return -1;
    }


    public void kill(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e(Constants.TAG, "pid = "+ pid);
                if (pid != -1 ) {
                    if(model.contains("MX")){

                        ShellUtils.execCommand("kill " + pid, false);

                    }else   {
                        ShellUtils.CommandResult result = ShellUtils.execCommand("kill " + pid, true);
                    }

                    Log.e(Constants.TAG, "Monkey 已结束");
                }
            }
        }).start();
    }
}
