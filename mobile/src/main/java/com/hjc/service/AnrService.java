package com.hjc.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.hjc.util.Constants;
import com.hjc.util.ShellUtils;

import java.io.IOException;

/**
 * Created by hujiachun684 on 16/5/24.
 */
public class AnrService extends IntentService{
    String[] commands = {"cd data/anr", "ls -l"};

    public AnrService() {
        super("AnrService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ShellUtils.CommandResult result = ShellUtils.execCommand(commands, false);
        Log.e(Constants.TAG, result.successMsg);

    }


    @Override
    public void onDestroy() {
        Log.e(Constants.TAG, "AnrService onDestroy");
        super.onDestroy();
    }
}
