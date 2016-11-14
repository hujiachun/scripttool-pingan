package com.hjc.scripttool.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hjc.scripttool.R;
import com.hjc.scriptutil.tools.Appinfo;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.service.EventService;
import com.hjc.service.MainService;
import com.hjc.service.MonkeyService;
import com.hjc.service.RadioService;
import com.hjc.service.TimerService;
import com.hjc.util.Constants;
import com.hjc.util.FileUtil;
import com.hjc.util.ShellUtils;
import com.hjc.util.Util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hujiachun on 15/11/5.
 */
public class MonkeyAcitivity extends Activity {
    EditText touch, motion, trackball, nav, majornav, syskeys, appswitch, anyevent, seed, throttle, runtime, packagename;
    TextView command, view1, view2, view3, view4, view5, view6, view7, view8, view9, view10;
    int time;
    static String touch_text, motion_text, trackball_text, nav_text, majornav_text, syskeys_text,
            appswitch_text, anyevent_text, seed_text, throttle_text, runtime_text, command_text, package_text;
    static String touchvalue, motionwalue, trackballvalue, navvalue, majornavvalue,
            syskeysvalue, appswitchvalue, anyeventvalue, crashesvalue = "", timeoutsvalue = "", killvalue = "", sendvalue, throttlevalue, runtimevalue;
    CheckBox crashes, timeouts, killprocess;
    boolean isconfirm = false;
    public static File file ;
    String path = null;
    public ArrayList<Appinfo> appinfos;
    static ProgressDialog progress;
    private TimerTask timerTask;


    public static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monkeyactivity);
        appinfos = new ArrayList<>();
        touch = (EditText) this.findViewById(R.id.touch);
        motion = (EditText) this.findViewById(R.id.motion);
        trackball = (EditText) this.findViewById(R.id.trackball);
        nav = (EditText) this.findViewById(R.id.nav);
        majornav = (EditText) this.findViewById(R.id.majornav);
        syskeys = (EditText) this.findViewById(R.id.syskeys);
        appswitch = (EditText) this.findViewById(R.id.appswitch);
        anyevent = (EditText) this.findViewById(R.id.anyevent);
        seed = (EditText) this.findViewById(R.id.seed);
        seed.setText(String.valueOf(new Random().nextInt()).substring(1, 7));
        throttle = (EditText) this.findViewById(R.id.throttle);
        runtime = (EditText) this.findViewById(R.id.count);


        /**
         * 提示
         */
        view1 = (TextView) this.findViewById(R.id.view1);
        view2 = (TextView) this.findViewById(R.id.view2);
        view3 = (TextView) this.findViewById(R.id.view3);
        view4 = (TextView) this.findViewById(R.id.view4);
        view5 = (TextView) this.findViewById(R.id.view5);
        view6 = (TextView) this.findViewById(R.id.view6);
        view7 = (TextView) this.findViewById(R.id.view7);
        view8 = (TextView) this.findViewById(R.id.view8);
        view9 = (TextView) this.findViewById(R.id.view9);
        view10 = (TextView) this.findViewById(R.id.view10);

        packagename = (EditText) this.findViewById(R.id.packagename);

        command = (TextView) this.findViewById(R.id.command);

        crashes = (CheckBox) this.findViewById(R.id.crashes);
        timeouts = (CheckBox) this.findViewById(R.id.timeouts);
//        killprocess = (CheckBox) this.findViewById(R.id.kill);

        crashes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    crashesvalue = " --ignore-crashes ";
                }
                else crashesvalue = "";
            }
        });


        timeouts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    timeoutsvalue = " --ignore-timeouts ";
                }
                else timeoutsvalue = "";
            }
        });

