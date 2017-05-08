package com.cxsj.runhdu.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.cxsj.runhdu.R;
import com.cxsj.runhdu.constant.URLs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用于更新的工具类
 */

public class UpdateUtil {
    public static final int MANUAL = 0;
    public static final int AUTO = 1;

    public static void check(Context context, int type) {
        HttpUtil.load(URLs.CHECK_UPDATE)
                .addParam("version2", context.getResources().getString(R.string.current_version))
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(context, "检查更新失败。", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
    }
}
