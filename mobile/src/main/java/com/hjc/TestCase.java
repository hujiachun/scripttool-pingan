package com.hjc;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hjc.performance.report.PerformanceReportInfo;
import com.hjc.performance.report.UtilJson;
import com.hjc.util.Constants;
import com.hjc.util.Util;
import com.hjc.util.Writer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hujiachun on 15/11/2.
 */
public class TestCase extends InstrumentationTestCase {


    public void test001() throws IOException {
//        File file=new File("/sdcard/bbb");
//        file.mkdirs();
//        for(int i=1;i<10;i++){
//            PerformanceReportInfo info = new PerformanceReportInfo();
//            info.setCpu("18");
//            info.setIndex(i+"");
//            info.setMem("200");
//            info.setTraffic("300");
//            if(i==1){
//                Writer.writeLine(new File(file.getAbsolutePath()+"/report.json"), "[", true);
//
//            }
//            else  Writer.writeLine(new File(file.getAbsolutePath()+"/report.json"), ",", true);
//
//            Writer.writeLine(new File(file.getAbsolutePath()+"/report.json"), info.toJsonString(), true);
//            if(i==9){
//                Writer.writeLine(new File(file.getAbsolutePath()+"/report.json"), "]", true);
//            }
//
//        }
        ArrayList<String> fpsLine = Util.readText("/sdcard/Result/performance/平安金管家_2016-10-20-19-50-29/fps.xml", getInstrumentation().getContext());
        for (String line : fpsLine) {
            Log.e(Constants.TAG, line);
        }


    }






}