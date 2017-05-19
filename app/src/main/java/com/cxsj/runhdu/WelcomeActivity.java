package com.cxsj.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.AnimationUtil;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.MD5Util;
import com.cxsj.runhdu.utils.Prefs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
        setContentView(R.layout.activity_welcome);

        loginButton = (Button) findViewById(R.id.welcome_login_button);
        registerButton = (Button) findViewById(R.id.welcome_register_button);

        //用户名不为空时，执行登录，跳转到MainActivity
        if (!TextUtils.isEmpty(username)) {
            showProgressDialog("正在登录...");
            HttpUtil.load(URLs.LOGIN)
                    .addParam("name", username)
                    .addParam("password", (String) prefs.get("MD5Pw", ""))
                    .post(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> {
                                Toast.makeText(WelcomeActivity.this,
                                        "网络连接失败！", Toast.LENGTH_SHORT).show();
                                closeProgressDialog();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.body().string().equals("true")) {
                                toActivity(WelcomeActivity.this, MainActivity.class);
                                ActivityManager.finishAll();
                            } else {
                                runOnUiThread(() -> {
                                    closeProgressDialog();
                                    Toast.makeText(WelcomeActivity.this,
                                            "登录失败。", Toast.LENGTH_SHORT).show();
                                    initView();
                                });
                            }
                        }
                    });
        } else {
            initView();
        }

        loginButton.setOnClickListener(v ->
                toActivity(WelcomeActivity.this, LoginActivity.class));

        registerButton.setOnClickListener(v ->
                toActivity(WelcomeActivity.this, RegisterActivity.class));
    }

    private void initView() {
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        loginButton.setAnimation(AnimationUtil.moveToViewLocation());
        registerButton.setAnimation(AnimationUtil.moveToViewLocation());
    }
}
