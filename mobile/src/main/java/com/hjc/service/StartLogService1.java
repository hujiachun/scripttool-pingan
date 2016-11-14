package com.hjc.service;


import android.app.ITimerService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.hjc.scripttool.report.MonkeyReport;
import com.hjc.scriptutil.mail.Email;
import com.hjc.scriptutil.mail.EmailService;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujiachun684 on 16/5/16.
 */
public class StartLogService1 extends Service{
    private IBinder binder = new TimerBinder();

    public Intent intent;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }





    @Override
    public void onDestroy() {
        Log.e("scripttool", "StartLogService1 onDestroy");
        super.onDestroy();
    }

    private final class TimerBinder extends ITimerService.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void start() throws RemoteException {

        }


    }



}
