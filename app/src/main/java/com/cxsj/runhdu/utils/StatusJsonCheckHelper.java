package com.cxsj.runhdu.utils;

import com.cxsj.runhdu.model.gson.Status;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Sail on 2017/5/21 0021.
 * 登录注册时返回的JSON检查。
 */

public class StatusJsonCheckHelper {
    public interface CheckCallback {
        void onPass();

        void onFailure(String msg, int which);
    }

    public static void check(String json, CheckCallback callback) {
        Status status = null;
        try {
            status = new Gson().fromJson(json, Status.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (status == null) {
            callback.onFailure("JSON格式错误。", 0);
        } else {
            if (status.getResult()) {
                callback.onPass();
            } else {
                callback.onFailure(status.getMessage(), status.getWhich());
            }
        }
    }
}
