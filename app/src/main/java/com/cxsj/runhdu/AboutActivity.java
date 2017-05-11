package com.cxsj.runhdu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

    private TextView checkVersion;
    private TextView feedback;
    private TextView help;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        addToolbar(R.id.about_toolbar, true);

        checkVersion = (TextView) findViewById(R.id.check_version_about);
        checkVersion.setOnClickListener(v -> {
            checkUpdate(this);
        });

        feedback = (TextView) findViewById(R.id.feedback_about);
        feedback.setOnClickListener(v -> {
            toActivity(AboutActivity.this, FeedbackActivity.class);
        });

        help = (TextView) findViewById(R.id.help_about);
        help.setOnClickListener(v -> {
            showComingSoonDialog();
        });
    }
}
