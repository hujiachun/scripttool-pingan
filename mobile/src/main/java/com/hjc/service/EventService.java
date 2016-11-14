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
 * Created by hujiachun on 16/9/2.
 */

public class EventService extends IntentService{


    public EventService() {
        super("EventService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        eventStart();
    }



    public void eventStart(){
        String resultpath = Settings.getDefaultSharedPreferences(getApplicationContext()).getString(Settings.KEY_LOGCAT_PATH, "");

        ShellUtils.execCommand("logcat -b events -v time >> " + resultpath + "/events.txt", true);
    }

    @Override
    public void onDestroy() {
        Log.e(Constants.TAG, "EventService onDestroy");
        super.onDestroy();
    }
}
