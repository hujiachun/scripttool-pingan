package com.hjc.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.ShellUtils;

/**
 * Created by hujiachun on 2016/3/19.
 */
public class RadioService extends IntentService{


    public RadioService() {
        super("RadioService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        radioStart();
    }


    public void radioStart(){
        String resultpath = Settings.getDefaultSharedPreferences(getApplicationContext()).getString(Settings.KEY_LOGCAT_PATH, "");

        ShellUtils.execCommand("logcat -b radio -v time >> " +resultpath + "/radio.txt", true);
    }

    @Override
    public void onDestroy() {
        Log.e(Constants.TAG, "RadioService onDestroy");
        super.onDestroy();
    }
}
