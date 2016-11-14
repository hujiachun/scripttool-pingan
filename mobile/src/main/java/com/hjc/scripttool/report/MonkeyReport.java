package com.hjc.scripttool.report;

import android.util.Log;

import com.hjc.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by hujiachun684 on 16/5/11.
 */
public class MonkeyReport {

    private ArrayList<String> anr, crash;
    private File file, sendFile;
    private boolean collect_anr = false, collect_crash = false, sureCrash = false;
    private String packageName;
    private StringBuffer flag;
    private String ANR_TAG = "ANR in com.";
    private String ANR_LINE = "ActivityManager";
    private String CRASH_TAG = "FATAL EXCEPTION:";
    private String CRASH_LINE = "AndroidRuntime";


    /**
     * @param packageName
     * @param logcat      包含路径
     * @param history
     */
    public MonkeyReport(String packageName, String logcat, String history) {
        Log.e(Constants.TAG, "指定了过滤报名:" + packageName);

        this.packageName = packageName;

        if (logcat != "") {
            this.file = new File(logcat + "/logcat.txt");
            sendFile = new File(logcat);
        } else {
            this.file = new File(Constants.MONKEY_PATH + history + "/logcat.txt");
            sendFile = new File(Constants.MONKEY_PATH + history);
        }

        anr = new ArrayList<>();
        crash = new ArrayList<>();
    }


    /**
     * 读取logcat信息
     */
    public void readLogcat() {
        flag = new StringBuffer();
        try {
            Log.e(Constants.TAG, "path:" + this.file.getAbsolutePath());
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;

            File errorTXT = new File(sendFile.getAbsolutePath() + "/error.txt");
            if(errorTXT.exists()){
                Log.e(Constants.TAG, "error.txt已存在");
                errorTXT.delete();
                Log.e(Constants.TAG, "error.txt删除成功");
            }
            FileOutputStream fos = new FileOutputStream(errorTXT, true);
            line:
            while ((lineTxt = bufferedReader.readLine()) != null) {
                if (collect_anr) {
                    if (lineTxt.contains(ANR_LINE)) {//收集anr

                        fos.write(lineTxt.getBytes());
                        fos.write("\n".getBytes());
                        fos.flush();

                    } else {
                        collect_anr = false;//结束收集
                        fos.write("\n".getBytes());
                        fos.write("\n".getBytes());
                        fos.write("*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *".getBytes());
                        fos.write("\n".getBytes());
                        fos.write("\n".getBytes());
                        fos.flush();
                    }
                    continue line;
                }
                if (collect_crash) {

                    if (lineTxt.contains(CRASH_LINE)) {//收集crash
                        fos.write(lineTxt.getBytes());
                        fos.write("\n".getBytes());
                        fos.flush();
                    } else {

                        collect_crash = false;//结束收集
                        fos.write("\n".getBytes());
                        fos.write("\n".getBytes());
                        fos.write("*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *".getBytes());
                        fos.write("\n".getBytes());
                        fos.write("\n".getBytes());
                        fos.flush();
                    }
                    continue line;
                }

                ANR:
                if (lineTxt.contains(ANR_TAG)) {
                    if (!lineTxt.contains(packageName)) {//过滤包名
                        break ANR;
                    }
                    fos.write(lineTxt.getBytes());
                    fos.write("\n".getBytes());
                    fos.flush();
                    anr.add(lineTxt);
                    collect_anr = true;
                }

                //过滤逻辑,先进入异常信息,sureCrash为true,进入第二行,若包含预设置包名,写了log信息
                if (lineTxt.contains(CRASH_TAG)) {
                    sureCrash = true;
                    flag.setLength(0);//清空
                    flag.append(lineTxt);//FATAL EXCEPTION:信息放入缓存
                    continue line;//结束本次循环

                }
                if(sureCrash){
                    if (lineTxt.contains(packageName)) {//过滤包名
                        sureCrash = false;//恢复sureCrash
                        crash.add(flag.toString());
                        fos.write(flag.toString().getBytes());
                        fos.write("\n".getBytes());
                        fos.write(lineTxt.getBytes());
                        fos.write("\n".getBytes());
                        fos.flush();
                        flag.setLength(0);//清空
                        collect_crash = true;
                    }else {
                        sureCrash = false;
                        flag.setLength(0);
                    }
                }
            }

            fos.close();
            bufferedReader.close();
            read.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int getAnrNumber() {

        return anr.size();
    }

    public int getCrashNumber() {

        return crash.size();
    }


}
