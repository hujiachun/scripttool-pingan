package com.hjc.performance.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by hujiachun on 16/9/23.
 */

@JSONType(orders = {"index", "mem", "cpu", "traffic"}, asm = false)
public class PerformanceReportInfo {


    @JSONField(name = "index")
    private String index = "";

    @JSONField(name = "mem")
    private String mem = "";

    @JSONField(name = "cpu")
    private String cpu = "";

    @JSONField(name = "traffic")
    private String traffic = "";


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getMem() {
        return mem;
    }

    public void setMem(String mem) {
        this.mem = mem;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }


    public String toJsonString(){

        return JSON.toJSONString(this, SerializerFeature.PrettyFormat).toString();
    }
}
