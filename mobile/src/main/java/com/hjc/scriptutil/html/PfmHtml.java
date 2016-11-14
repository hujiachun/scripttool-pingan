package com.hjc.scriptutil.html;

import android.content.Context;

import com.hjc.scriptutil.tools.PerformaceData;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by hujiachun on 16/9/5.
 */

public class PfmHtml {

    private String path;
    private Context context;
    private PerformaceData pdata;
    private ArrayList<Float> memFloatList;
    private ArrayList<Double> cpuDoubleList;
    private ArrayList<String> count;
    private ArrayList<Double> trafficList;


    public PfmHtml(String path, Context context) throws IOException {
        this.path = path;
        this.context = context;
        this.pdata = new PerformaceData(this.path, this.context);
        this.memFloatList = pdata.memFloatList;
        this.cpuDoubleList = pdata.cpuDoubleList;
        this.trafficList = pdata.getRealTrafficList();
    }



    public double getAverageCpu(){
        return this.pdata.getAverageCpu();
    }

    public String getAverageMem(){
        DecimalFormat dFormat = new DecimalFormat("0.00");
        return dFormat.format(this.pdata.getAverageMem());
    }


    public double getUseTraffic(){
        return this.pdata.getTraffic();
    }

    /**
     * 获取内存
     * @return
     * @throws IOException
     */
    public String getMemData() throws IOException {

        return getData(this.memFloatList);
    }

    /**
     * 获取cpu数据
     * @return
     */
    public String getCpuData(){
        return getData(this.cpuDoubleList);
    }


    /**
     * 获取流量数据
     */
    public String getTraffic(){
        return getData(this.trafficList);
    }


    /**
     * 获取个数
     * @return
     */
    public String getCount(){
        count = new ArrayList<>();
        for (int i=1; i<=this.memFloatList.size(); i++){
            count.add(String.valueOf(i));
        }
        return getData(count);
    }


    private String getData(ArrayList list){
        String data = "data: [";
        for (int i=0; i < list.size(); i++){
            if(i != list.size() - 1){
                data += "'" + list.get(i) + "',";
            }
            else data += "'" + list.get(i) +"']";
        }
        return data;
    }
}
