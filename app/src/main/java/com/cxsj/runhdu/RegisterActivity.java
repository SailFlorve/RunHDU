package com.cxsj.runhdu;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout pwInputLayout;
    private TextInputLayout pwEnsureLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerButton = (Button) findViewById(R.id.register_button);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        pwInputLayout = (TextInputLayout) findViewById(R.id.pw_input_layout);
        pwEnsureLayout = (TextInputLayout) findViewById(R.id.pw_ensure_input_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setSupportActionBar((Toolbar) findViewById(R.id.register_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                String username = usernameInputLayout.getEditText().getText().toString();
                String password = pwInputLayout.getEditText().getText().toString();
                String passwordEnsure = pwEnsureLayout.getEditText().getText().toString();

                if (checkInputs(username, password, passwordEnsure)) {
                    HttpUtil.load(URLs.REGISTER)
                            .addParam("name", username)
                            .addParam("password",password)
                            .post(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Toast.makeText(RegisterActivity.this, "网络连接失败。", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String res = response.body().string();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            checkReturn(res);
                                        }
                                    });
                                }
                            });
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkInputs(String username, String password, String passwordEnsure) {
        usernameInputLayout.setErrorEnabled(false);
        pwInputLayout.setErrorEnabled(false);
        pwEnsureLayout.setErrorEnabled(false);

        if (TextUtils.isEmpty(username)) {
            usernameInputLayout.setError("用户名不能为空。");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            pwInputLayout.setError("密码不能为空。");
            return false;
        }

        if (TextUtils.isEmpty(passwordEnsure)) {
            pwEnsureLayout.setError("确认密码不能为空。");
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

        if (!password.equals(passwordEnsure)) {
            pwEnsureLayout.setError("两个密码不一致。");
            return false;
        }
        return true;
    }

    private void checkReturn(String res) {
        Log.i("TAG", res);
        if (res.contains("ERROR")) {
            usernameInputLayout.setError("用户名和密码不符合要求。");
        } else if (res.contains("FALSE")) {
            usernameInputLayout.setError("用户名已经存在。");
        } else if (res.contains("false")) {
            Toast.makeText(this, "注册失败。", Toast.LENGTH_SHORT).show();
        } else if (res.contains("true")) {
            Toast.makeText(this, "注册成功。", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "服务器未知错误。", Toast.LENGTH_SHORT).show();
        }
    }
}
