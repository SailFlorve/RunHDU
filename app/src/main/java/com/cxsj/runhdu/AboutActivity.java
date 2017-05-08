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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setSupportActionBar((Toolbar) findViewById(R.id.about_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        checkVersion = (TextView) findViewById(R.id.check_version_about);
        checkVersion.setOnClickListener(v -> {
            checkUpdate(this);
        });

        feedback = (TextView) findViewById(R.id.feedback_about);
        feedback.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        help = (TextView) findViewById(R.id.help_about);
        help.setOnClickListener(v -> {
            showComingSoonDialog();
        });
    }
}
