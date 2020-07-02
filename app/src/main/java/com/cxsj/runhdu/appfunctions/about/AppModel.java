package com.cxsj.runhdu.appfunctions.about;

import android.content.Context;

import com.cxsj.runhdu.R;
import com.cxsj.runhdu.base.BaseModel;
import com.cxsj.runhdu.bean.gson.UpdateInfo;
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
 * APP相关的数据请求封装
 */

public class AppModel extends BaseModel {

    public interface UpdateCheckCallback {
        void onSuccess(UpdateInfo updateInfo);

        void onFailure(String msg);
    }

    public static void checkUpdate(Context context, AppModel.UpdateCheckCallback callback) {
        final String currentVersion = context.getResources().getString(R.string.current_version);

        HttpUtil.load(URLs.UPDATE_URL)
                .addParam("version2", currentVersion)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onFailure("连接更新服务器失败。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        UpdateInfo updateInfo;
                        try {
                            updateInfo = new Gson().fromJson(response.body().string(), UpdateInfo.class);
                            updateInfo.setCurrentVersion(currentVersion);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            callback.onFailure("服务器错误。");
                            return;
                        }

                        if (updateInfo != null) {
                            mHandler.post(() -> callback.onSuccess(updateInfo));
                        } else {
                            mHandler.post(() -> callback.onFailure("检查更新异常。"));
                        }
                    }
                });
    }
}
