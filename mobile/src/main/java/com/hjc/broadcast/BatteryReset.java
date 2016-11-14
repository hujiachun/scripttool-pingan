package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.ShellUtils;

/**
 * Created by hujiachun on 16/10/17.
 */

public class BatteryReset extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ShellUtils.CommandResult result = ShellUtils.execCommand("dumpsys batterystats --reset", true);
        Toast.makeText(context, result.successMsg + "请拔掉数据线", Toast.LENGTH_SHORT).show();
    }
}
