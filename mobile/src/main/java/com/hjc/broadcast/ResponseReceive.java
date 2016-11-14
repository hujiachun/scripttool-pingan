package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by hujiachun on 16/9/6.
 */

public class ResponseReceive extends BroadcastReceiver {
    private SharedPreferences preferences;
    private String name, response;

    @Override
    public void onReceive(Context context, Intent intent) {
        name = intent.getStringExtra(Constants.ACTION);
        response = intent.getStringExtra(Constants.TIME);
        Log.e(Constants.TAG, "接收 " + name + ":" + response);
        preferences = Settings.getDefaultSharedPreferences(context);
        String time = preferences.getString(Settings.KEY_TIME, "");
        String pak = preferences.getString(Settings.KEY_PACKAGE, "").split(":")[0];
        String path = "/sdcard/Result/performance/" + pak + "_" + time;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File result = new File(path + "/response.xml");
        FileUtil.removeLineFromFile(path + "/response.xml", name);
        try {
            FileOutputStream fos = new FileOutputStream(result, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(name + Constants.COMMA + response);
            bw.write("\n");
            bw.flush();
            bw.close();
            osw.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
