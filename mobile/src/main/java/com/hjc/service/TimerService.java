package com.hjc.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.hjc.scripttool.report.MonkeyReport;
import com.hjc.scriptutil.mail.SendEmail;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.scriptutil.mail.EmailService;
import com.hjc.scriptutil.mail.Email;
import com.hjc.util.Constants;
import com.hjc.util.Util;
import com.hjc.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujiachun on 16/1/25.
 */
public class TimerService extends IntentService {
    private int time;
    private SharedPreferences preferences;

    public TimerService() {
        super("TimerService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        time = intent.getIntExtra("time", 0);
        Log.e(Constants.TAG, "monkey测试时间=" + time + "s");

        try {
            Thread.sleep(time * 1000);
            monkeyHandler.sendMessage(new Message());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Handler monkeyHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.e(Constants.TAG, "monkey测试结束");
            sendBroadcast(new Intent().setAction("st.STOP.MONKEY"));
            sendBroadcast(new Intent().setAction("st.ANR"));
//            preferences = Settings.getDefaultSharedPreferences(getApplicationContext());
//            if(preferences.getString(Settings.KEY_EMAIL_TO, Constants.NULL) != ""){
//                Toast.makeText(getApplicationContext(), "sending...", Toast.LENGTH_SHORT).show();
//                new Thread(sendEMail).start();
//            }
            new SendEmail().excute(getApplicationContext(), "");

            super.handleMessage(msg);
        }
    };


    @Override
    public void onDestroy() {
        Log.e(Constants.TAG, "TimerService onDestroy");
        super.onDestroy();
    }
}

