package com.cxsj.runhdu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.controller.DataSyncUtil;
import com.cxsj.runhdu.model.gson.UpdateInfo;
import com.cxsj.runhdu.utils.Utility;

/**
 * 关于Activity
 */
public class AboutActivity extends BaseActivity {

    private TextView checkVersion;
    private TextView feedback;
    private TextView help;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbar(R.id.about_toolbar, true);

        checkVersion = (TextView) findViewById(R.id.check_version_about);
        checkVersion.setOnClickListener(v -> {

            checkUpdate();
        });

        feedback = (TextView) findViewById(R.id.feedback_about);
        feedback.setOnClickListener(v ->
                toActivity(AboutActivity.this, FeedbackActivity.class));

        help = (TextView) findViewById(R.id.help_about);
        help.setOnClickListener(v ->
                toActivity(AboutActivity.this, HelpActivity.class));
    }

    //检查更新
    private void checkUpdate() {
        showProgressDialog("正在检查更新...");
        DataSyncUtil.checkUpdate(this, new DataSyncUtil.UpdateCheckCallback() {
            @Override
            public void onSuccess(UpdateInfo updateInfo) {
                closeProgressDialog();
                if (updateInfo.isUpdate()) {
                    String dialogStr = "当前版本：" +
                            updateInfo.getCurrentVersion() +
                            "\n最新版本：" +
                            updateInfo.getLatestVersion() +
                            "\n\n" +
                            updateInfo.getStatement();
                    new AlertDialog.Builder(AboutActivity.this)
                            .setTitle("发现新版本")
                            .setMessage(dialogStr)
                            .setPositiveButton("立即升级", (dialog, which) -> {
                                Intent it = new Intent(Intent.ACTION_VIEW,
                                        Utility.getDownloadUri(updateInfo.getLatestVersion()));
                                startActivity(it);
                            })
                            .setNegativeButton("以后再说", null)
                            .create().show();
                } else {
                    Toast.makeText(AboutActivity.this, "未发现新版本。", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String msg) {
                closeProgressDialog();
                Toast.makeText(AboutActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
