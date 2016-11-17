package com.hjc.util;

import android.app.ActivityManager;
import android.app.ILogactService;
import android.app.ITimerService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.hjc.scriptutil.tools.Settings;
import com.hjc.service.LogcatService;
import com.hjc.service.StartLogService1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujiachun on 15/11/3.
 */
public class Util {
    public static ILogactService iLogactService;
    public static ITimerService iTimerService;

    /**
     * 多个进程下
     *
     * @param context
     * @param packagename
     * @return
     * @throws IOException
     */
    public static int[] getMorePid(Context context, String packagename) throws IOException {
        int[] pids;
        int i = 0;
        if (Integer.parseInt(android.os.Build.VERSION.RELEASE.substring(0, 1)) < 5) {//android 5.0后不支持
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> process = am.getRunningAppProcesses();//所有进程
            pids = new int[process.size()];

            for (ActivityManager.RunningAppProcessInfo pro : process) {
                if (pro.processName.contains(packagename)) {

                    pids[i] = pro.pid;
                    i++;
                }
            }
        } else {

            List<ProcessManager.Process> apps = ProcessManager.getRunningApps();
            pids = new int[apps.size()];
            for (ProcessManager.Process app : apps) {
                if (app.name.contains(packagename)) {

                    pids[i] = app.pid;
                    i++;
                }

            }
        }
        return pids;
    }


    /**
     * 多个进程下 取单一
     *
     * @param context
     * @param packagename
     * @return
     * @throws IOException
     */
    public static int getSinglePid(Context context, String packagename) throws IOException {
        int pid = 0;
        int version = Integer.parseInt(Build.VERSION.RELEASE.substring(0, 1));
        if (version < 5) {//android 5.0后不支持,只能得到本进程

            Log.e(Constants.TAG, "ActivityManager");
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> process = am.getRunningAppProcesses();


            for (ActivityManager.RunningAppProcessInfo pro : process) {

                if (pro.processName.equals(packagename)) {
                    pid = pro.pid;

                }
            }
        } else if (version == 7) {

            //7.0 权限问题,不能获取到其它进程
        } else {
            Log.e(Constants.TAG, "ProcessManager");
            List<ProcessManager.Process> apps = ProcessManager.getRunningApps();

            for (ProcessManager.Process app : apps) {
                if (app.name.equals(packagename)) {

                    pid = app.pid;

                }

            }
        }
        return pid;
    }


