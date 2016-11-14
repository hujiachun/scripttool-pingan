package com.hjc.service;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import com.hjc.scripttool.activity.MonkeyAcitivity;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.ShellUtils;
import com.hjc.util.Util;

import java.io.IOException;

/**
 * Created by hujiachun684 on 16/5/3.
 */
public class MonkeyService extends Service {
    public String before_activity = "", after_activity = "", command = "";
    private int same = 0, account = 0;
    private Handler acticity_handler = new Handler();
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        command = Settings.getDefaultSharedPreferences(getApplicationContext()).getString(Settings.KEY_COMMAND, "");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
//        registerReceiver(new ScreenActionReceiver(), filter);

        acticity_handler.postDelayed(acitivitytask, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable acitivitytask = new Runnable() {
        @Override
        public void run() {

            backtask();
            if (Settings.getDefaultSharedPreferences(getApplicationContext()).getBoolean("monkey", true)) {
                activitytask();
                if(command.contains("com.pingan.lifeinsurance")){
                    if (same > 30) {

                        sendBroadcast(new Intent().setAction("st.ANR"));
//                        account++;
                        try {
                            int pid = Util.getPid("com.android.commands.monkey");
                            ShellUtils.execCommand("kill " + pid, true);
                            ShellUtils.execCommand("am force-stop com.pingan.lifeinsurance ", true);
                            Thread.sleep(1);
                            Log.e("scripttool", "APP restart pid = " + pid + " and same = " + same + " run " + command);

                            same = 0;
                            if (Settings.getDefaultSharedPreferences(getApplicationContext()).getBoolean("monkey", true)) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ShellUtils.execCommand("am start com.pingan.lifeinsurance/.initialize.activity.LauncherActivity", true);
                                        try {
                                            Thread.sleep(60000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        ShellUtils.execCommand(command, true);
                                    }
                                }).start();

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        same = 0;
                    }
                }

            }

            acticity_handler.postDelayed(this, 10000);
        }
    };


    private void activitytask() {
        after_activity = getRunningActivityName();
//        Log.e("scripttool", "same = " + same + "  before_activity = " + before_activity + "  after_activity = " +after_activity);
        if (before_activity.equals(after_activity)) {
            same++;
        } else {
            same = 0;
        }
        before_activity = after_activity;
        Settings.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("same", same).commit();
    }


    private String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName runningActivity = activityManager.getRunningTasks(1).get(0).topActivity;
        return runningActivity.getPackageName() + "->" + runningActivity.getClassName();
    }

    private void backtask() {
        if (getRunningActivityName().equals("com.pingan.lifeinsurance->cn.jk.padoctor.CordovaApp")

                || getRunningActivityName().equals("com.pingan.lifeinsurance->com.pingan.lifeinsurance.activity.WebviewActivity")
                || getRunningActivityName().equals("com.pingan.lifeinsurance->com.pingan.anydoor.hybrid.activity.CacheableWebViewActivity")
                || getRunningActivityName().equals("com.pingan.lifeinsurance->com.pingan.lifeinsurance.activity.mine.MineSettingActivity")

                ) {
            Log.e("scripttool", "i'm back");

            ShellUtils.execCommand("input keyevent 4", true);
        }
    }


    @Override
    public void onDestroy() {


        Log.e(Constants.TAG, "MonkeyService onDestroy");
        acticity_handler.removeCallbacks(acitivitytask);

        super.onDestroy();
    }


    public class ScreenActionReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                Log.e(Constants.TAG, "屏幕解锁广播...");


            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Log.e(Constants.TAG, "屏幕加锁广播...");
                PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
                PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
                mWakelock.acquire();
                mWakelock.release();

                KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
                keyguardLock.disableKeyguard();
            }
        }


    }

}