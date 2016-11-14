package com.hjc.util;

import android.app.IActivityController;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



public class ActivityController extends IActivityController.Stub {

    public static final String CRASH_TIME = "CRASH_TIME";

    public static final String CRASH_MSG = "CRASH_MSG";



    public File exec= new File("/sdcard/" + "exec.txt");

    public Context context;



    @Override
    public boolean activityStarting(Intent intent, String pkg) {
        Log.e(Constants.TAG, "activityStarting");
        return true;
    }

    @Override
    public boolean activityResuming(String pkg) {
        Log.e(Constants.TAG, "activityResuming");
        return true;
    }

    @Override
    public int appEarlyNotResponding(String processName, int pid, String annotation) {
        Log.e(Constants.TAG, "appEarlyNotResponding");
        return 0;
    }

    @Override
    public boolean appCrashed(String processName, int pid, String shortMsg,
                              String longMsg, long timeMillis, String stackTrace) {
        String logMsg = "Send error[crash] to scripttool." + "\n"
                + "// CRASH: " + processName + " (pid " + pid + ")" + "\n"
                + "// Short Msg: " + shortMsg + "\n"
                + "// Long Msg: " + longMsg + "\n"
                + "// Build Label: " + Build.FINGERPRINT + "\n"
                + "// Build Changelist: " + Build.VERSION.INCREMENTAL + "\n"
                + "// Build Time: " + Build.TIME + "\n"
                + "// " + stackTrace.replace("\n", "\n// ");

        try {

            Writer.writeLine(this.exec, logMsg, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTime = sdf.format(now);
        Intent intent = new Intent("sk.action.CRASH");
        Bundle msg = new Bundle();
        msg.putString(CRASH_TIME, nowTime);
        msg.putString(CRASH_MSG, stackTrace.replace("\n", "\n// "));
        intent.putExtras(msg);
        context.sendBroadcast(intent);

        return false;
    }

    @Override
    public int appNotResponding(String processName, int pid, String processStats) {

        String logMsg = "Send error[anr] to scripttool." + "\n"
                + "// NOT RESPONDING: " + processName + " (pid " + pid + ")" + "\n"
                + processStats;

        try {

            Writer.writeLine(this.exec, logMsg, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTime = sdf.format(now);
        Intent intent = new Intent("sk.action.CRASH");
        Bundle msg = new Bundle();
        msg.putString(CRASH_TIME, nowTime);
        msg.putString(CRASH_MSG, "NOT RESPONDING:" + processName + " (pid " + pid + ")" + "-->" + processStats);
        intent.putExtras(msg);
        context.sendBroadcast(intent);
        return 0;
    }

    @Override
    public int systemNotResponding(String msg) throws RemoteException {
        String logMsg = "Send error[snr] to scripttool." + "\n"
                + "// WATCHDOG: " + msg;
        try {

            Writer.writeLine(this.exec, logMsg, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}

