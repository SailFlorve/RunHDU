package com.cxsj.runhdu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.gson.UpdateInfo;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Sail on 2017/4/13 0013.
 * Base activity to observe.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private final String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: ");
        super.onNewIntent(intent);
    }

    protected void checkUpdate(Context context) {
        Prefs prefs = new Prefs(context);
        HttpUtil.load(URLs.UPDATE_URL)
                .addParam("version2",
                        getResources().getString(R.string.current_version))
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if ((Activity) context instanceof AboutActivity) {
                            runOnUiThread(() -> {
                                Toast.makeText(context, "连接更新服务器失败。", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        UpdateInfo info = null;
                        try {
                            info = new Gson().fromJson(response.body().string(), UpdateInfo.class);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if(info==null) return;
                        String currentVersion = getResources().getString(R.string.current_version);
                        String ignoreVersion = (String) prefs.get("ignore_version", "");
                        UpdateInfo finalInfo = info;
                        runOnUiThread(() -> {
                            if (finalInfo.isUpdate.equals("true")) {
                                //如果MainActivity检查更新，且已忽略此版本

                                if ((Activity) context instanceof MainActivity
                                        && ignoreVersion.equals(currentVersion)) return;


                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setTitle("版本更新")
                                        .setMessage("当前版本："
                                                + getResources().getString(R.string.current_version)
                                                + "\n最新版本："
                                                + finalInfo.latestVersion
                                                + "\n\n"
                                                + finalInfo.statement)
                                        .setPositiveButton("立即升级", (dialog, which) -> {
                                        })
                                        .setNegativeButton("稍后提醒", (dialog, which) -> {
                                        });
                                if ((Activity) context instanceof MainActivity) {
                                    builder.setNeutralButton("忽略此版本", (dialog, which) -> {
                                        prefs.put("ignore_version", currentVersion);
                                    });
                                }
                                builder.create().show();
                            } else if (finalInfo.isUpdate.equals("false")) {
                                if ((Activity) context instanceof AboutActivity) {
                                    Toast.makeText(context, "未发现新版本。", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if ((Activity) context instanceof AboutActivity) {
                                    Toast.makeText(context, "服务器异常。", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    protected void showComingSoonDialog() {
        new AlertDialog.Builder(this)
                .setTitle("敬请期待")
                .setMessage("此功能正在开发中，敬请期待。")
                .setPositiveButton("十分期待", (dialog, which) -> {
                }).create().show();
    }
}

