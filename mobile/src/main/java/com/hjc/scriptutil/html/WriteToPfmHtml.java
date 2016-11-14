package com.hjc.scriptutil.html;

import android.content.Context;
import android.util.Log;

import com.hjc.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 * Created by hujiachun on 16/9/5.
 */

public class WriteToPfmHtml {
    public static String POTAL = "potal";
    public static String POLICY = "policy";
    public static String ALS = "als";
    public static String FORTUNE = "fortune";
    public static String HEALTH = "health";

    private HashMap<String, String> count, meminfo, cpuinfo, traffic, averageCpu, averageMem, averageTraffic;

    public WriteToPfmHtml(HashMap<String, String> meminfo, HashMap<String, String>
            cpuinfo, HashMap<String, String> count, HashMap<String, String> traffic
            , HashMap<String, String> averageCpu, HashMap<String, String> averageMem
    , HashMap<String, String> averageTraffic ){
        this.count = count;
        this.meminfo = meminfo;
        this.cpuinfo = cpuinfo;
        this.traffic = traffic;
        this.averageCpu = averageCpu;
        this.averageMem = averageMem;
        this.averageTraffic = averageTraffic;
    }


    public void writeToPotal(Context context, String path) throws IOException {
        File html = new File(context.getFilesDir().getPath() + "/report/index.html");
        File report = new File(path + "/index.html");
        FileOutputStream fos = new FileOutputStream(report);
        FileReader fr = new FileReader(html);
        BufferedReader buff = new BufferedReader(fr);
        String line = "";
        String rep = "";
        while ((line=buff.readLine()) != null){

            if(line.contains("$potal_cpu")){

                rep = line.replace("$potal_cpu", this.averageCpu.get(POTAL))
                .replace("$potal_mem", this.averageMem.get(POTAL)).replace("$potal_traffic", this.averageTraffic.get(POTAL));
                fos.write(rep.getBytes()) ;
            }
            else if(line.contains("policy_cpu")){
                rep = line.replace("$policy_cpu", this.averageCpu.get(POLICY))
                        .replace("$policy_mem", this.averageMem.get(POLICY)).replace("$policy_traffic", this.averageTraffic.get(POLICY));
                fos.write(rep.getBytes()) ;
            }
            else if(line.contains("fortune_cpu")){
                rep = line.replace("$fortune_cpu", this.averageCpu.get(FORTUNE))
                        .replace("$fortune_mem", this.averageMem.get(FORTUNE)).replace("$fortune_traffic", this.averageTraffic.get(FORTUNE));
                fos.write(rep.getBytes()) ;
            }
            else if(line.contains("als_cpu")){
                rep = line.replace("$als_cpu", this.averageCpu.get(ALS))
                        .replace("$als_mem", this.averageMem.get(ALS)).replace("$als_traffic", this.averageTraffic.get(ALS));
                fos.write(rep.getBytes()) ;
            }
            else if(line.contains("health_cpu")){
                rep = line.replace("$health_cpu", this.averageCpu.get(HEALTH))
                        .replace("$health_mem", this.averageMem.get(HEALTH)).replace("$health_traffic", this.averageTraffic.get(HEALTH));
                fos.write(rep.getBytes()) ;
            }
            else if(line.contains("$potal")){
                rep = line.replace("$potal", this.count.get(POTAL));

                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$门户cpuinfo")){
                rep = line.replace("$门户cpuinfo", this.cpuinfo.get(POTAL));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$门户meminfo")){
                rep = line.replace("$门户meminfo", this.meminfo.get(POTAL));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$门户traffic")){
                rep = line.replace("$门户traffic", this.traffic.get(POTAL));
                fos.write(rep.getBytes()) ;

            }

            else if(line.contains("$policy")){
                rep = line.replace("$policy", this.count.get(POLICY));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$保单cpuinfo")){
                rep = line.replace("$保单cpuinfo", this.cpuinfo.get(POLICY));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$保单meminfo")){
                rep = line.replace("$保单meminfo", this.meminfo.get(POLICY));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$保单traffic")){
                rep = line.replace("$保单traffic", this.traffic.get(POLICY));
                fos.write(rep.getBytes()) ;

            }

            else if(line.contains("$als")){
                rep = line.replace("$als", this.count.get(ALS));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$活动cpuinfo")){
                rep = line.replace("$活动cpuinfo", this.cpuinfo.get(ALS));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$活动meminfo")){
                rep = line.replace("$活动meminfo", this.meminfo.get(ALS));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$活动traffic")){
                rep = line.replace("$活动traffic", this.traffic.get(ALS));
                fos.write(rep.getBytes()) ;

            }

            else if(line.contains("$fortune")){
                rep = line.replace("$fortune", this.count.get(FORTUNE));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$财富cpuinfo")){
                rep = line.replace("$财富cpuinfo", this.cpuinfo.get(FORTUNE));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$财富meminfo")){
                rep = line.replace("$财富meminfo", this.meminfo.get(FORTUNE));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$财富traffic")){
                rep = line.replace("$财富traffic", this.traffic.get(FORTUNE));
                fos.write(rep.getBytes()) ;

            }

            else if(line.contains("$health")){
                rep = line.replace("$health", this.count.get(HEALTH));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$健康cpuinfo")){
                rep = line.replace("$健康cpuinfo", this.cpuinfo.get(HEALTH));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$健康meminfo")){
                rep = line.replace("$健康meminfo", this.meminfo.get(HEALTH));
                fos.write(rep.getBytes()) ;

            }
            else if(line.contains("$健康traffic")){
                rep = line.replace("$健康traffic", this.traffic.get(HEALTH));
                fos.write(rep.getBytes()) ;

            }

            else fos.write(line.getBytes());
            fos.write("\n".getBytes());
            fos.flush();
        }
        fos.close();
        buff.close();
        fr.close();
    }


}
