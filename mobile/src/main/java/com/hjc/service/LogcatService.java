package com.hjc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.io.IOException;

import android.app.ILogactService;
import android.util.Log;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Command;
import com.hjc.util.Constants;
import com.hjc.util.ShellUtils;

/**
 * Created by hujiachun on 15/12/15.
 */
public class LogcatService extends Service{
    private IBinder binder = new LogCatBinder();
    String resultpath ,logString = null;
    public Intent intent;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            logString =(String) msg.obj;
            if(logString != null){
                sendBroadcast(new Intent().putExtra("log", logString).setAction("sk.action.WRITELOG"));
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         this.intent = intent;
        return super.onStartCommand(intent, flags, startId);
    }

    public void logcat() throws IOException {
        resultpath =  Settings.getDefaultSharedPreferences(getApplicationContext()).getString(Settings.KEY_LOGCAT_PATH, "");
        Log.e(Constants.TAG, resultpath);
                String[] commands = {"logcat -c" ,"logcat -v time *:E "
                        + " >>" + resultpath + "/logcat.txt", };
        Log.e(Constants.TAG, "logcat 已开始");
        ShellUtils.CommandResult re = ShellUtils.execCommand(commands, true);

        Log.e(Constants.TAG, "logcat errorMsg->" + re.errorMsg);
        Log.e(Constants.TAG, "logcat successMsg->" +re.successMsg);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private final class LogCatBinder extends ILogactService.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void startlogcat() throws RemoteException {

            Runnable log = new Runnable() {
                @Override
                public void run() {
                    try {
                        logcat();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread logcatThread = new Thread(log);
            logcatThread.start();
            Log.e(Constants.TAG, "logcat name : " + logcatThread.getName());


        }
    }

}
