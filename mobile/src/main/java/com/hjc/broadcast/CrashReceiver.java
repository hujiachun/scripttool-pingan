package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.util.Log;

import com.hjc.util.ActivityController;
import com.hjc.util.Constants;
import java.io.File;
import java.io.FileWriter;


public class CrashReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(Constants.TAG, "Caught app crash.");

        crashResultWrite(intent);
    }

    private void crashResultWrite(Intent intent) {
        File file = new File("/sdcard/" + "crash.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fis = new FileWriter(file, true);
            intent.getStringExtra(ActivityController.CRASH_TIME);
            fis.write(intent.getStringExtra(ActivityController.CRASH_TIME));
            fis.write(intent.getStringExtra(ActivityController.CRASH_MSG));
            fis.flush();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
