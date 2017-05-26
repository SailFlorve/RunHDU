package com.cxsj.runhdu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.controller.DataSyncUtil;
import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sail on 2017/4/13 0013.
 * Base activity to observe.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    protected Prefs prefs;
    protected String username;
    protected boolean isSyncOn = true;
    protected final String TAG = "BaseActivity";
    protected PermissionCallback permissionCallback;

    protected interface PermissionCallback {
        void onGranted();

        void onDenied(List<String> permissions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        prefs = new Prefs(this, username);
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

    protected void showComingSoonDialog() {
        new AlertDialog.Builder(this)
                .setTitle("敬请期待")
                .setMessage("此功能正在开发中，敬请期待。")
                .setPositiveButton("十分期待", null).create().show();
    }

    protected void showProgressDialog(String text) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(text);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
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

    public void requestPermissions(String[] permissions, PermissionCallback callback) {
        permissionCallback = callback;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, permissionList.toArray(new String[permissionList.size()]), 0);
        } else {
            permissionCallback.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0) {
                    List<String> deniedPermission = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            deniedPermission.add(permissions[i]);
                        }
                    }
                    if (deniedPermission.isEmpty()) {
                        permissionCallback.onGranted();
                    } else {
                        permissionCallback.onDenied(deniedPermission);
                    }
                }
                break;
            default:
                break;
        }
    }
}