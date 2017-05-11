package com.cxsj.runhdu;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.cxsj.runhdu.utils.Prefs;
import com.cxsj.runhdu.utils.SyncUtil;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addToolbar(R.id.toolbar_settings, true);
        if (savedInstanceState == null) {
            SettingFragment settingFragment = new SettingFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_content, settingFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        returnToMainActivity();
    }

    private void returnToMainActivity() {
        toActivity(this, MainActivity.class);
        finish();
    }

    public static class SettingFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener {
        private Preference exitLogin;
        private Preference clearData;
        private Prefs prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 加载xml资源文件
            addPreferencesFromResource(R.xml.preferences);
            prefs = new Prefs(getActivity());
            exitLogin = findPreference("exit_login");
            clearData = findPreference("clear_data");
            exitLogin.setOnPreferenceClickListener(this);
            clearData.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("exit_login")) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("退出账号")
                        .setMessage("确定退出当前账号？")
                        .setPositiveButton("立即退出", (dialog, which) -> {
                            SettingsActivity activity = (SettingsActivity) getActivity();
                            activity.exitLogin();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {

                        }).create().show();
            } else if (preference.getKey().equals("clear_data")) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("清除数据")
                        .setMessage("确定要清除所有跑步数据吗？")
                        .setPositiveButton("立刻清除", (dialog, which) -> {
                            clearData();
                        })
                        .setNegativeButton("不清除", (dialog, which) -> {

                        }).create().show();
            }
            return true;
        }

//        @Override
//        public boolean onPreferenceChange(Preference preference, Object newValue) {
//            targetSteps.setSummary(newValue + "步");
//            columnNum.setSummary(newValue + "天");
//            locateRate.setSummary(newValue + "秒");
//            return false;
//        }

        private void clearData() {
            SettingsActivity parent = (SettingsActivity) getActivity();
            parent.showProgressDialog("正在删除...");
            String username = (String) prefs.get("username", "0");
            SyncUtil.deleteAllLocalAndServerData(username, new SyncUtil.SyncDataCallback() {
                @Override
                public void onSyncFailure(String msg) {
                    parent.closeProgressDialog();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSyncSuccess() {
                    parent.closeProgressDialog();
                    Toast.makeText(getActivity(), "删除成功。", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
