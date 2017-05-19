package com.cxsj.runhdu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.model.gson.UpdateInfo;
import com.cxsj.runhdu.utils.ActivityManager;
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
    private Context mContext;
    private ProgressDialog progressDialog;
    protected Prefs prefs;
    protected String username;
    protected boolean isSyncOn = true;
    protected final String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        prefs = new Prefs(this);
        username = (String) prefs.get("username", "");
        isSyncOn = (boolean) prefs.get("sync_data", true);

    }

    @Override
    protected void onDestroy() {
        ActivityManager.removeActivity(this);
        super.onDestroy();
    }

    protected void setToolbar(int toolbarResId, boolean haveBackButton) {
        setSupportActionBar((Toolbar) findViewById(toolbarResId));
        if (haveBackButton) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void checkUpdate(Context context) {
        showProgressDialog("正在检查更新...");
        HttpUtil.load(URLs.UPDATE_URL)
                .addParam("version2",
                        getResources().getString(R.string.current_version))
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (context instanceof AboutActivity) {
                            runOnUiThread(() -> {
                                closeProgressDialog();
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
                        if (info == null) return;
                        String currentVersion = getResources().getString(R.string.current_version);
                        String ignoreVersion = (String) prefs.get("ignore_version", "");
                        UpdateInfo finalInfo = info;
                        runOnUiThread(() -> {
                            closeProgressDialog();
                            switch (finalInfo.isUpdate) {
                                case "true":
                                    //如果MainActivity检查更新，且已忽略此版本

                                    if (context instanceof MainActivity
                                            && ignoreVersion.equals(currentVersion)) return;


                                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                            .setTitle("版本更新")
                                            .setMessage("当前版本："
                                                    + getResources().getString(R.string.current_version)
                                                    + "\n最新版本："
                                                    + finalInfo.latestVersion
                                                    + "\n\n"
                                                    + finalInfo.statement)
                                            .setPositiveButton("立即升级", null)
                                            .setNegativeButton("稍后提醒", null);
                                    if (context instanceof MainActivity) {
                                        builder.setNeutralButton("忽略此版本",
                                                (dialog, which) -> prefs.put("ignore_version", currentVersion));
                                    }
                                    builder.create().show();
                                    break;
                                case "false":
                                    if (context instanceof AboutActivity) {
                                        Toast.makeText(context, "未发现新版本。", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                default:
                                    if (context instanceof AboutActivity) {
                                        Toast.makeText(context, "服务器异常。", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        });
                    }
                });
    }


    protected void showComingSoonDialog() {
        new AlertDialog.Builder(this)
                .setTitle("敬请期待")
                .setMessage("此功能正在开发中，敬请期待。")
                .setPositiveButton("十分期待", null).create().show();
    }

    protected void showProgressDialog(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(text);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    protected void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected void exitLogin() {
        prefs.put("username", "");
        ActivityManager.finishAll();
        toActivity(this, WelcomeActivity.class);
    }

    protected void toActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }
}