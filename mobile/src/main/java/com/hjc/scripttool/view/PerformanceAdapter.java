package com.hjc.scripttool.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjc.scripttool.R;
import com.hjc.scripttool.activity.PerformanceCaseActitity;
import com.hjc.scriptutil.html.AnalysisResponseXML;
import com.hjc.scriptutil.html.PfmHtml;
import com.hjc.scriptutil.html.WriteResponse;
import com.hjc.scriptutil.html.WriteToPfmHtml;
import com.hjc.scriptutil.mail.Email;
import com.hjc.scriptutil.mail.EmailService;
import com.hjc.scriptutil.tools.Performanceinfo;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.FileUtil;
import com.hjc.util.Util;
import com.hjc.util.ZipCompressor;
import com.hjc.util.ZipUtil;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hujiachun on 16/3/2.
 */
public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.ItemViewHolder> {
    public ArrayList<Performanceinfo> items;
    private OnRecyclerViewItemClickListener listener;
    public Context context;
    Performanceinfo item;



    public PerformanceAdapter(ArrayList<Performanceinfo> items, Context context) {
        this.items = items;
        this.context = context;
    }


    //点击监听事件
    public interface OnRecyclerViewItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    //设置监听器
    public void setListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        //将布局进行绑定
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.performanceinfo, viewGroup, false);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, final int i) {

        item = items.get(i);
        itemViewHolder.name.setText(item.getName());
        itemViewHolder.time.setText(item.getTime());
        itemViewHolder.review.setImageResource(item.getRereview_img());
        itemViewHolder.delete.setImageResource(item.getDelete_img());
        itemViewHolder.email.setImageResource(item.getEmail_img());

        itemViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (this) {
                    int position = itemViewHolder.getLayoutPosition();
                    if(position != -1){
                        Log.e(Constants.TAG, i + ":" + position);
                        Util.deleteDir(new File(Constants.PERFORMANCE_PATH + items.get(position).getName() + "_" + items.get(position).getTime()));
                        items.remove(position);
                        notifyItemRemoved(position);
//                        notifyAll();

                    }
                }
            }
        });

        itemViewHolder.review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                File file = new File(Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText());
                String[] list = file.list();

                List<String> hisList = new ArrayList<>(Arrays.asList(list));
                intent.putStringArrayListExtra(Constants.CASE_LIST, (ArrayList<String>) hisList).
                        putExtra(Constants.TYPE, itemViewHolder.name.getText()).putExtra(Constants.TIME, itemViewHolder.time.getText());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, PerformanceCaseActitity.class);
                context.startActivity(intent);
            }
        });


        itemViewHolder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "正在上传... ", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                try {


//                    if (new File(Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText() + "/report.zip").exists()) {
//                        Log.e(Constants.TAG, "已压缩");
//                    } else {
//                        ZipUtil.zip(Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText(),
//                                Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText() + "/report.zip");
//                    }
//                    Email email = new Email();
//                    ArrayList<String> report = new ArrayList();
//                    ArrayList<String> reportName = new ArrayList();
//                    SharedPreferences preferences = Settings.getDefaultSharedPreferences(context);
//                    report.add(Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText() + "/report.zip");
//                    reportName.add("report.zip");
//                    email.setAttachment(report.toArray(new String[0]));
//                    email.setReceiver("hujiachun684@pingan.com.cn");
//                    email.setSubject("Performance Report");
//                    email.setAttachmentName(reportName.toArray(new String[0]));
//                    email.setContent("路径:" + Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText());
//                    new EmailService(email).sendAttachmentEmail();

                    String path = Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText();

                    String[] fileList = new File(path).list();
                    PfmHtml potal, policy, als, fortune, health;
                    HashMap memmap = new HashMap();
                    HashMap cpumap = new HashMap();
                    HashMap trafficmap = new HashMap();
                    HashMap countmap = new HashMap();
                    HashMap averageCpu = new HashMap();
                    HashMap averageMem = new HashMap();
                    HashMap averageTraffic = new HashMap();
                    for(String file : fileList){
                        Log.e(Constants.TAG, file);
                        if(file.contains(WriteToPfmHtml.POTAL)){//通过关键字potal区分模块

                             potal = new PfmHtml(path + "/" + file, context);
                             memmap.put(WriteToPfmHtml.POTAL, potal.getMemData());
                             cpumap.put(WriteToPfmHtml.POTAL, potal.getCpuData());
                             trafficmap.put(WriteToPfmHtml.POTAL, potal.getTraffic());
                             countmap.put(WriteToPfmHtml.POTAL, potal.getCount());
                             averageCpu.put(WriteToPfmHtml.POTAL,String.valueOf(potal.getAverageCpu()));
                             averageMem.put(WriteToPfmHtml.POTAL, String.valueOf(potal.getAverageMem()));
                             averageTraffic.put(WriteToPfmHtml.POTAL, String.valueOf(potal.getUseTraffic()));
                        }
                        if(file.contains(WriteToPfmHtml.POLICY)){
                            policy = new PfmHtml(path +"/" + file, context);
                            memmap.put(WriteToPfmHtml.POLICY, policy.getMemData());
                            cpumap.put(WriteToPfmHtml.POLICY, policy.getCpuData());
                            trafficmap.put(WriteToPfmHtml.POLICY, policy.getTraffic());
                            countmap.put(WriteToPfmHtml.POLICY, policy.getCount());
                            averageCpu.put(WriteToPfmHtml.POLICY, String.valueOf(policy.getAverageCpu()));
                            averageMem.put(WriteToPfmHtml.POLICY, String.valueOf(policy.getAverageMem()));
                            averageTraffic.put(WriteToPfmHtml.POLICY, String.valueOf(policy.getUseTraffic()));
                        }
                        if(file.contains(WriteToPfmHtml.ALS)){
                            als = new PfmHtml(path + "/" + file, context);
                            memmap.put(WriteToPfmHtml.ALS, als.getMemData());
                            cpumap.put(WriteToPfmHtml.ALS, als.getCpuData());
                            trafficmap.put(WriteToPfmHtml.ALS, als.getTraffic());
                            countmap.put(WriteToPfmHtml.ALS, als.getCount());
                            averageCpu.put(WriteToPfmHtml.ALS, String.valueOf(als.getAverageCpu()));
                            averageMem.put(WriteToPfmHtml.ALS, String.valueOf(als.getAverageMem()));
                            averageTraffic.put(WriteToPfmHtml.ALS, String.valueOf(als.getUseTraffic()));
                        }
                        if(file.contains(WriteToPfmHtml.FORTUNE)){
                            fortune = new PfmHtml(path + "/" + file, context);
                            memmap.put(WriteToPfmHtml.FORTUNE, fortune.getMemData());
                            cpumap.put(WriteToPfmHtml.FORTUNE, fortune.getCpuData());
                            trafficmap.put(WriteToPfmHtml.FORTUNE, fortune.getTraffic());
                            countmap.put(WriteToPfmHtml.FORTUNE, fortune.getCount());
                            averageCpu.put(WriteToPfmHtml.FORTUNE, String.valueOf(fortune.getAverageCpu()));
                            averageMem.put(WriteToPfmHtml.FORTUNE, String.valueOf(fortune.getAverageMem()));
                            averageTraffic.put(WriteToPfmHtml.FORTUNE, String.valueOf(fortune.getUseTraffic()));
                        }
                        if(file.contains(WriteToPfmHtml.HEALTH)){
                            health = new PfmHtml(path + "/" + file, context);
                            memmap.put(WriteToPfmHtml.HEALTH, health.getMemData());
                            cpumap.put(WriteToPfmHtml.HEALTH, health.getCpuData());
                            trafficmap.put(WriteToPfmHtml.HEALTH, health.getTraffic());
                            countmap.put(WriteToPfmHtml.HEALTH, health.getCount());
                            averageCpu.put(WriteToPfmHtml.HEALTH, String.valueOf(health.getAverageCpu()));
                            averageMem.put(WriteToPfmHtml.HEALTH, String.valueOf(health.getAverageMem()));
                            averageTraffic.put(WriteToPfmHtml.HEALTH, String.valueOf(health.getUseTraffic()));
                        }

                    }
                    File htmlfile = new File(path + "/html");
                    if(!htmlfile.exists()){
                        htmlfile.mkdirs();
                    }

                    new WriteToPfmHtml(memmap, cpumap, countmap, trafficmap, averageCpu, averageMem, averageTraffic).writeToPotal(context, htmlfile.getAbsolutePath());

                    AnalysisResponseXML arx = new AnalysisResponseXML(path);
                    arx.readXML();
                    new WriteResponse(arx.getActionData(), arx.getTimeList()).writeToResponse(context, htmlfile.getAbsolutePath());
                    FileUtil.copyFolderWithSelf(context.getFilesDir().getPath() + "/report/css/", htmlfile.getAbsolutePath());
                    FileUtil.copyFolderWithSelf(context.getFilesDir().getPath() + "/report/js/", htmlfile.getAbsolutePath());
                    FileUtil.CopySingleFileTo(context.getFilesDir().getPath() + "/report/echarts.min.js", htmlfile.getAbsolutePath());
                    ZipUtil.zip(htmlfile.getAbsolutePath(), path +"/report.zip");

                    Email email = new Email();
                    ArrayList<String> report = new ArrayList();
                    ArrayList<String> reportName = new ArrayList();
                    SharedPreferences preferences = Settings.getDefaultSharedPreferences(context);
                    report.add(path + "/report.zip");
                    reportName.add("report.zip");
                    email.setAttachment(report.toArray(new String[0]));
                    email.setReceiver("hujiachun684@pingan.com.cn");
                    email.setSubject("Android性能报告");
                    email.setAttachmentName(reportName.toArray(new String[0]));
                    email.setContent("路径:" + Constants.PERFORMANCE_PATH + itemViewHolder.name.getText() + "_" + itemViewHolder.time.getText());
                    new EmailService(email).sendAttachmentEmail();
                    handler.sendEmptyMessage(0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                    }
                }).start();
            }
        });

        if (listener != null) {
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = itemViewHolder.getLayoutPosition();
                    listener.onItemClick(itemViewHolder.itemView, pos);
                }

            });

            itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = itemViewHolder.getLayoutPosition();
                    listener.onItemLongClick(itemViewHolder.itemView, pos);
                    return false;
                }
            });
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public int getItemCount() {
        return items.size();
    }

    //ViewHolder，用于缓存
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        ImageView delete;
        ImageView review;
        ImageView name_img;
        ImageView time_img;
        ImageView email;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.preformance_name);
            time = (TextView) itemView.findViewById(R.id.preformance_time);
            delete = (ImageView) itemView.findViewById(R.id.preformance_delete);
            review = (ImageView) itemView.findViewById(R.id.preformance_review);
            name_img = (ImageView) itemView.findViewById(R.id.name_img);
            time_img = (ImageView) itemView.findViewById(R.id.time_img);
            email = (ImageView) itemView.findViewById(R.id.email_img);

        }
    }
}

