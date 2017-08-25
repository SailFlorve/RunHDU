package com.cxsj.runhdu.Model;

import com.cxsj.runhdu.bean.gson.Status;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SailFlorve on 2017/8/25 0025.
 * 登录与注册数据请求
 */

public class LoginModel extends BaseModel {
    public interface LoginCallback {
        void onLoginSuccess();

        void onLoginFailure(String msg, int which);
    }

    private static Callback getCallback(LoginCallback callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(() -> callback.onLoginFailure(CONNECT_FAILED, 0));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    mHandler.post(() -> {
                        Status status = null;
                        status = new Gson().fromJson(json, Status.class);
                        if (status == null) {
                            callback.onLoginFailure(RETURN_DATA_ERROR, 0);
                        } else {
                            if (status.getResult()) {
                                callback.onLoginSuccess();
                            } else {
                                callback.onLoginFailure(status.getMessage(), status.getWhich());
                            }
                        }
                    });
                } catch (JsonSyntaxException e) {
                    callback.onLoginFailure(RETURN_DATA_ERROR, 0);
                    e.printStackTrace();
                }
            }
        };
    }

    public static void login(String username, String passwordMD5, LoginCallback callback) {
        HttpUtil.load(URLs.LOGIN)
                .addParam("name", username)
                .addParam("password", passwordMD5)
                .post(getCallback(callback));
    }

    public static void register(String username, String passwordMD5, LoginCallback callback) {
        HttpUtil.load(URLs.REGISTER)
                //传加密后的用户名and密码
                .addParam("name", username)
                .addParam("password", passwordMD5)
                .post(getCallback(callback));
    }
}
