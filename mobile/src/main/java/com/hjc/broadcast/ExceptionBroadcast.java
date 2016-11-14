package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.hjc.util.Constants;

/**
 * Created by hujiachun684 on 16/7/14.
 */
public class ExceptionBroadcast extends BroadcastReceiver{
    public String type;
    @Override
    public void onReceive(Context context, Intent intent) {

        type = intent.getStringExtra("type");
        if(type.equals("crash")){
            Log.e(Constants.TAG, "crash");
            Toast.makeText(context, "crash", Toast.LENGTH_LONG).show();

        }
        if(type.equals("anr")){
            Log.e(Constants.TAG, "anr");
            Toast.makeText(context, "anr", Toast.LENGTH_LONG).show();

        }
    }
}
