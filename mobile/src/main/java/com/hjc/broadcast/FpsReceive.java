package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

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
 * Created by hujiachun on 16/10/13.
 */

public class FpsReceive extends BroadcastReceiver {
    private SharedPreferences preferences;
    private String caseName, fpsList;

    @Override
    public void onReceive(Context context, Intent intent) {
        caseName = intent.getStringExtra("casename");
        fpsList = intent.getStringExtra("fpslist");
        Log.e(Constants.TAG, "接收 " + caseName + ":" + fpsList);
        Toast.makeText(context, caseName + ":" + fpsList, Toast.LENGTH_SHORT).show();

        preferences = Settings.getDefaultSharedPreferences(context);
        String time = preferences.getString(Settings.KEY_TIME, "");
        String pak = preferences.getString(Settings.KEY_PACKAGE, "").split(":")[0];
        String path = "/sdcard/Result/performance/" + pak + "_" + time;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File result = new File(path + "/fps.txt");
        FileUtil.removeLineFromFile(path + "/fps.txt", caseName);
        try {
            FileOutputStream fos = new FileOutputStream(result, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(caseName + ":" + fpsList);
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
