package com.hjc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hjc.scriptutil.tools.ExcelUtil;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

/**
 * Created by hujiachun on 15/11/17.
 */
public class TestResult extends BroadcastReceiver {
    public Context context;
    private String name, step = "", expectation = "";
    private boolean result;
    private String packagename = "com.pingan.lifeinsurance";

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        name = intent.getStringExtra("NAME");
        result = intent.getExtras().getBoolean("RESULT");
        step = intent.getStringExtra("STEP");
        expectation = intent.getStringExtra("EXPECTATION");
        Log.e(Constants.TAG, "NAME:" + name + ", " + "STEP:" + step + ", " + "EXPECTATION:" + expectation +", " + "RESULT:" + result);

        final String resultpath = Settings.getDefaultSharedPreferences(context).getString(Settings.KEY_UIAUTOMATOR, "");

        final File screenshotFile = new File(resultpath + "/screenshot");
        if(!screenshotFile.exists()){
            screenshotFile.mkdirs();
        }



        File excelXLS = new File(resultpath + "/report.xls");
        ExcelUtil excel = new ExcelUtil(excelXLS);
        if(!excelXLS.exists()){
            Log.e(Constants.TAG, "createExcel");
            String sharedPackage = Settings.getDefaultSharedPreferences(context).getString(Settings.KEY_PACKAGE, packagename);
            if(sharedPackage.equals(packagename)){
                excel.createExcel(getAppVersionName(context, sharedPackage));
            }
            else excel.createExcel(getAppVersionName(context, sharedPackage.split(":")[1]));
        }

        try {
            excel.updateExcel(excelXLS, name, step, expectation, String.valueOf(result));
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        File resultTXT = new File(resultpath + "/result.txt");
        FileUtil.removeLineFromFile(new File(resultpath + "/result.txt").getAbsolutePath(), name);
        try {
            FileOutputStream fos = new FileOutputStream(resultTXT, true);
            fos.write(name.getBytes());
            fos.write(":".getBytes());
            fos.write((result ? "true" : "false").getBytes());
            fos.write("\n".getBytes());//换行

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent().setAction("st.take").putExtra("name", name).putExtra("result", result).putExtra("screenshotFile", screenshotFile.getAbsolutePath()));

        Toast.makeText(context, name + ":" + result, Toast.LENGTH_SHORT).show();



    }

    //获取当前版本号
    private  String getAppVersionName(Context context, String name) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(name, 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

}
