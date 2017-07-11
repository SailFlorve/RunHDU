package com.cxsj.runhdu;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cxsj.runhdu.adapters.FriendApplyRecyclerViewAdapter;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.model.gson.MyFriend;
import com.cxsj.runhdu.model.gson.Status;
import com.cxsj.runhdu.utils.HttpUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 好友申请Activity
 */
public class FriendApplyBoxActivity extends BaseActivity {

    private RecyclerView applyListView;
    private FriendApplyRecyclerViewAdapter adapter;
    private List<MyFriend.ApplicantInfo> applicantInfoList = new ArrayList<>();
    private LinearLayout noApplyTipLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_apply_box);
        setToolbar(R.id.toolbar_apply_box, true);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        toActivity(this, FriendActivity.class);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        applyListView = (RecyclerView) findViewById(R.id.friend_apply_list);
        noApplyTipLayout = (LinearLayout) findViewById(R.id.no_friend_apply_tip_layout);

        adapter = new FriendApplyRecyclerViewAdapter(
                R.layout.friend_apply_list_item, applicantInfoList);
        applyListView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        applyListView.setLayoutManager(manager);

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.refuse_apply_button) {
                reply(position, false);
            } else if (view.getId() == R.id.agree_apply_button) {
                reply(position, true);
            }
        });
    }

    //初始化申请列表
    private void initData() {
        String json = (String) prefs.get(username + "_friend_json", "");
        if (TextUtils.isEmpty(json)) {
            return;
        }
        MyFriend myFriend = new Gson().fromJson(json, MyFriend.class);
        if (myFriend.getApplyList().isEmpty()) {
            noApplyTipLayout.setVisibility(View.VISIBLE);
        } else {
            noApplyTipLayout.setVisibility(View.GONE);
            applicantInfoList.clear();
            applicantInfoList.addAll(myFriend.getApplyList());
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 回复申请
     * @param position 回复第几个申请
     * @param isAgree 是否同意
     */
    private void reply(int position, Boolean isAgree) {
        String agreeName = applicantInfoList.get(position).getApplicant();
        showProgressDialog("正在处理...");
        HttpUtil.load(URLs.REPLY_FRIEND)
                .addParam("UserA", agreeName)
                .addParam("UserB", username)
                .addParam("IsAgree", isAgree.toString())
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            closeProgressDialog();
                            Toast.makeText(FriendApplyBoxActivity.this,
                                    "网络连接失败。", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        Log.d(TAG, "onResponse: " + json);
                        runOnUiThread(() -> {
                            closeProgressDialog();
                            Status status = new Gson().fromJson(json, Status.class);
                            if (status.getResult()) {
                                Toast.makeText(FriendApplyBoxActivity.this,
                                        "操作成功", Toast.LENGTH_SHORT).show();
                                applicantInfoList.remove(position);
                                adapter.notifyDataSetChanged();
                                if (applicantInfoList.isEmpty()) {
                                    noApplyTipLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(FriendApplyBoxActivity.this,
                                        status.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}
