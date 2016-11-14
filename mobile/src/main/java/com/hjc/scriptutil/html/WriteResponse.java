package com.hjc.scriptutil.html;

import android.content.Context;
import android.util.Log;

import com.hjc.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by hujiachun on 16/9/6.
 */

public class WriteResponse {
    private String timeinfo, action;



    public WriteResponse(String action, String responseinfo) {
        this.timeinfo = responseinfo;
        this.action = action;
        Log.e(Constants.TAG, "传过来:" + this.action);
    }

    public void writeToResponse(Context context, String path) throws IOException {

        File html = new File(context.getFilesDir().getPath() + "/report/response.html");
        File report = new File(path + "/response.html");
        FileOutputStream fos = new FileOutputStream(report);
        OutputStreamWriter oStreamWriter = new OutputStreamWriter(fos, "GBK");
        InputStreamReader fr = new InputStreamReader(new FileInputStream(html));
        BufferedReader buff = new BufferedReader(fr);
        String line = "";
        String rep = "";
        while ((line=buff.readLine()) != null){
            if(line.contains("$action")){
                rep = line.replace("$action", this.action);
                Log.e(Constants.TAG,"替换后:"+ rep);
                fos.write(rep.getBytes()) ;
//                oStreamWriter.append(rep);
            }

            else if(line.contains("$response")){
                rep = line.replace("$response", this.timeinfo);
                fos.write(rep.getBytes()) ;
//                oStreamWriter.append(rep);
            }
            else {
                fos.write(line.getBytes());
//                oStreamWriter.append(line);
            }
            fos.write("\n".getBytes());
            fos.flush();
//            oStreamWriter.append("\n");
//            oStreamWriter.flush();
        }

        oStreamWriter.close();
        fos.close();
        buff.close();
        fr.close();
    }
}
