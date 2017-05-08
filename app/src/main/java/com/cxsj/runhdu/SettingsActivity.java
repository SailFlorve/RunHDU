package com.cxsj.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.sport.RunningInfo;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.Prefs;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            returnToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static class SettingFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener {

        private String TAG = "SettingFragment";
        private ListPreference targetSteps;
        private ListPreference columnNum;
        private ListPreference locateRate;
        private Preference exitLogin;
        private Preference clearData;
        private Prefs prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 加载xml资源文件
            addPreferencesFromResource(R.xml.preferences);
            prefs = new Prefs(getActivity());
            targetSteps = (ListPreference) findPreference("target_steps");
            columnNum = (ListPreference) findPreference("chart_column_num");
            locateRate = (ListPreference) findPreference("locate_rate");
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
                            exitLogin();
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
            HttpUtil.load(URLs.CLEAR_DATA)
                    .addParam("userName", (String) prefs.get("username", "0"))
                    .post(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "网络连接失败。", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            getActivity().runOnUiThread(() -> {
                                if (result.equals("true")) {
                                    DataSupport.deleteAll(RunningInfo.class);
                                    Toast.makeText(getActivity(), "清除成功。", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "清除失败。", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }

        private void exitLogin() {
            prefs.put("username", "");
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
            MainActivity.mainActivity.finish();
            getActivity().finish();
        }
    }
}
