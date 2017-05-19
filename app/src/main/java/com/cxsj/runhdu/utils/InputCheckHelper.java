package com.cxsj.runhdu.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * 检查登录注册时输入正确性的封装类
 */

public class InputCheckHelper {
    //错误类型,是用户名错误还是密码错误
    public static final int ERR_USERNAME = 0;
    public static final int ERR_PASSWORD = 1;
    public static final int ERR_PASSWORD_ENSURE = 2;

    //检查回调
    public interface CheckCallback {

        void onPass();

        /**
         * 用户名或密码不合法回调
         *
         * @param which 用户名不合法还是密码不合法的标志
         * @param msg   不合法的原因
         */
        void onFailure(int which, String msg);

    }

    /**
     * 检查输入
     *
     * @param username       用户名
     * @param password       密码
     * @param passwordEnsure 确认密码(可以为空)
     * @param callback       检查回调
     */
    public static void check(String username, String password, @Nullable String passwordEnsure, CheckCallback callback) {
        //确认密码不为空时,检查有效性以及是否与密码一致
        if (passwordEnsure != null) {
            String passwordEnsureMsg = checkPassword(passwordEnsure);
            if (passwordEnsureMsg != null) {
                callback.onFailure(ERR_PASSWORD_ENSURE, passwordEnsureMsg);
                return;
            }

            if (!password.equals(passwordEnsure)) {
                callback.onFailure(ERR_PASSWORD, "两个密码不一致。");
                return;
            }
        }

        String usernameMsg = checkUsername(username);
        if (usernameMsg != null) {
            callback.onFailure(ERR_USERNAME, usernameMsg);
            return;
        }

        String passwordMsg = checkPassword(password);
        if (passwordMsg != null) {
            callback.onFailure(ERR_PASSWORD, passwordMsg);
            return;
        }

        callback.onPass();
    }

    /**
     * @param username 检查的用户名
     * @return 错误信息
     */
    private static String checkUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return "用户名不能为空。";
        }

        if (username.length() < 3 || username.length() > 8) {
            return "用户名长度为3-8个字符。";
        }

        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!((c >= 48 && c <= 57) ||
                    (c >= 65 && c <= 90) ||
                    (c >= 97 && c <= 122) ||
                    (c == 95))) {
                return "用户名只能包含数字、字母及下划线。";
            }
        }

        return null;
    }


    private static String checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return "密码不可以为空。";
        }

        if (password.length() < 6 || password.length() > 20) {
            return "密码长度为6-20个字符。";
        }

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!((c >= 48 && c <= 57) ||
                    (c >= 65 && c <= 90) ||
                    (c >= 97 && c <= 122) ||
                    (c == 95) || (c == 64))) {
                return "密码只能包含数字、字母及下划线。";
            }
        }
        return null;
    }
}