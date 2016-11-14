package com.hjc.scripttool.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hjc.scripttool.R;
import com.hjc.scripttool.report.MonkeyReport;
import com.hjc.scriptutil.mail.Email;
import com.hjc.scriptutil.mail.EmailService;
import com.hjc.scriptutil.mail.SendEmail;
import com.hjc.scriptutil.tools.Settings;
import com.hjc.util.Constants;
import com.hjc.util.Util;
import com.hjc.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujiachun on 15/12/15.
 */
public class MonkeyResultActivity extends Activity{
    TextView anr, crash;
    Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monkeyresult);
        anr = (TextView) this.findViewById(R.id.anr);
        crash = (TextView) this.findViewById(R.id.crash);
        sendBtn = (Button) this.findViewById(R.id.sendemail);

        final Intent intent = getIntent();
        anr.setText("(" + intent.getIntExtra(Constants.ANR, 0) + ")");
        crash.setText("(" + intent.getIntExtra(Constants.CRASH, 0) + ")");

        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               new SendEmail().excute(getApplicationContext(), intent.getStringExtra(Constants.CLICK_HISTORY));
            }
        });

    }


}
