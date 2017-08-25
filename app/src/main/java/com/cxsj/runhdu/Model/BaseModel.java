package com.cxsj.runhdu.Model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cxsj.runhdu.bean.gson.Status;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SailFlorve on 2017/8/25 0025.
 * 所有model基类
 */

public class BaseModel {

    protected static final String TAG = "";

    protected static final String CONNECT_FAILED = "网络连接失败。";
    protected static final String RETURN_DATA_ERROR = "服务器数据出错。";

    protected static Handler mHandler = new Handler(Looper.getMainLooper());

    public interface BaseCallback {
        void onFailure(String msg);

        void onSuccess();
    }

    /**
     * 检查服务器返回为true还是false
     *
     * @param response
     */
    private static void checkStatusResponse(Response response, BaseCallback callback) throws IOException {
        String result = response.body().string();
        Log.d(TAG, "checkStatusResponse: " + result);
        mHandler.post(() -> {
            if (result.equals("true")) {
                callback.onSuccess();
            } else {
                callback.onFailure(RETURN_DATA_ERROR);
            }
        });
    }

    private static void checkJsonStatusResponse(Response response, BaseCallback callback) throws IOException {
        String jsonStr = response.body().string();
        Log.d(TAG, "checkJsonStatusResponse: " + jsonStr);

        mHandler.post(() -> {
            Status status = null;
            try {
                status = new Gson().fromJson(jsonStr, Status.class);
                if (status.getResult()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(status.getMessage());
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                callback.onFailure(RETURN_DATA_ERROR);
            }
        });
    }

    protected static Callback getStatusHttpCallback(BaseCallback callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(() -> callback.onFailure(CONNECT_FAILED));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                checkStatusResponse(response, callback);
            }
        };
    }

    protected static Callback getJsonStatusHttpCallback(BaseCallback callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(() -> callback.onFailure(CONNECT_FAILED));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                checkJsonStatusResponse(response, callback);
            }
        };
    }
}
