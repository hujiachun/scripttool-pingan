package com.hjc.scriptutil.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by hujiachun on 16/9/7.
 */

public class AnalysisResponseXML {
    private String path;
    private ArrayList actionList, timeList;

    public AnalysisResponseXML(String path) {
        this.path = path;
        actionList = new ArrayList();
        timeList = new ArrayList();
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


    public String getActionData(){
        return getData(actionList);
    }


    public String getTimeList(){
        return getData(timeList);
    }

    public void readXML() throws IOException {
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        File file = new File(this.path + "/response.xml");
        if(file.isFile() && file.exists()){
            try {
                read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine())!= null) {
                    actionList.add(lineTxt.split(",")[0]);
                    timeList.add(lineTxt.split(",")[1]);
                }
                read.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(read != null){
                    read.close();
                }
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            }

        }
    }
}
