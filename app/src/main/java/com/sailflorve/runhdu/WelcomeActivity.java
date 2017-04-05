package com.sailflorve.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sailflorve.runhdu.utils.ActivityManager;
import com.sailflorve.runhdu.utils.AnimationUtil;

public class WelcomeActivity extends AppCompatActivity{

    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        loginButton = (Button) findViewById(R.id.real_login_button);
        registerButton = (Button) findViewById(R.id.register_button);

        initView();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
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

    private void initView()
    {
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        loginButton.setAnimation(AnimationUtil.moveToViewLocation());
        registerButton.setAnimation(AnimationUtil.moveToViewLocation());
    }
}
