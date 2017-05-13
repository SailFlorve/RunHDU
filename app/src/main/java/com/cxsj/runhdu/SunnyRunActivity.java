package com.cxsj.runhdu;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxsj.runhdu.adapters.SunnyRunRecyclerViewAdapter;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.gson.StudentInfo;
import com.cxsj.runhdu.sport.SunnyRunInfo;
import com.cxsj.runhdu.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SunnyRunActivity extends BaseActivity {

    private RecyclerView sunnyRunRecyclerView;
    private SunnyRunRecyclerViewAdapter adapter;
    private TextView listTopText;
    private TextInputLayout usernameText;
    private Button loginButton;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout loginLayout;
    private List<SunnyRunInfo> infoList = new ArrayList<>();
    private StudentInfo studentInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunny_run);
        usernameText = (TextInputLayout) findViewById(R.id.sunny_run_username_edit_text);
        listTopText = (TextView) findViewById(R.id.sun_list_top_text);
        loginButton = (Button) findViewById(R.id.sunny_run_login_button);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.sunny_refresh);
        loginLayout = (LinearLayout) findViewById(R.id.sunny_run_login_layout);
        setToolbar(R.id.sunny_run_toolbar, true);
        refreshLayout.setEnabled(false);

        initRecyclerView();
        checkLogin();

        loginButton.setOnClickListener(v -> {
            //收回输入法
            InputMethodManager imm = (InputMethodManager)
                    getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(refreshLayout.getWindowToken(), 0);
            if (checkInput()) {
                prefs.put("sunny_run_username", usernameText.getEditText().getText().toString());
                getData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sunny_run_activty_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.cancel_login:
                cancelLogin();
                break;

            case R.id.refresh_sun_list:
                if (refreshLayout.isRefreshing()) return true;
                if (loginLayout.getVisibility() != View.VISIBLE) {
                    clearData();
                    checkLogin();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //检查是否已经登录
    private void checkLogin() {
        loginLayout.setVisibility(View.INVISIBLE);
        usernameText.getEditText().setText((String) prefs.get("sunny_run_username", ""));
        if ((boolean) prefs.get("sunny_run_is_login", false)) {
            getData();
        } else {
            loginLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        sunnyRunRecyclerView = (RecyclerView) findViewById(R.id.sunny_run_list);
        adapter = new SunnyRunRecyclerViewAdapter(R.layout.sunny_run_list_item, infoList);
        adapter.openLoadAnimation();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        sunnyRunRecyclerView.setLayoutManager(layoutManager);
        sunnyRunRecyclerView.setAdapter(adapter);
    }

    private void cancelLogin() {
        refreshLayout.setRefreshing(false);
        prefs.put("sunny_run_is_login", false);
        loginLayout.setVisibility(View.VISIBLE);
        clearData();
    }

    private void getData() {
        queryStudentInfo((String) prefs.get("sunny_run_username", ""));
    }

    private void queryStudentInfo(final String username) {
        refreshLayout.setRefreshing(true);
        HttpUtil.load(URLs.SUNNY_RUN_INFO_API + username)
                .get(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(SunnyRunActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                            refreshLayout.setRefreshing(false);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Gson gson = new Gson();
                        studentInfo = gson.fromJson(response.body().string(), StudentInfo.class);
                        Log.d(TAG, "onResponse: " + studentInfo.getName());
                        runOnUiThread(() -> {
                            if ("该用户不存在".equals(studentInfo.getState())) {
                                refreshLayout.setRefreshing(false);
                                usernameText.setError("学号不存在。");
                                cancelLogin();
                            } else {
                                prefs.put("sunny_run_username", username);
                                getListFromAPI();
                            }
                        });
                    }
                });

    }

    private void getListFromAPI() {
        refreshLayout.setRefreshing(true);
        loginLayout.setVisibility(View.INVISIBLE);
        clearData();
        HttpUtil.load(URLs.SUNNY_RUN_QUERY_API + prefs.get("sunny_run_username", ""))
                .get(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(SunnyRunActivity.this, "请检查网络。", Toast.LENGTH_SHORT).show();
                            refreshLayout.setRefreshing(false);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int times = 0;
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray = parser.parse(response.body().string()).getAsJsonArray();
                        Gson gson = new Gson();
                        for (JsonElement info : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            SunnyRunInfo runInfo = gson.fromJson(info, SunnyRunInfo.class);
                            runInfo.setNumber(String.valueOf(++times));
                            infoList.add(runInfo);
                        }
                        prefs.put("sunny_run_is_login", true);
                        Collections.reverse(infoList);
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            listTopText.setText(String.format("%s同学，你已经跑了%s次，共%s米。",
                                    studentInfo.getName(), studentInfo.getValidTimes(), studentInfo.getMileages()));
                            refreshLayout.setRefreshing(false);
                        });
                    }
                });
    }

    private void clearData() {
        infoList.clear();
        adapter.notifyDataSetChanged();
        listTopText.setText("");
    }

    private boolean checkInput() {
        usernameText.setErrorEnabled(false);
        String username = usernameText.getEditText().getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameText.setError("学号不能为空。");
            return false;
        }

//        if (TextUtils.isEmpty(password)) {
//            pwText.setError("密码不能为空。");
//            return false;
//        }
//        if (password.contains(" ")) {
//            pwText.setError("密码中有非法字符。");
//            return false;
//        }
        return true;
    }
}
