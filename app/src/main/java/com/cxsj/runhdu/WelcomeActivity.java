package com.cxsj.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.AnimationUtil;
import com.cxsj.runhdu.utils.Prefs;

public class WelcomeActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        ActivityManager.addActivity(this);
        prefs = new Prefs(this);
        if (!TextUtils.isEmpty((String) prefs.get("username", null))) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            ActivityManager.finishAll();
        }

        setContentView(R.layout.activity_welcome);

        loginButton = (Button) findViewById(R.id.welcome_login_button);
        registerButton = (Button) findViewById(R.id.welcome_register_button);

        initView();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        loginButton.setAnimation(AnimationUtil.moveToViewLocation());
        registerButton.setAnimation(AnimationUtil.moveToViewLocation());
    }
}
