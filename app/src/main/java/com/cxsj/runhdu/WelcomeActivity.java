package com.cxsj.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.AnimationUtil;
import com.cxsj.runhdu.utils.Prefs;

public class WelcomeActivity extends BaseActivity {

    private Button loginButton;
    private Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }

        //用户名不为空时，执行登录，跳转到MainActivity
        if (!TextUtils.isEmpty(username)) {
            toActivity(this, MainActivity.class);
            ActivityManager.finishAll();
        }

        setContentView(R.layout.activity_welcome);

        loginButton = (Button) findViewById(R.id.welcome_login_button);
        registerButton = (Button) findViewById(R.id.welcome_register_button);

        initView();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivity(WelcomeActivity.this, LoginActivity.class);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivity(WelcomeActivity.this, RegisterActivity.class);
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
