package com.sailflorve.runhdu;

import android.content.Context;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.sailflorve.runhdu.adapters.SunnyRunRecyclerViewAdapter;
import com.sailflorve.runhdu.sport.SunnyRunInfo;
import com.sailflorve.runhdu.utils.HttpUtil;
import com.sailflorve.runhdu.utils.Prefs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SunnyRunActivity extends AppCompatActivity {

    private RecyclerView sunnyRunRecyclerView;
    private SunnyRunRecyclerViewAdapter adapter;
    private TextView listTopText;
    private TextInputLayout usernameText;
    private TextInputLayout pwText;
    private Button loginButton;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout loginLayout;
    private Prefs prefs;
    private List<SunnyRunInfo> infoList = new ArrayList<>();

    private final String QUERY_URL = "http://hdu.sunnysport.org.cn/runner/achievements.html";
    private final String INDEX_URL = "http://hdu.sunnysport.org.cn/runner/index.html";
    private final String LOGIN_URL = "http://hdu.sunnysport.org.cn/login/";
    private final String TAG = "SunnyRunActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunny_run);
        prefs = new Prefs(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.sunny_run_activity_bar);
        usernameText = (TextInputLayout) findViewById(R.id.username_edit_text);
        pwText = (TextInputLayout) findViewById(R.id.pw_edit_text);
        listTopText = (TextView) findViewById(R.id.sun_list_top_text);
        loginButton = (Button) findViewById(R.id.login_button);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.sunny_refresh);
        loginLayout = (LinearLayout) findViewById(R.id.login_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("阳光长跑");
        refreshLayout.setEnabled(false);

        initRecyclerView();
        checkCookie();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收回输入法
                InputMethodManager imm = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(refreshLayout.getWindowToken(), 0);
                requestCookie();
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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.cancel_login) {
            cancelLogin();
        }
        if (item.getItemId() == R.id.refresh_sun_list) {
            checkCookie();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkCookie() {
        if (TextUtils.isEmpty((String) prefs.get("cookie", null))) {
            loginLayout.setVisibility(View.VISIBLE);
        } else {
            getListFromHtml();
        }
    }

    private void requestCookie() {
        loginLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(true);
        HttpUtil.HttpRequest.url(LOGIN_URL)
                .add("username", usernameText.getEditText().getText().toString())
                .add("password", pwText.getEditText().getText().toString())
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.loop();
                        Toast.makeText(SunnyRunActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                        Looper.prepare();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: enter");
                        String setCookie = response.header("Set-Cookie");
                        if (setCookie != null) {
                            Log.d("setCookie", setCookie);
                            prefs.put("cookie", setCookie.split(";")[0]);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getListFromHtml();
                                }
                            });
                        } else {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            pwText.setError("用户名或密码错误");
                                            refreshLayout.setRefreshing(false);
                                        }
                                    }
                            );

                        }
                    }
                });
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
        usernameText.getEditText().setText(null);
        pwText.getEditText().setText(null);
        pwText.setErrorEnabled(false);
        prefs.put("cookie", "");
        checkCookie();
    }

    private void getListFromHtml() {
        Log.d("1", (String) prefs.get("cookie", null));
        refreshLayout.setRefreshing(true);
        loginLayout.setVisibility(View.GONE);
        infoList.clear();
        HttpUtil.HttpRequest.url(QUERY_URL)
                .header("cookie", (String) prefs.get("cookie", null))
                .get(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SunnyRunActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        if (str.contains("用户登录")) {
                            runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  Toast.makeText(SunnyRunActivity.this,
                                                          "登录出现异常，请重试。", Toast.LENGTH_SHORT).show();
                                                  cancelLogin();
                                                  refreshLayout.setRefreshing(false);
                                              }
                                          }
                            );
                            return;
                        }
                        Document doc = Jsoup.parse(str);
                        final String topText = doc.getElementsByClass("nav navbar-nav navbar-right")
                                .get(0).select("li").get(0).select("a").text()
                                + "同学，以下是你的阳光长跑情况：";
                        Elements trs = doc.select("table").select("tr");
                        for (int i = 0; i < trs.size(); i++) {
                            Elements tds = trs.get(i).select("td");
                            String[] strings = new String[tds.size()];
                            for (int j = 0; j < tds.size(); j++) {
                                String text = tds.get(j).text();
                                strings[j] = text.split("m")[0].trim();
                                if (tds.get(j).select("span").size() > 0) {
                                    Log.d("name", tds.get(j).select("span").get(0).className());
                                    if (tds.get(j).select("span").get(0).className().contains("ok")) {
                                        strings[j] = "ok";
                                    } else {
                                        strings[j] = "no";
                                    }
                                }
                            }
                            if (strings.length >= 6) {
                                infoList.add(new SunnyRunInfo(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5]));
                            }
                        }
                        Collections.reverse(infoList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listTopText.setText(topText);
                                adapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                            }
                        });

                    }
                });
    }
}
