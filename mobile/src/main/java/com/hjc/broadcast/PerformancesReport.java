package com.hjc.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjc.performance.Cpu;
import com.hjc.performance.MemoryInfo;
import com.hjc.scriptutil.tools.AnalysisPerformancesData;
import com.hjc.scriptutil.tools.Devices;
import com.hjc.scriptutil.tools.PerformanceCaseInfo;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.TimeUtil;
import com.hjc.util.UploadUtil;
import com.hjc.util.Util;
import com.hjc.util.Writer;
import java.io.File;
import java.util.ArrayList;


/**
 * Created by hujiachun on 16/9/26.
 */

public class PerformancesReport extends BroadcastReceiver {
    //    String url = "http://11.240.196.29:8000/myweb/uploadFile";
    private String url;
    private String HTTP = "http://";
    private String SUFFIX = "/performance/uploadFile";
    private File jsonFile;
    private Context context;
    private SharedPreferences preferences;
    private String resultFilePath;
    private String TAG = "performance";//根据这个过滤
    private ArrayList<String> performanceCase = new ArrayList<>();
    private String parameterData, msg;
    private MemoryInfo memoryInfo;
    private String uid, appVersion, reportName;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        preferences = Settings.getDefaultSharedPreferences(context);
        resultFilePath = Constants.PERFORMANCE_PATH + preferences.getString(Settings.KEY_PACKAGE, "").split(":")[0]
                + "_" + preferences.getString(Settings.KEY_TIME, Constants.NA);
        Log.e(Constants.TAG, resultFilePath);
        memoryInfo = new MemoryInfo();
        url = HTTP + preferences.getString(Settings.KEY_SERVER, "11.240.193.204:8080") + SUFFIX;
        jsonFile = new File(resultFilePath + "/json");
        jsonFile.mkdirs();
        File file = new File(resultFilePath);
        String[] lists = file.list();
        for (String str : lists) {
            if (str.startsWith(TAG)) {//添加性能数据文件
                Log.e(Constants.TAG, "过滤出:" + str);
                performanceCase.add(str);
            }
        }

        AnalysisPerformancesData data = new AnalysisPerformancesData(resultFilePath + "/", performanceCase, context);
        ArrayList<PerformanceCaseInfo> items = data.getItems();
        final JSONObject performanceJson = new JSONObject();
        for (PerformanceCaseInfo info : items) {
            JSONObject obj_mem = new JSONObject();
            JSONObject obj_des = new JSONObject();
            JSONObject obj_cpu = new JSONObject();
            JSONObject obj_traffic = new JSONObject();
            JSONObject obj_index = new JSONObject();
            JSONObject obj_ac = new JSONObject();
            JSONObject obj_am = new JSONObject();
            JSONObject obj_tt = new JSONObject();
//            obj_mem.put("startTime", info.getPerformaceData().getStartTime().toString());
            obj_des.put("description", info.getPerformaceData().getDescription().toString());
            obj_mem.put("memory", info.getPerformaceData().getMemFloatList().toString());
            obj_cpu.put("cpu", info.getPerformaceData().getCpuDoubleList().toString());
            obj_traffic.put("traffic", info.getPerformaceData().getRealTrafficList().toString());
            obj_index.put("index", info.getPerformaceData().getIndexList().toString());
            obj_ac.put("averageCpu", info.getPerformaceData().getAverageCpu());
            obj_am.put("averageMem", info.getPerformaceData().getAverageMem());
            obj_tt.put("totalTraffic", info.getPerformaceData().getTraffic());
            JSONArray list = new JSONArray();
            list.add(obj_des);
            list.add(obj_mem);
            list.add(obj_cpu);
            list.add(obj_traffic);
            list.add(obj_index);
            list.add(obj_ac);
            list.add(obj_am);
            list.add(obj_tt);
            performanceJson.put(info.getCaseName(), list);
        }
//        Log.e(Constants.TAG, performanceJson.toString());


        ArrayList<String> fpsLine = Util.readText(resultFilePath + "/fps.txt", context);
        JSONObject fpsJson = new JSONObject();
        for (String line : fpsLine) {
            String[] sp = line.split(":");
            fpsJson.put(sp[0], sp[1]);
        }
        Log.e(Constants.TAG, fpsJson.toString());
        ArrayList<String> responseLine = Util.readText(resultFilePath + "/response.xml", context);
        JSONObject responseJson = new JSONObject();
        for (String line : responseLine) {
            String[] sp = line.split(",");
            responseJson.put(sp[0], sp[1]);
        }
//        Log.e(Constants.TAG, responseJson.toString());

        Devices devices = new Devices();
        devices.setAndroidVersion(memoryInfo.getSDKVersion());
        devices.setDevicesMemory(memoryInfo.getTotalMemory());
        devices.setDevicesCpu(Cpu.getCpuNum()+ "核CPU");
        devices.setDevicesName(Build.MODEL);
        JSONObject devicesJson = new JSONObject();
        devicesJson.put("devicesName", devices.getDevicesName());
        devicesJson.put("devicesCpu", devices.getDevicesCpu());
        devicesJson.put("devicesVersion", devices.getAndroidVersion());
        devicesJson.put("devicesMemory", devices.getDevicesMemory());
        Log.e(Constants.TAG, devicesJson.toString());
        JSONObject obj_performance = new JSONObject();
        JSONObject obj_fps = new JSONObject();
        JSONObject obj_response = new JSONObject();
        JSONObject obj_devices = new JSONObject();
        JSONArray list = new JSONArray();
        obj_performance.put("performanceData", performanceJson);
        obj_fps.put("fpsData", fpsJson);
        obj_response.put("responseData", responseJson);
        obj_devices.put("devices", devicesJson);
        list.add(obj_performance);
        list.add(obj_fps);
        list.add(obj_response);
        list.add(obj_devices);
        Log.e(Constants.TAG, "最终传输:" + list.toJSONString());
        parameterData = "performanceContent=" + list.toJSONString();
//        Log.e(Constants.TAG, "传输:" + parameterData);
        appVersion = preferences.getString(Settings.KEY_VERSION, "noversion");
        reportName = appVersion + "_" + TimeUtil.getMMSS() + ".json";
        Writer.writeLine(new File(jsonFile.getAbsolutePath() + "/" + reportName), list.toString(), false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(Constants.TAG, "开始上传......");
                UploadUtil.uploadFile(new File(jsonFile.getAbsolutePath() + "/" + reportName), url);
                try {
//                    msg = UploadUtil.doPost(parameterData, url);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(Constants.TAG, "上传失败 :" + e.toString());
                }

                Log.e(Constants.TAG, "返回 :" + msg);
            }
        }).start();
    }


}
