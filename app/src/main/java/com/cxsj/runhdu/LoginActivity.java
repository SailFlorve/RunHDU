package com.cxsj.runhdu;

import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cxsj.runhdu.Model.BaseModel;
import com.cxsj.runhdu.Model.LoginModel;
import com.cxsj.runhdu.utils.InputCheckHelper;
import com.cxsj.runhdu.utils.MD5Util;
import com.cxsj.runhdu.utils.StatusJsonCheckHelper;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.ActivityManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginModel.LoginCallback {

    private Button loginButton;
    private EditText usernameText;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout pwInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.login_button);
        usernameText = (EditText) findViewById(R.id.username_text);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        pwInputLayout = (TextInputLayout) findViewById(R.id.pw_input_layout);
        setToolbar(R.id.login_toolbar, true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loginButton.setOnClickListener(this);
        usernameText.setOnClickListener(this);

        if (!TextUtils.isEmpty(username)) {
            usernameText.setText(username);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                usernameInputLayout.setErrorEnabled(false);
                pwInputLayout.setErrorEnabled(false);

                String username = usernameInputLayout.getEditText().getText().toString();
                String password = pwInputLayout.getEditText().getText().toString();
                //检查输入
                InputCheckHelper.check(username, password, null, new InputCheckHelper.CheckCallback() {
                    @Override
                    public void onPass() {
                        showProgressDialog("正在登录...");
                        LoginModel.login(username, MD5Util.encode(password), LoginActivity.this);
                    }

                    @Override
                    public void onFailure(int which, String msg) {
                        if (which == InputCheckHelper.ERR_USERNAME) {
                            usernameInputLayout.setError(msg);
                        } else if (which == InputCheckHelper.ERR_PASSWORD) {
                            pwInputLayout.setError(msg);
                        }
                    }
                });
                break;
            default:
        }
    }

    @Override
    public void onLoginSuccess() {
        showProgressDialog("登录成功...");
        usernameInputLayout.setEnabled(false);
        defaultPrefs.put("username", usernameInputLayout.getEditText().getText().toString());
        defaultPrefs.put("MD5Pw", MD5Util.encode(pwInputLayout.getEditText().getText().toString()));
        toActivity(LoginActivity.this, MainActivity.class);
        ActivityManager.finishAll();
    }

    @Override
    public void onLoginFailure(String msg, int which) {
        closeProgressDialog();
        if (which == InputCheckHelper.ERR_USERNAME) {
            usernameInputLayout.setError(msg);
        } else {
            pwInputLayout.setError(msg);
        }
    }
}
