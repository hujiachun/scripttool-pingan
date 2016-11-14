package com.hjc.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class Http {

    private SharedPreferences pref_sk;

    public Http(Context context) {

    }



    public Http() {

    }

    public static long getContentLength(String url) throws IOException {
        HttpGet post = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        return client.execute(post).getEntity().getContentLength();
    }

    public int upload(String path, String url) {
        Log.e(Constants.TAG, "Upload " + HttpVersion.HTTP_1_1);
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
        HttpPost httppost = new HttpPost(url);
        Log.e(Constants.TAG, "Upload url : " + url);
        File file = new File(path);
        MultipartEntity mutiEntity = new MultipartEntity();
        mutiEntity.addPart("jsonFile", new FileBody(file));
        httppost.setEntity(mutiEntity);
        HttpResponse response;
        String result = "";
        String line = "";
        try {
            response = httpclient.execute(httppost);
            Log.e(Constants.TAG, response.getStatusLine().toString());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            while ((line = rd.readLine())!= null) {
                result += line;

            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "upload failed because of :\n" + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        int status = -1;
        try {
            status = JSON.parseObject(result).getInteger("status");
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    /**
     * @category 上传文件至Server的方法
     * @param uploadUrl 上传路径参数
     * @param uploadFilePath 文件路径
     */
    public void uploadFile(String uploadUrl, String uploadFilePath) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try {
            Log.e(Constants.TAG, "1");
            URL url = new URL(uploadUrl);
            Log.e(Constants.TAG, "2");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            Log.e(Constants.TAG, "3");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(60 * 1000);
            httpURLConnection.setRequestProperty("Connection", "keep-alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            Log.e(Constants.TAG, "4");
            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadFile\"; filename=\"report.json\"" + end);

            dos.writeBytes(end);
            // 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
            FileInputStream fis = new FileInputStream(uploadFilePath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);

            }
            fis.close();
            Log.e(Constants.TAG, "file send to server............");
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            // 读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();
            Log.e(Constants.TAG, result);
            dos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constants.TAG, e.toString());
        }

    }

}
