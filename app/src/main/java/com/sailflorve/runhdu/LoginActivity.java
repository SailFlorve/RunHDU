package com.sailflorve.runhdu;

import android.content.Intent;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.sailflorve.runhdu.httputils.HttpUtil;
import com.sailflorve.runhdu.utils.ActivityManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private CircularProgressButton loginButton;
    private EditText usernameText;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout pwInputLayout;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (CircularProgressButton) findViewById(R.id.real_login_button);
        usernameText = (EditText) findViewById(R.id.username_text);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        pwInputLayout = (TextInputLayout) findViewById(R.id.pw_input_layout);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        loginButton.setIndeterminateProgressMode(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loginButton.setOnClickListener(this);
        usernameText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.real_login_button:
                doLogin();
                break;

            case R.id.username_text:
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;

            default:
        }
    }

    private void doLogin() {
        usernameInputLayout.setErrorEnabled(false);
        pwInputLayout.setErrorEnabled(false);

        String username = usernameInputLayout.getEditText().getText().toString();
        String password = pwInputLayout.getEditText().getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameInputLayout.setError("用户名不能为空");
            return;
        } else if (TextUtils.isEmpty(password)) {
            pwInputLayout.setError("密码不能为空");
            return;
        }

        loginButton.setProgress(1);
        loginButton.setClickable(false);
        HttpUtil.load("http://112.74.115.231:8080/HelloWorld/HelloForm")
                .addParams("name", usernameInputLayout.getEditText().getText().toString())
                .addParams("password", pwInputLayout.getEditText().getText().toString())
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pwInputLayout.setError("网络连接失败");
                                loginButton.setProgress(0);
                                loginButton.setClickable(true);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        if (result.contains("true")) {
                            loginButton.setProgress(100);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        ActivityManager.finishAll();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pwInputLayout.setError("用户名或密码错误");
                                    loginButton.setProgress(0);
                                    loginButton.setIdleText("重试");
                                    loginButton.setClickable(true);
                                }
                            });
                        }

                    }
                });
    }
}