//        killprocess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    killvalue = " --kill-process-after-error ";
//                }
//
//            }
//        });

        Button btn1 = (Button) this.findViewById(R.id.btn1);
        Button btn2 = (Button) this.findViewById(R.id.btn2);
        Button btn3 = (Button) this.findViewById(R.id.btn3);



        btn1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {


                touch_text = touch.getText().toString();
                motion_text = motion.getText().toString();
                trackball_text = trackball.getText().toString();
                nav_text = nav.getText().toString();
                majornav_text = majornav.getText().toString();
                syskeys_text = syskeys.getText().toString();
                appswitch_text = appswitch.getText().toString();
                anyevent_text = anyevent.getText().toString();
                seed_text = seed.getText().toString();
                throttle_text = throttle.getText().toString();
                runtime_text = runtime.getText().toString();


                if (!touch_text.equals("")) {
                    touchvalue = " --pct-touch " + touch_text;
                } else {
                    touchvalue = "";
                }
                if (!motion_text.equals("")) {
                    motionwalue = " --pct-motion " + motion_text;
                } else {
                    motionwalue = "";
                }
                if (!trackball_text.equals("")) {
                    trackballvalue = " --pct-trackball " + trackball_text;
                } else {
                    trackballvalue = "";
                }
                if (!nav_text.equals("")) {
                    navvalue = " --pct-nav " + nav_text;
                } else {
                    navvalue = "";
                }
                if (!trackball_text.equals("")) {
                    majornavvalue = " --pct-majornav " + majornav_text;
                } else {
                    majornavvalue = "";
                }
                if (!syskeys_text.equals("")) {
                    syskeysvalue = " --pct-syskeys " + syskeys_text;
                } else {
                    syskeysvalue = "";
                }
                if (!appswitch_text.equals("")) {
                    appswitchvalue = " --pct-appswitch " + appswitch_text;
                } else {
                    appswitchvalue = "";
                }
                if (!anyevent_text.equals("")) {
                    anyeventvalue = " --pct-anyevent " + anyevent_text;
                } else {
                    anyeventvalue = "";
                }
                if (!seed_text.equals("")) {
                    sendvalue = " -s " + seed_text;
                } else {
                    sendvalue = "";
                }
                if (!throttle_text.equals("")) {
                    throttlevalue = " --throttle " + throttle_text;
                } else {
                    throttlevalue = "";
                }
                if (!runtime_text.equals("")) {
                    runtimevalue = runtime_text;
                } else {
                    runtimevalue = "";
                }


                if (runtimevalue == "") {
                    Toast.makeText(getApplicationContext(), "please enter Monkey Test runtime", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(runtimevalue) < 60){
                    Toast.makeText(getApplicationContext(), "至少60s", Toast.LENGTH_SHORT).show();

                }

                else if (package_text == null) {
                    Toast.makeText(getApplicationContext(), "please enter APP package", Toast.LENGTH_SHORT).show();
                } else {
                    Date date = new Date(new Date().getTime());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    String str = format.format(date);

                    file = new File(Constants.MONKEY_PATH + str + "_" + packagename.getText().toString());
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    path = file.getAbsolutePath();
                    time = Integer.parseInt(runtimevalue);
//                    if(android.os.Build.VERSION.RELEASE.startsWith("4")){
//  data/data/com.hjc.scripttool/files/uitest/a4/
                        command_text = " monkey " + package_text
                                + touchvalue + motionwalue + trackballvalue + navvalue + majornavvalue +
                                syskeysvalue + anyeventvalue + appswitchvalue
                                + crashesvalue + timeoutsvalue
                                + " --monitor-native-crashes --ignore-security-exceptions "
                                + " -v -v -v " + throttlevalue
//                            + sendvalue
//                            + killvalue
                                + " " + 99999999
                                + " >>" + path + "/monkeylog.txt"
//                            + " 1>>" + path + "/monkeylog.txt 2>&1 &"
                        ;
//                    }
//                    else if(android.os.Build.VERSION.RELEASE.startsWith("5")){
//                        Log.e(Constants.TAG, "Android 5");
//                        command_text = " /data/data/com.hjc.scripttool/files/uitest/a5/monkey " + package_text
//                                + touchvalue + motionwalue + trackballvalue + navvalue + majornavvalue +
//                                syskeysvalue + anyeventvalue + appswitchvalue
//                                + crashesvalue + timeoutsvalue
//                                + " --monitor-native-crashes --ignore-security-exceptions "
//                                + " -v -v -v " + throttlevalue + sendvalue
////                            + killvalue
//                                + " " + 9999999
//                                + " >>" + path + "/monkeylog.txt"
////                            + " 1>>" + path + "/monkeylog.txt 2>&1 &"
//                        ;
//                    }
//                    else {
//                        Log.e(Constants.TAG, "Android 6");
//                        Toast.makeText(getApplicationContext(), "6.0机器我没适配哦", Toast.LENGTH_SHORT).show();
//                    }


                    if(Settings.getDefaultSharedPreferences(getApplicationContext()).getString(Settings.KEY_EMAIL_TO, "").equals("")){
                        command.setText("monkey 配置完毕!!!" + "\n" + "你没有设置邮箱，结果将只能在本地查看");
                    }
                    else {
                        command.setText("monkey 配置完毕!!!" + "\n" + "报告将发送至：" + Settings.getDefaultSharedPreferences(getApplicationContext()).getString(Settings.KEY_EMAIL_TO, "") + ";" +
                                Settings.getDefaultSharedPreferences(getApplicationContext()).getString(Settings.KEY_EMAIL_CC, ""));

                    }

                    Settings.getDefaultSharedPreferences(getApplicationContext()).edit().putString(Settings.KEY_COMMAND,
                            command_text).putString(Settings.KEY_TRACES, Util.getTracesTime()).commit();




                    isconfirm = true;

                }

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isconfirm == false) {
                    Toast.makeText(getApplicationContext(), "please confirm", Toast.LENGTH_SHORT).show();
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Util.stopLogCat(getApplicationContext());
                        }
                    }).start();


                    Util.startLogcat(getApplicationContext(), file);


                    //logcat路径
                    SharedPreferences logcat_sp = getSharedPreferences(Settings.KEY_LOGCAT_PATH, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor logcat_editor = logcat_sp.edit();
                    logcat_editor.putString(Settings.KEY_LOGCAT_PATH, file.getAbsolutePath());
                    logcat_editor.commit();


                    Settings.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("monkey", true).commit();


                    Thread task = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Settings.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("monkey", true).commit();
                            Log.e(Constants.TAG, Build.MODEL);
                            if(package_text.contains("com.pingan.lifeinsurance") && (!Build.MODEL.contains("H60"))){
                                ShellUtils.execCommand("am start com.pingan.lifeinsurance/.initialize.activity.LauncherActivity", true);
                                try {
                                    Thread.sleep(30000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            ShellUtils.CommandResult result = ShellUtils.execCommand(command_text, true);


                        }
                    });


                    task.start();
                    Log.e(Constants.TAG, "monkey " + task.getName());
                    Settings.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(Constants.TIME, time).commit();

                    String[] pag_split = package_text.split("\\s+");
                    String select_pag = "未知App:" + pag_split[1];
                    Settings.getDefaultSharedPreferences(getApplicationContext()).edit().
                            putString(Settings.KEY_PACKAGE, select_pag).commit();
                    Log.e(Constants.TAG,"指定过滤包名"+ pag_split[1]);

                    startService(new Intent(MonkeyAcitivity.this, MonkeyService.class));
//                    startService(new Intent(MonkeyAcitivity.this, MainService.class));
                    startService(new Intent(MonkeyAcitivity.this, EventService.class));
                    startService(new Intent(MonkeyAcitivity.this, RadioService.class));
                    Intent intent = new Intent(MonkeyAcitivity.this, TimerService.class);
                    intent.putExtra(Constants.TIME, time);
                    startService(intent);
//
                    finish();
                    Util.pressHome(getApplicationContext());

                }

            }

        });

        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ResultListActivity.class);
                startActivity(intent);

            }
        });

        Button btn_package = (Button) this.findViewById(R.id.btn_package);
        btn_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = ProgressDialog.show(MonkeyAcitivity.this, "Loading...", "Please wait...", true, false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.TYPE, Constants.THREE);
                        intent.setClass(getApplicationContext(), PackageActivity.class);
                        startActivityForResult(intent, 100);
                        handler.sendMessage(new Message());
                    }
                }).start();


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            packagename = (EditText) this.findViewById(R.id.packagename);
            packagename.setText(bundle.getString("appname"));
            package_text = bundle.getString("packagename");
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


    public void installPackageSystem(View v) throws IOException, InterruptedException {

        progress = ProgressDialog.show(MonkeyAcitivity.this, "Loading...", "Please wait...", true, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.TYPE, Constants.SYSTEM);
                intent.setClass(MonkeyAcitivity.this, PackageActivity.class);
                startActivityForResult(intent, 100);
                handler.sendMessage(new Message());
            }
        }).start();



    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        isconfirm = false;
    }




    @Override
    public void onStart() {
        super.onStart();


        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "点击事件，在屏幕的任一位置", Toast.LENGTH_LONG).show();

            }
        });

        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "点击事件，可能会伴随着滑动", Toast.LENGTH_LONG).show();

            }
        });

        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "滑动事件，可能会伴随着点击", Toast.LENGTH_LONG).show();

            }
        });

        view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "方向键", Toast.LENGTH_LONG).show();

            }
        });

        view5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "home,back,start call,end call及音量控制）", Toast.LENGTH_LONG).show();

            }
        });

        view6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Activity之前切换", Toast.LENGTH_LONG).show();

            }
        });

        view7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "其它不常用的设备按钮", Toast.LENGTH_LONG).show();

            }
        });

        view8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "5-way键盘的中间按键、回退按键、菜单按键)", Toast.LENGTH_LONG).show();

            }
        });

        view9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "seed相同，则两次Monkey测试所产生的事件序列也相同的。", Toast.LENGTH_LONG).show();

            }
        });

        view10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "用于指定用户操作（即事件）间的时延，单位是毫秒；", Toast.LENGTH_LONG).show();

            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();

    }


}


