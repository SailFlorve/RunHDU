package com.cxsj.runhdu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.controller.DataSyncUtil;

public class AboutActivity extends BaseActivity {

    private TextView checkVersion;
    private TextView feedback;
    private TextView help;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbar(R.id.about_toolbar, true);

        checkVersion = (TextView) findViewById(R.id.check_version_about);
        checkVersion.setOnClickListener(v -> {
            showProgressDialog("正在检查更新...");
            checkUpdate(this);
        });

        feedback = (TextView) findViewById(R.id.feedback_about);
        feedback.setOnClickListener(v ->
                toActivity(AboutActivity.this, FeedbackActivity.class));

        help = (TextView) findViewById(R.id.help_about);
        help.setOnClickListener(v ->
                toActivity(AboutActivity.this, HelpActivity.class));
    }
}
