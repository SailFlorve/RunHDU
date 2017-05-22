package com.cxsj.runhdu;

import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.InputCheckHelper;
import com.cxsj.runhdu.utils.MD5Util;
import com.cxsj.runhdu.utils.StatusJsonCheckHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Button registerButton;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout pwInputLayout;
    private TextInputLayout pwEnsureLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerButton = (Button) findViewById(R.id.register_button);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        pwInputLayout = (TextInputLayout) findViewById(R.id.pw_input_layout);
        pwEnsureLayout = (TextInputLayout) findViewById(R.id.pw_ensure_input_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setToolbar(R.id.register_toolbar, true);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                usernameInputLayout.setErrorEnabled(false);
                pwInputLayout.setErrorEnabled(false);
                pwEnsureLayout.setErrorEnabled(false);

                String username = usernameInputLayout.getEditText().getText().toString();
                String password = pwInputLayout.getEditText().getText().toString();
                String passwordEnsure = pwEnsureLayout.getEditText().getText().toString();

                InputCheckHelper.check(username, password, passwordEnsure, new InputCheckHelper.CheckCallback() {
                    @Override
                    public void onPass() {
                        showProgressDialog("正在注册...");
                        HttpUtil.load(URLs.REGISTER)
                                //传加密后的用户名and密码
                                .addParam("name", username)
                                .addParam("password", MD5Util.encode(password))
                                .post(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(() -> {
                                            closeProgressDialog();
                                            Toast.makeText(RegisterActivity.this,
                                                    "网络连接失败。", Toast.LENGTH_SHORT).show();
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final String res = response.body().string();
                                        runOnUiThread(() -> {
                                            closeProgressDialog();
                                            checkReturn(res);
                                        });
                                    }
                                });
                    }

                    @Override
                    public void onFailure(int which, String msg) {
                        switch (which) {
                            case InputCheckHelper.ERR_USERNAME:
                                usernameInputLayout.setError(msg);
                                break;
                            case InputCheckHelper.ERR_PASSWORD:
                                pwInputLayout.setError(msg);
                                break;
                            case InputCheckHelper.ERR_PASSWORD_ENSURE:
                                pwEnsureLayout.setError(msg);
                                break;
                            default:

                        }
                    }
                });
                break;
        }
    }

    private void checkReturn(String res) {
        Log.i("TAG", res);
        StatusJsonCheckHelper.check(res, new StatusJsonCheckHelper.CheckCallback() {
            @Override
            public void onPass() {
                Toast.makeText(RegisterActivity.this, "注册成功。", Toast.LENGTH_SHORT).show();
                toActivity(RegisterActivity.this, LoginActivity.class);
                finish();
            }

            @Override
            public void onFailure(String msg, int which) {
                if (which == 0) {
                    usernameInputLayout.setError(msg);
                } else {
                    pwInputLayout.setError(msg);
                }
            }
        });
    }
}