package com.hjc.performance;

/**
 * Created by hujiachun684 on 16/7/6.
 */


import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.hjc.util.Constants;


public class Cpu {


    private Context context;
    private long processCpu;
    private ArrayList<Long> idleCpu = new ArrayList<Long>();
    private ArrayList<Long> totalCpu = new ArrayList<Long>();
    private boolean isInitialStatics = true;
    private SimpleDateFormat formatterFile;
    private MemoryInfo mi;
    private String totalMemorySize;
    private long preTraffic;
    private long lastestTraffic;
    private long traffic;
    private TrafficInfo trafficInfo;
    private ArrayList<String> cpuUsedRatio = new ArrayList<String>();
    private ArrayList<Long> totalCpu2 = new ArrayList<Long>();
    private long processCpu2;
    private ArrayList<Long> idleCpu2 = new ArrayList<Long>();
    private String processCpuRatio = "";
    private ArrayList<String> totalCpuRatio = new ArrayList<String>();
    private int pid, uid;
    private static final String INTEL_CPU_NAME = "model name";
    private static final String CPU_DIR_PATH = "/sys/devices/system/cpu/";
    private static final String CPU_X86 = "x86";
    private static final String CPU_INFO_PATH = "/proc/cpuinfo";
    private static final String CPU_STAT = "/proc/stat";

    public Cpu(Context context, int pid, int uid) {
        this.pid = pid;
        this.uid = uid;
        this.context = context;
        formatterFile = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mi = new MemoryInfo();
        totalMemorySize = mi.getTotalMemory();
        cpuUsedRatio = new ArrayList<>();
        trafficInfo = new TrafficInfo(this.uid);
    }

