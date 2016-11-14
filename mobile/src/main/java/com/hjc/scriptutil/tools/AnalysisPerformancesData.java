package com.hjc.scriptutil.tools;

import android.content.Context;

import com.hjc.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by hujiachun on 16/9/27.
 */

public class AnalysisPerformancesData {
    private ArrayList<PerformanceCaseInfo> items = new ArrayList<>();
    public ArrayList<String> case_list;
    public HashMap<String, Float> max_mem, average_mem;
    public HashMap<String, Double> max_cpu, average_cpu, traffic;
    public HashMap<String, HashMap> max_mem_hash, average_mem_hash, max_cpu_hash, average_cpu_hash, traffic_hash;
    public HashMap<String, PerformaceData> performaceData_map;
    /**
     *
     * @param path 性能数据的路径
     * @param caseList 性能数据文件名,也就是case名
     */
    public AnalysisPerformancesData(String path, ArrayList caseList, Context context) {
        performaceData_map = new HashMap<>();
        max_mem_hash = new HashMap<>();
        average_mem_hash = new HashMap<>();
        max_cpu_hash = new HashMap<>();
        average_cpu_hash = new HashMap<>();
        traffic_hash = new HashMap<>();
        this.case_list = caseList;

        for(int i = 0; i < case_list.size(); i++){
            if(case_list.get(i).contains(Constants.CSV)){//过滤csv文件
                String case_name = case_list.get(i).split(Constants.CSV)[0];
                max_mem = new HashMap();
                average_mem = new HashMap();
                max_cpu = new HashMap();
                average_cpu = new HashMap();
                traffic = new HashMap<>();
                try {
                    PerformaceData pd = new PerformaceData(path + case_list.get(i), context);//解析data
                    performaceData_map.put(case_name, pd);
                    max_mem.put(Constants.CASE_NAME, pd.getMaxMem());
                    average_mem.put(Constants.CASE_NAME, pd.getAverageMem());
                    max_cpu.put(Constants.CASE_NAME, pd.getMaxCpu());
                    average_cpu.put(Constants.CASE_NAME, pd.getAverageCpu());
                    traffic.put(Constants.CASE_NAME, pd.getTraffic());
                    max_mem_hash.put(case_name, max_mem);//case 对应相应的值
                    average_mem_hash.put(case_name, average_mem);
                    max_cpu_hash.put(case_name, max_cpu);//case 对应相应的值
                    average_cpu_hash.put(case_name, average_cpu);
                    traffic_hash.put(case_name, traffic);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void init() {

        for(int i = 0; i < case_list.size(); i++){
            if(case_list.get(i).endsWith(Constants.CSV)){
                PerformanceCaseInfo performanceCaseInfo = new PerformanceCaseInfo();
                String case_str = case_list.get(i).split(Constants.CSV)[0];
                performanceCaseInfo.setPerformaceData(performaceData_map.get(case_str));//取出对应case的数据
                performanceCaseInfo.setCaseName(case_str);//获取用例名称
                performanceCaseInfo.setMaxMem((Float) max_mem_hash.get(case_str).get(Constants.CASE_NAME));
                performanceCaseInfo.setAverageMem((Float) average_mem_hash.get(case_str).get(Constants.CASE_NAME));
                performanceCaseInfo.setMaxCpu((Double) max_cpu_hash.get(case_str).get(Constants.CASE_NAME));
                performanceCaseInfo.setAverageCpu((Double) average_cpu_hash.get(case_str).get(Constants.CASE_NAME));
                performanceCaseInfo.setTraffic((Double) traffic_hash.get(case_str).get(Constants.CASE_NAME));
                items.add(performanceCaseInfo);
            }

        }
    }

    /**
     * 返回性能信息
     */
    public ArrayList<PerformanceCaseInfo> getItems(){
        init();
        return items;

    }
}
