package com.hjc.scripttool.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hjc.scripttool.R;
import com.hjc.scripttool.activity.ChartActivity;
import com.hjc.scriptutil.tools.PerformanceCaseInfo;
import com.hjc.util.Constants;
import com.hjc.util.Util;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by hujiachun on 16/3/8.
 */
public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.ItemViewHolder>{

    public ArrayList<PerformanceCaseInfo> items;
    public Context context;
    public String path;

    public CaseAdapter(ArrayList<PerformanceCaseInfo> items, String path, Context context) {
        this.items = items;
        this.context = context;
        this.path = path;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.performancecaseinfo, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CaseAdapter.ItemViewHolder itemViewHolder, final int position) {

        final PerformanceCaseInfo item = items.get(position);

        itemViewHolder.performance_img.setImageResource(R.drawable.ceae);
        itemViewHolder.average_mem_vaule.setText(String.valueOf(item.getAverageMem()));
        itemViewHolder.average_cpu_vaule.setText(String.valueOf(item.getAverageCpu()));
        itemViewHolder.max_mem_vaule.setText(String.valueOf(item.getMaxMem()));
        itemViewHolder.max_cpu_vaule.setText(String.valueOf(item.getMaxCpu()));
        itemViewHolder.performance_case.setText(item.getCaseName() + "  耗流:" + item.getTraffic() +"KB   ·");
        itemViewHolder.case_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ArrayList<String> memStringList = new ArrayList<>();
                ArrayList<String> cpuStringList = new ArrayList<>();
                ArrayList<String> trafficStringList = new ArrayList<>();
                intent.putExtra(Constants.MAX_MEM, String.valueOf(item.getMaxMem()));
                intent.putExtra(Constants.MAX_CPU, String.valueOf(item.getMaxCpu()));
                try {

                    ArrayList<String> dataList = Util.readCsv(path + item.getCaseName() + Constants.CSV, context);//解析CSV
                    for( String data : dataList){
                        memStringList.add(data.split(",")[2]);//得到内存
                        cpuStringList.add(data.split(",")[4].split(Constants.PCT)[0]);//得到cpu
                        trafficStringList.add(data.split(",")[8]);//得到实时流量消耗
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putStringArrayListExtra(Constants.MEM_LIST, memStringList);
                intent.putStringArrayListExtra(Constants.CPU_LIST, cpuStringList);
                intent.putStringArrayListExtra(Constants.TRAFFIC_LIST, trafficStringList);
                intent.setClass(context.getApplicationContext(), ChartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public final static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView performance_img;
        TextView performance_case, max_mem_vaule, average_mem_vaule, max_cpu_vaule, average_cpu_vaule;
        LinearLayout case_layout;


        public ItemViewHolder(View itemView) {
            super(itemView);
            performance_img = (ImageView) itemView.findViewById(R.id.performance_img);
            performance_case = (TextView) itemView.findViewById(R.id.performance_case);
            max_mem_vaule = (TextView) itemView.findViewById(R.id.max_mem_vaule);
            average_mem_vaule = (TextView) itemView.findViewById(R.id.average_mem_vaule);
            max_cpu_vaule = (TextView) itemView.findViewById(R.id.max_cpu_vaule);
            average_cpu_vaule = (TextView) itemView.findViewById(R.id.average_cpu_vaule);
            case_layout = (LinearLayout) itemView.findViewById(R.id.case_layout);
        }
    }
}