    /**
     * read the status of CPU.
     *
     * @throws FileNotFoundException
     */
    public void readCpuStat() {
        String processPid = Integer.toString(pid);
        String cpuStatPath = "/proc/" + processPid + "/stat";
        try {
            // monitor cpu stat of certain process
            RandomAccessFile processCpuInfo = new RandomAccessFile(cpuStatPath, "r");
            String line = "";
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.setLength(0);
            while ((line = processCpuInfo.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            String[] tok = stringBuffer.toString().split(" ");
            processCpu = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
            processCpuInfo.close();
        } catch (FileNotFoundException e) {
            Log.e(Constants.TAG, "FileNotFoundException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readTotalCpuStat();
    }

    /**
     * read stat of each CPU cores
     */
    private void readTotalCpuStat() {
        try {
            // monitor total and idle cpu stat of certain process
            RandomAccessFile cpuInfo = new RandomAccessFile(CPU_STAT, "r");
            String line = "";
            while ((null != (line = cpuInfo.readLine())) && line.startsWith("cpu")) {
                String[] toks = line.split("\\s+");
                idleCpu.add(Long.parseLong(toks[4]));
                totalCpu.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                        + Long.parseLong(toks[6]) + Long.parseLong(toks[5]) + Long.parseLong(toks[7]));
            }
            cpuInfo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get CPU name.
     *
     * @return CPU name
     */
    public static String getCpuName() {
        try {
            RandomAccessFile cpuStat = new RandomAccessFile(CPU_INFO_PATH, "r");
            // check cpu type
            if (Build.CPU_ABI.equalsIgnoreCase(CPU_X86)) {
                String line;
                while (null != (line = cpuStat.readLine())) {
                    String[] values = line.split(":");
                    if (values[0].contains(INTEL_CPU_NAME)) {
                        cpuStat.close();
                        return values[1];
                    }
                }
            } else {
                String[] cpu = cpuStat.readLine().split(":"); // cpu信息的前一段是含有processor字符串，此处替换为不显示
                cpuStat.close();
                return cpu[1];
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException: " + e.getMessage());
        }
        return "";
    }

    /**
     * display directories naming with "cpu*"
     *
     * @author andrewleo
     */
    static class CpuFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            // Check if filename matchs "cpu[0-9]"
            if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                return true;
            }
            return false;
        }
    }

    /**
     * get CPU core numbers
     *
     * @return cpu core numbers
     */
    public static int getCpuNum() {
        try {
            // Get directory containing CPU info
            File dir = new File(CPU_DIR_PATH);
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * get CPU core list
     *
     * @return cpu core list
     */
    public ArrayList<String> getCpuList() {
        ArrayList<String> cpuList = new ArrayList<String>();
        try {
            // Get directory containing CPU info
            File dir = new File(CPU_DIR_PATH);
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            for (int i = 0; i < files.length; i++) {
                cpuList.add(files[i].getName());
            }
            return cpuList;
        } catch (Exception e) {
            e.printStackTrace();
            cpuList.add("cpu0");
            return cpuList;
        }
    }

    /**
     * reserve used ratio of process CPU and total CPU, meanwhile collect
     * network traffic.
     *
     * @return network traffic ,used ratio of process CPU and total CPU in
     * certain interval
     */
    public ArrayList<String> getCpuRatioInfo() throws IOException {


        DecimalFormat fomart = new DecimalFormat();
        fomart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        fomart.setGroupingUsed(false);
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(2);

        cpuUsedRatio.clear();
        idleCpu.clear();
        totalCpu.clear();
        totalCpuRatio.clear();
        readCpuStat();

        if (isInitialStatics) {
            preTraffic = trafficInfo.getTrafficInfo();//第一次获取流量
            isInitialStatics = false;
        } else {
            lastestTraffic = trafficInfo.getTrafficInfo();//第N次获取流量
            if (preTraffic == -1)
                traffic = -1;
            else {
                if (lastestTraffic > preTraffic) {
                    traffic = (lastestTraffic - preTraffic) / 1024;
                    preTraffic = lastestTraffic;
                }
            }
        }


        StringBuffer totalCpuBuffer = new StringBuffer();
        if (null != totalCpu2 && totalCpu2.size() > 0) {
            processCpuRatio = fomart.format(100 * ((double) (processCpu - processCpu2) / ((double) (totalCpu.get(0) - totalCpu2.get(0)))));
            for (int i = 0; i < (totalCpu.size() > totalCpu2.size() ? totalCpu2.size() : totalCpu.size()); i++) {
                String cpuRatio = "0.00";
                if (totalCpu.get(i) - totalCpu2.get(i) > 0) {
                    cpuRatio = fomart
                            .format(100 * ((double) ((totalCpu.get(i) - idleCpu.get(i)) - (totalCpu2.get(i) - idleCpu2.get(i))) / (double) (totalCpu
                                    .get(i) - totalCpu2.get(i))));
                }
                totalCpuRatio.add(cpuRatio);
                totalCpuBuffer.append(cpuRatio + Constants.COMMA);
            }
        } else {
            processCpuRatio = "0";
            totalCpuRatio.add("0");
            totalCpuBuffer.append("0,");
            totalCpu2 = (ArrayList<Long>) totalCpu.clone();
            processCpu2 = processCpu;
            idleCpu2 = (ArrayList<Long>) idleCpu.clone();
        }


        totalCpu2 = (ArrayList<Long>) totalCpu.clone();
        processCpu2 = processCpu;
        idleCpu2 = (ArrayList<Long>) idleCpu.clone();
        cpuUsedRatio.add(processCpuRatio);
        cpuUsedRatio.add(totalCpuRatio.get(0));
        cpuUsedRatio.add(String.valueOf(traffic));

        return cpuUsedRatio;
    }

    /**
     * is text a positive number
     *
     * @param text
     * @return
     */
    private boolean isPositive(String text) {
        Double num;
        try {
            num = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return false;
        }
        return num >= 0;
    }


    public long getTraffic() throws IOException {
        return trafficInfo.getTrafficInfo();
    }
}

