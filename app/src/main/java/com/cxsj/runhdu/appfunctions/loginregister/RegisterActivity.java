package com.cxsj.runhdu.appfunctions.loginregister;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cxsj.runhdu.R;
import com.cxsj.runhdu.base.BaseActivity;
import com.cxsj.runhdu.utils.InputCheckHelper;
import com.cxsj.runhdu.utils.MD5Util;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, LoginModel.LoginCallback {

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
                        LoginModel.register(username, MD5Util.encode(password), RegisterActivity.this);
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

    @Override
    public void onLoginSuccess() {
        Toast.makeText(RegisterActivity.this, "注册成功。", Toast.LENGTH_SHORT).show();
        toActivity(RegisterActivity.this, LoginActivity.class);
        finish();
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