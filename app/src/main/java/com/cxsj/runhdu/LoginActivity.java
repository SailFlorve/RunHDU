package com.cxsj.runhdu;

import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.ActivityManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private CircularProgressButton loginButton;
    private EditText usernameText;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout pwInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (CircularProgressButton) findViewById(R.id.login_button);
        usernameText = (EditText) findViewById(R.id.username_text);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        pwInputLayout = (TextInputLayout) findViewById(R.id.pw_input_layout);
        loginButton.setIndeterminateProgressMode(true);
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
                String username = usernameInputLayout.getEditText().getText().toString();
                String password = pwInputLayout.getEditText().getText().toString();
                if (checkUsername(username, password)) {
                    loginButton.setProgress(1);
                    loginButton.setClickable(false);
                    HttpUtil.load(URLs.LOGIN)
                            .addParam("name", username)
                            .addParam("password", password)
                            .post(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(() -> {
                                        pwInputLayout.setError("网络连接失败。");
                                        loginButton.setProgress(0);
                                        loginButton.setClickable(true);
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String result = response.body().string();
                                    runOnUiThread(() -> checkReturn(result));

                                }
                            });
                }
                break;
            default:
        }
    }

    private boolean checkUsername(String username, String password) {
        usernameInputLayout.setErrorEnabled(false);
        pwInputLayout.setErrorEnabled(false);

        if (TextUtils.isEmpty(username)) {
            usernameInputLayout.setError("用户名不能为空");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            pwInputLayout.setError("密码不能为空");
            return false;
        }

        if (username.length() < 3 || username.length() > 8) {
            usernameInputLayout.setError("用户名长度为3-8个字符。");
            return false;
        }

        if (password.length() < 6 || password.length() > 20) {
            pwInputLayout.setError("密码长度为6-20个字符。");
            return false;
        }

        //检查用户名每个字符有效性
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!((c >= 48 && c <= 57) ||
                    (c >= 65 && c <= 90) ||
                    (c >= 97 && c <= 122) ||
                    (c == 95))) {
                usernameInputLayout.setError("用户名只能包含数字、字母及下划线。");
                return false;
            }
        }

        //检查密码每个字符有效性
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!((c >= 48 && c <= 57) ||
                    (c >= 65 && c <= 90) ||
                    (c >= 97 && c <= 122) ||
                    (c == 95) ||
                    (c == 64))) {
                pwInputLayout.setError("密码只能包含数字、字母、下划线及@。");
                return false;
            }
        }
        return true;
    }

    private void checkReturn(String res) {
        Log.i("Login", res);
        if (res.contains("ERROR")) {
            usernameInputLayout.setError("用户名不符合要求。");
            loginButton.setProgress(0);
            loginButton.setIdleText("重试");
            loginButton.setClickable(true);

        } else if (res.contains("false")) {
            pwInputLayout.setError("密码错误。");
            loginButton.setProgress(0);
            loginButton.setIdleText("重试");
            loginButton.setClickable(true);

        } else if (res.contains("true")) {
            usernameInputLayout.setEnabled(false);
            prefs.put("username", usernameInputLayout.getEditText().getText().toString());
            loginButton.setProgress(100);
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    toActivity(LoginActivity.this, MainActivity.class);
                    ActivityManager.finishAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            Toast.makeText(this, "服务器未知错误。", Toast.LENGTH_SHORT).show();
            loginButton.setProgress(0);
            loginButton.setIdleText("重试");
            loginButton.setClickable(true);
        }
    }
}
