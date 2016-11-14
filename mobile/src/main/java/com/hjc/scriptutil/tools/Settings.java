package com.hjc.scriptutil.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hujiachun on 16/2/25.
 */
public class Settings {

    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_PERFORMANCE = "performance";
    public static final String KEY_LOGCAT_PATH = "logcat_path";
    public static final String KEY_LOGCAT_STATE = "logcat_state";
    public static final String KEY_BATTERY_STATE = "battery_state";
    public static final String KEY_UIAUTOMATOR = "ui_resultpath";
    public static final String KEY_HEAP_STATE = "root_state";
    public static final String KEY_PACKAGE = "package_name";
    public static final String KEY_CASE = "test_case";
    public static final String KEY_TIME = "time_str";
    public static final String KEY_WIFI_SSID = "ssid";
    public static final String KEY_WIFI_PWD = "pwd";
    public static final String KEY_WIFI_STATE = "state";
    public static final String KEY_COMMAND = "command";
    public static final String KEY_EMAIL_TO = "email_to";
    public static final String KEY_EMAIL_CC = "email_cc";
    public static final String KEY_SERVER = "server";
    public static final String KEY_TRACES = "traces";
    public static final String KEY_STRAT = "start";
    public static final String KEY_VERSION = "VERSION";
    private static Settings instance = null;
    private SharedPreferences preferences;


    public static synchronized Settings getInstance(Context context){
        if(instance == null){

            instance = new Settings(context);
        }
        return instance;
    }

    private Settings(Context context){
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public String getPackageName(){
        return this.preferences.getString(KEY_PACKAGE, "平安金管家:com.pingan.lifeinsurance").split(":")[1];
    }



}
