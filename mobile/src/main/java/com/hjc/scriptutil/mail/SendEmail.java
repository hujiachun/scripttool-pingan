package com.hjc.scriptutil.mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.hjc.scripttool.R;
import com.hjc.scripttool.report.MonkeyReport;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.Util;
import com.hjc.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujiachun on 16/8/10.
 */
public class SendEmail {
    private SharedPreferences preferences;
    private Context context;
    private String packageName, path, clickHistory;
    private List<String> traces_name, traces_str;
    private MonkeyReport mr;
    private int anr, crash;


    public void excute(Context context, String clickHistory) {
        this.context = context;
        this.clickHistory = clickHistory;
        preferences = Settings.getDefaultSharedPreferences(context);
        packageName = Settings.getInstance(context).getPackageName();
        if (preferences.getString(Settings.KEY_EMAIL_TO, Constants.NULL) != "") {
            Toast.makeText(context, "sending...", Toast.LENGTH_SHORT).show();
            new Thread(sendEMail).start();
        }
    }


    /**
     * @param
     */
    public void report() {
        if (mr != null) {
            mr = null;
        }
        if (clickHistory != "") {
            mr = new MonkeyReport(packageName, "", clickHistory);
            path = Constants.MONKEY_PATH + clickHistory;
        } else {
            mr = new MonkeyReport(packageName, preferences.getString(Settings.KEY_LOGCAT_PATH, ""), "");
            path = preferences.getString(Settings.KEY_LOGCAT_PATH, "");
        }
        mr.readLogcat();
        anr = mr.getAnrNumber();
        crash = mr.getCrashNumber();
        traces_name = new ArrayList();
        traces_str = new ArrayList();
        if (anr != 0) {//设置了过滤 ,anr为0不处理traces

            File file = new File(path);
            for (String list : file.list()) {
                if (list.contains("traces.txt")) {
                    if (Util.getTotalSizeOfFilesInDir(
                            new File(path + "/" + list)) / 1045576 > 5) {//文件大于5M 压缩
                        try {
                            ZipUtil.zip(path + "/" + list
                                    , path + "/" + "traces.zip");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        traces_str.add(path + "/" + "traces.zip");
                        traces_name.add("traces.zip");
                    } else {
                        traces_str.add(path + "/" + list);
                        traces_name.add(list);
                    }
                }
            }
        }
    }

    private Runnable sendEMail = new Runnable() {
        @Override
        public void run() {

            report();
            if (anr != 0 || crash != 0) {
                traces_str.add(path + "/error.txt");
                traces_name.add("error.txt");
            }

            String[] attachments = traces_str.toArray(new String[0]);
            String[] traces_names = traces_name.toArray(new String[0]);
            String runtime = new DecimalFormat("0.000").format((double) preferences.getInt(Constants.TIME, 0) / 3600);
            Email email = new Email();
            email.setAttachment(attachments);
            email.setAttachmentName(traces_names);
            email.setSubject("Monkey Report");
            email.setContent("\n" + "执行设备: " + Build.MODEL + "\n" + "系统版本: " + "Android " + Build.VERSION.RELEASE + "\n" + "执行时长：" + runtime + "小时" + "\n" + "结果路径：" + path + "\n" + "ANR : " + anr + "\n" + "CRASH : " + crash);
            email.setReceiver(preferences.getString(Settings.KEY_EMAIL_TO,
                    Constants.NULL));
            String email_cc = preferences.getString(Settings.KEY_EMAIL_CC,
                    Constants.NULL);
//            Log.e(Constants.TAG, "email_cc:" + email_cc);
            if (email_cc != "") {
                email.setCc(email_cc);
            }

            try {
                new EmailService(email).sendAttachmentEmail();

            } catch (Exception e) {
                Log.e(Constants.TAG, e.toString());
            }
        }

    };
}
