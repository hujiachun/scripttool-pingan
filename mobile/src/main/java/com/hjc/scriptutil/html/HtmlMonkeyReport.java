package com.hjc.scriptutil.html;

import android.content.Context;

import com.hjc.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hujiachun on 16/9/4.
 */

public class HtmlMonkeyReport {

    private String start;
    private String end;
    private String time;
    private String project;
    private String device;
    private String anr;
    private String crash;

    public HtmlMonkeyReport(String start, String end, String time, String project, String device, String anr, String crash) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.project = project;
        this.device = device;
        this.anr = anr;
        this.crash = crash;
    }

    public void writeToHtml(Context context, String path) throws IOException {
        File html = new File(context.getFilesDir().getPath() + "/report/testReport.html");
        File report = new File(path + "/Report.html");
        FileOutputStream fos = new FileOutputStream(report);
        FileReader fr = new FileReader(html);
        BufferedReader buff = new BufferedReader(fr);
        String line = "";
        String rep = "";
        Map<String,ReplaceReport> map= new HashMap();
        map.put(Constants.MONKEY_START, new ReplaceReport.$start());
        map.put(Constants.MONKEY_END, new ReplaceReport.$end());
        map.put(Constants.MONKEY_TIME, new ReplaceReport.$time());
        map.put(Constants.MONKEY_PROJECT, new ReplaceReport.$project());
        map.put(Constants.MONKEY_DEVICES, new ReplaceReport.$device());
        map.put(Constants.MONKEY_ANR, new ReplaceReport.$anr());
        map.put(Constants.MONKEY_CRASH, new ReplaceReport.$crash());
        MonkeyReportInfo info = new MonkeyReportInfo("2016", "2017", "1", "com.pingan.life", "meizu", "10", "1");
        A: while ((line=buff.readLine()) != null){
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next().toString();
                if(line.contains(key)){
                    rep = line.replace(key, info.getInfo(key));
                    map.get(key).replace(fos, rep);
                    continue A;//替换成功后遍历换行,防止重复读写
                }

            }
            fos.write(line.getBytes());

//            if(line.contains("$start")){
//                rep = line.replace("$start", this.start);
//                fos.write(rep.getBytes()) ;
//            }
//            else if(line.contains("$end")){
//                rep = line.replace("$end", this.end);
//                fos.write(rep.getBytes()) ;
//            }
//            else if(line.contains("$time")){
//                rep = line.replace("$time", this.time);
//                fos.write(rep.getBytes()) ;
//            }
//            else if(line.contains("$project")){
//                rep = line.replace("$project", this.project);
//                fos.write(rep.getBytes()) ;
//            }
//            else if(line.contains("$device")){
//                rep = line.replace("$devicet", this.device);
//                fos.write(rep.getBytes()) ;
//            }
//            else if(line.contains("$anr")){
//                rep = line.replace("$anr", this.anr);
//                fos.write(rep.getBytes()) ;
//            }
//            else if(line.contains("$crash")){
//                rep = line.replace("$crash", this.crash);
//                fos.write(rep.getBytes()) ;
//            }
//            else fos.write(line.getBytes());
            fos.flush();
        }
        fos.close();
        buff.close();
        fr.close();
    }
}
