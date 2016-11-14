package com.hjc.scripttool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hjc.scripttool.R;
import com.hjc.scriptutil.html.HtmlMonkeyReport;
import com.hjc.util.Constants;
import com.hjc.util.Copier;
import com.hjc.util.ShellUtils;
import com.hjc.util.UploadUtil;
import com.hjc.util.Writer;
import com.hjc.util.ZipCompressor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import com.android.internal.os.PowerProfile;


/**
 * Created by hujiachun on 15/11/5.
 */
public class MainActivity extends Activity {
    public String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
//crash        UploadUtil.uploadFile(new File("/sdcard/monkeylog.txt"), "http://11.240.196.29:8000/myweb/uploadFile");


        File report = new File(getFilesDir().getPath() + "/report.zip");
        if(!report.exists()){
            try {
                Copier.copyFile(getResources().openRawResource(R.raw.report), new FileOutputStream(report));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ZipCompressor.decompress(report);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Writer.chmodEverythingBySystem(getFilesDir().getPath() + "/report/");

        }


    }

    public void gotoUiautomator(View v) throws Exception {

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), UiautomatorActivity.class);
        startActivity(intent);



    }

    public void gotoMonkey(View v) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    File css = new File(getFilesDir().getPath() + "/report.zip");
                    Copier.copyFile(new FileInputStream(css), new FileOutputStream(new File("/sdcard/report.zip")));
                    HtmlMonkeyReport html = new HtmlMonkeyReport("1", "1", "1", "1", "1", "1", "1");
                    html.writeToHtml(getApplicationContext(), "/sdcard");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MonkeyAcitivity.class);
        startActivity(intent);

    }


    public void gotosetting(View v) {
//        PowerProfile power = new PowerProfile(getApplicationContext());
//        Log.e(Constants.TAG, "getBatteryCapacity:" + power.getBatteryCapacity());

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), SettingActivity.class);
        startActivity(intent);

    }


    public Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            str = msg.getData().getString("str");
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    };


//    private void test(){
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "tag");
//        wl.acquire();
//        wl.release();
//    }


//    private void setActivityController() {
//        try {
//            Class<?> cActivityManagerNative = Class
//                    .forName("android.app.ActivityManagerNative");
//            Method mGetDefault = cActivityManagerNative.getMethod("getDefault",
//                    null);
//            Object oActivityManagerNative = mGetDefault.invoke(null, null);
//            Class<?> i = Class.forName("android.app.IActivityController$Stub");
//
//            Method mSetActivityController = cActivityManagerNative.getMethod(
//                    "setActivityController",
//                    Class.forName("android.app.IActivityController"));
//            mSetActivityController.invoke(oActivityManagerNative,
//                    new ActivityController());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//            Log.e(Constants.TAG, e.toString());
//        }
//    }



//    private void setActivityController(ActivityController controller) throws InvocationTargetException, IllegalAccessException {
//        Object oActivityManagerNative = null;
//        Method mSetActivityController = null;
//           try {
//               Class<?> cActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
//               Log.e(Constants.TAG, "1 " + cActivityManagerNative.getName());
//               Method mGetDefault = cActivityManagerNative.getMethod("getDefault");
//               Log.e(Constants.TAG, "2 " + mGetDefault.getName());
//               oActivityManagerNative = mGetDefault.invoke(null);
//               Log.e(Constants.TAG, "3 " + oActivityManagerNative.getClass().getName());
//
//               mSetActivityController = cActivityManagerNative.getMethod("setActivityController", Class.forName("android.app.IActivityController"));
//               Log.e(Constants.TAG, "4 " + mSetActivityController.getName());
//
//           } catch (Exception e){
//               Log.e(Constants.TAG, e.toString());
//           }
//
//            mSetActivityController.invoke(oActivityManagerNative, controller);
//
//            Log.e(Constants.TAG, "5");
//
//    }



    public void performance(View v) throws InterruptedException, IOException {

        Intent intent = new Intent();
        File file = new File(Constants.PERFORMANCE_PATH);
        String[] list = file.list();
        if (list != null) {
            List<String> hisList = new ArrayList<>(Arrays.asList(list));
            intent.putStringArrayListExtra(Constants.PERFORMANCE_LIST, (ArrayList<String>) hisList);
            intent.setClass(getApplicationContext(), PerformanceHistoryAcitity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_SHORT).show();
        }
    }

}
