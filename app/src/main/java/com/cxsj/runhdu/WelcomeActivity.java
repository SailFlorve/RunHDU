package com.cxsj.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.AnimationUtil;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.MD5Util;
import com.cxsj.runhdu.utils.Prefs;
import com.cxsj.runhdu.utils.StatusJsonCheckHelper;

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
            Log.d(TAG, "onCreate: !isTaskRoot");
            finish();
            return;
        }
        setContentView(R.layout.activity_welcome);

        loginButton = (Button) findViewById(R.id.welcome_login_button);
        registerButton = (Button) findViewById(R.id.welcome_register_button);

        //用户名不为空时，执行登录，跳转到MainActivity
        if (!TextUtils.isEmpty(username)) {
            doLogin();
        } else {
            initView();
        }

        loginButton.setOnClickListener(v ->
                toActivity(WelcomeActivity.this, LoginActivity.class));

        registerButton.setOnClickListener(v ->
                toActivity(WelcomeActivity.this, RegisterActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (loginButton.getVisibility() != View.GONE) {
            super.onBackPressed();
        }
    }

    private void initView() {
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        loginButton.setAnimation(AnimationUtil.moveToViewLocation());
        registerButton.setAnimation(AnimationUtil.moveToViewLocation());
    }

    private void doLogin() {
        runOnUiThread(() -> HttpUtil.load(URLs.LOGIN)
                .addParam("name", username)
                .addParam("password", (String) prefs.get("MD5Pw", ""))
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(WelcomeActivity.this,
                                    "网络连接失败，请重试。", Toast.LENGTH_LONG).show();
                            initView();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        checkReturn(json);
                    }
                }));
    }

    private void checkReturn(String json) {
        runOnUiThread(() -> {
            closeProgressDialog();
            StatusJsonCheckHelper.check(json, new StatusJsonCheckHelper.CheckCallback() {
                @Override
                public void onPass() {
                    toActivity(WelcomeActivity.this, MainActivity.class);
                    ActivityManager.finishAll();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

                @Override
                public void onFailure(String msg, int which) {
                    Toast.makeText(WelcomeActivity.this,
                            "登录失败。原因：" + msg, Toast.LENGTH_SHORT).show();
                    initView();
                }
            });
        });
    }
}