    public static int getPid(String tag) throws IOException {
        Process p;
        p = Runtime.getRuntime().exec("ps ");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            if (line.contains(tag)) {

                if (line.split("\\s")[0].equals("shell")) {
                    return Integer.parseInt(line.split("\\s")[5]);
                } else if (line.split("\\s")[0].equals("system")) {
                    return Integer.parseInt(line.split("\\s")[4]);
                } else if (line.split("\\s")[0].contains("u")) {
                    return Integer.parseInt(line.split("\\s+")[1]);
                } else {
                    return Integer.parseInt(line.split("\\s")[6]);
                }
            }
        }
        bufferedReader.close();
        p.destroy();
        return 0;
    }

    public static int getUid(String tag) throws IOException {
        Process p;
        p = Runtime.getRuntime().exec("ps ");
        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = bufferedReader2.readLine()) != null) {
            if (line.contains(tag)) {
                return Integer.parseInt(line.split("\\s+")[0].split("a")[1]) + 10000;
            }
        }

        bufferedReader2.close();
        p.destroy();
        return 0;
    }


    public static String getBatteryUid(String tag) throws IOException {
        Process p;
        p = Runtime.getRuntime().exec("ps ");
        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = bufferedReader2.readLine()) != null) {
            if (line.contains(tag)) {
                String uidStr = line.split("\\s+")[0];
                return uidStr.split("_")[0] + uidStr.split("_")[1];
            }
        }

        bufferedReader2.close();
        p.destroy();
        return null;
    }


    public static void setSystemIme() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("ime list -s");
        p.waitFor();


        String command = "";
        BufferedReader buff = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = buff.readLine()) != null) {
            if (!line.equals("com.hjc.scripttool/com.hjc.service.Utf7ImeService")) {
                command = line;

                break;
            }

        }
        buff.close();
        p.destroy();
        ShellUtils.execCommand("ime set " + command + " selected", true);
    }


    public static void setUtf7Ime() {
        ShellUtils.execCommand("ime set com.hjc.scripttool/com.hjc.service.Utf7ImeService selected", true);
    }

    /**
     * upgrade app to get root permission
     *
     * @return is root successfully
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int existValue = process.waitFor();
            if (existValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "upgradeRootPermission exception=" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
    }


    /**
     * 获取logcat pid
     *
     * @param tag
     * @return
     * @throws IOException
     */
    public static ArrayList getlogPid(String tag) throws IOException {
        ArrayList<Integer> pidArray = new ArrayList<>();
        Process p;
        p = Runtime.getRuntime().exec("ps " + tag);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            if (line.contains(tag)) {
                Log.e(Constants.TAG, line);
                if (line.split("\\s")[0].equals("root")) {
                    if ((line.split("root      ")[1].split("  ")[0]).contains(" ")) {
                        pidArray.add(Integer.parseInt(line.split("root      ")[1].split("  ")[0].split(" ")[0]));
                    } else {
                        pidArray.add(Integer.parseInt(line.split("root      ")[1].split("  ")[0]));
                    }
                }

            }

        }
        bufferedReader.close();
        p.destroy();
        return pidArray;
    }

    public static void createResult(File file, BufferedReader reader) throws IOException {

        File fi = new File(file.getCanonicalFile() + "/log.txt");
        FileOutputStream fos = new FileOutputStream(fi, true);
        String read;
        while ((read = reader.readLine()) != null) {
            fos.write(read.getBytes());
            fos.write("\n".getBytes());//换行
        }
        fos.close();
        reader.close();


    }


    public static ArrayList<String> getPackage() throws IOException, InterruptedException {

        ArrayList<String> packageStr = new ArrayList<>();
        BufferedReader reader = ShellUtils.execCcommand("pm list packages -u -3");

        String line;
        while ((line = reader.readLine()) != null) {
            String packageNameStr = line.split(":")[1];
            packageStr.add(packageNameStr);

        }
        reader.close();

        return packageStr;

    }

    /**
     * Csv
     *
     * @param filePath
     */
    public static ArrayList<String> readCsv(String filePath, Context context) throws IOException {
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> date = new ArrayList<>();
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            try {
                read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                int number = 0;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (number > 5) {
                        date.add(lineTxt);
                    }
                    number++;
                }
                read.close();
                bufferedReader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (read != null) {
                    read.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }

        } else {
            Toast.makeText(context, "result not find", Toast.LENGTH_SHORT).show();
        }
        return date;
    }


    public static String getDescriptionLine(String filePath, Context context) throws IOException {
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        String desLine = "";
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            try {
                read = new InputStreamReader(new FileInputStream(file), "GBK");
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                int number = 0;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (number == 1) {
                        desLine = lineTxt;
                        break;
                    }
                    number++;
                }
                read.close();
                bufferedReader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (read != null) {
                    read.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }

        } else {
            Toast.makeText(context, "result not find", Toast.LENGTH_SHORT).show();
        }
        return desLine;
    }

    /**
     * 读取text结果
     *
     * @param filePath
     */
    public static ArrayList<String> readText(String filePath, Context context) {
        ArrayList<String> lines = new ArrayList<String>();
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {

                    lines.add(lineTxt);
                }
                read.close();
                bufferedReader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, "result not find", Toast.LENGTH_SHORT).show();
        }
        return lines;
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 返回桌面
     *
     * @param context
     */
    public static void pressHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);

    }


    /**
     * 启动logcat服务
     */
    public static void startLogcat(Context context, File file) {
//        SharedPreferences log = context.getSharedPreferences("logcatpath", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor logeditor = log.edit();
//        logeditor.clear();
//        logeditor.putString("resultpath", file.getAbsolutePath());
//        logeditor.commit();

        Settings.getDefaultSharedPreferences(context).edit().putString(Settings.KEY_LOGCAT_PATH, file.getAbsolutePath()).commit();
        logConnection conn = new logConnection();
        Intent intent = new Intent(context, LogcatService.class);
        context.bindService(intent, conn, context.BIND_AUTO_CREATE);

    }


    public static void startLog(Context context, int time) {
        TimerConnection conn = new TimerConnection();
        Intent intent = new Intent(context, StartLogService1.class);
        intent.putExtra(Constants.TIME, time);
        context.bindService(intent, conn, context.BIND_AUTO_CREATE);
    }


    /**
     * 停止logCat
     *
     * @param context
     */
    public static void stopLogCat(Context context) {

        try {
            ArrayList<Integer> pids = Util.getlogPid(Constants.LOGCAT);
//            Log.e(Constants.TAG, "logcat pid= " + pid);
            if (pids != null) {
                for (int pid : pids) {
                    ShellUtils.execCommand("kill " + pid, true);
                    Log.e(Constants.TAG, "logcat " + pid + " 已结束");
                }

                Settings.getDefaultSharedPreferences(context).edit().putBoolean(Settings.KEY_LOGCAT_STATE, false).commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static final class logConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(Constants.TAG, "logcat 已准备");
            iLogactService = ILogactService.Stub.asInterface(iBinder);
            try {
                iLogactService.startlogcat();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    private static final class TimerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(Constants.TAG, "start 已准备");
            iTimerService = ITimerService.Stub.asInterface(iBinder);
            try {
                iTimerService.start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     *  
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e(Constants.TAG, serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    public static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    public static String getTracesTime() {
        String back = "";
        File traces = new File("data/anr/traces.txt");
        String[] cs = {"cd data/anr", "ls -l"};
        if (traces.exists()) {
            ShellUtils.CommandResult result = ShellUtils.execCommand(cs, true);
            back = result.successMsg.split("\\s+")[4] + " " + result.successMsg.split("\\s+")[5];
            Log.e(Constants.TAG, back);
            return back;
        } else return "";

    }


}
