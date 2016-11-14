package com.hjc.scriptutil.html;

import com.hjc.util.Constants;

import java.util.HashMap;

/**
 * Created by hujiachun on 16/9/9.
 */

public class MonkeyReportInfo {

    private HashMap<String, String> info;
    private String $start;
    private String $end;
    private String $time;
    private String $project;
    private String $device;
    private String $anr;
    private String $crash;

    public MonkeyReportInfo(String start, String end, String time, String project,
                            String device, String anr, String crash) {
        this.$start = start;
        this.$end = end;
        this.$time = time;
        this.$project = project;
        this.$device = device;
        this.$anr = anr;
        this.$crash = crash;

        info = new HashMap();
        info.put(Constants.MONKEY_START, this.$start);
        info.put(Constants.MONKEY_END, this.$end);
        info.put(Constants.MONKEY_TIME, this.$time);
        info.put(Constants.MONKEY_PROJECT, this.$project);
        info.put(Constants.MONKEY_DEVICES, this.$device);
        info.put(Constants.MONKEY_ANR, this.$anr);
        info.put(Constants.MONKEY_CRASH, this.$crash);
    }


    public String getInfo(String type){

        return info.get(type);
    }
}
