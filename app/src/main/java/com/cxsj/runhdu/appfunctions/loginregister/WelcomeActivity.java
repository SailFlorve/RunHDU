package com.cxsj.runhdu.appfunctions.loginregister;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.cxsj.runhdu.R;
import com.cxsj.runhdu.appfunctions.main.MainActivity;
import com.cxsj.runhdu.base.BaseActivity;
import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.AnimationUtil;

/**
 * 欢迎页面
 */
public class WelcomeActivity extends BaseActivity implements LoginModel.LoginCallback {

    private Button loginButton;
    private Button registerButton;
    private CheckBox cbOffline;

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
        cbOffline = findViewById(R.id.cb_offline);

        //用户名不为空时，执行登录，跳转到MainActivity
        if (!TextUtils.isEmpty(username)) {
            doLogin();
        } else {
            showButtons();
        }

        loginButton.setOnClickListener(v ->
                toActivity(WelcomeActivity.this, LoginActivity.class));

        registerButton.setOnClickListener(v ->
                toActivity(WelcomeActivity.this, RegisterActivity.class));

        cbOffline.setChecked((Boolean) defaultPrefs.get("offline_mode", false));

        cbOffline.setOnCheckedChangeListener((buttonView, isChecked) ->
                defaultPrefs.put("offline_mode", isChecked));
    }

    @Override
    public void onBackPressed() {
        if (loginButton.getVisibility() != View.GONE) {
            super.onBackPressed();
        }
    }

    //显示登录注册按钮
    private void showButtons() {
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        loginButton.setAnimation(AnimationUtil.moveToViewLocation());
        registerButton.setAnimation(AnimationUtil.moveToViewLocation());
    }

    private void doLogin() {
        LoginModel.login(username, (String) defaultPrefs.get("MD5Pw", ""), WelcomeActivity.this);
    }

    @Override
    public void onLoginSuccess() {
        closeProgressDialog();
        toActivity(WelcomeActivity.this, MainActivity.class);
        ActivityManager.finishAll();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onLoginFailure(String msg, int which) {
        closeProgressDialog();
        Toast.makeText(WelcomeActivity.this,
                "登录失败。原因：" + msg, Toast.LENGTH_SHORT).show();
        showButtons();
    }
}
