package com.cxsj.runhdu.appfunctions.friend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.adapters.RecyclerViewSectionAdapter;
import com.cxsj.runhdu.appfunctions.main.DataQueryModel;
import com.cxsj.runhdu.appfunctions.main.RunDetailsActivity;
import com.cxsj.runhdu.appfunctions.running.RunningModel;
import com.cxsj.runhdu.base.BaseActivity;
import com.cxsj.runhdu.bean.gson.Running;
import com.cxsj.runhdu.bean.gson.Status;
import com.cxsj.runhdu.bean.sport.RunningInfoSection;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.Utility;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 好友跑步详情Activity
 */
public class FriendDetailsActivity extends BaseActivity {

    private String friendUsername;
    private LinearLayout noRunTipLayout;
    private ImageView bgImg;
    private CircleImageView profileImg;
    private RecyclerView runListView;
    private RecyclerViewSectionAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<RunningInfoSection> sectionList;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);
        setToolbar(R.id.toolbar_friend_details, true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            friendUsername = bundle.getString("friend_user_name");
        } else {
            finish();
        }
        initView();
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.delete_friend) {
            new AlertDialog.Builder(this)
                    .setTitle("删除好友确认")
                    .setMessage("确定要删除此好友？此操作不可恢复。")
                    .setPositiveButton("删除", (d, w) -> deleteFriend())
                    .setNegativeButton("取消", null)
                    .create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        noRunTipLayout = (LinearLayout) findViewById(R.id.friend_no_run_tip);
        bgImg = (ImageView) findViewById(R.id.friend_detail_bg);
        profileImg = (CircleImageView) findViewById(R.id.friend_detail_profile);
        runListView = (RecyclerView) findViewById(R.id.friend_run_list_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.friend_details_srl);
        collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.friend_detail_collapsing_layout);

        refreshLayout.setEnabled(false);

        sectionList = new ArrayList<>();
        adapter = new RecyclerViewSectionAdapter(R.layout.run_list_item, R.layout.run_list_header_item, sectionList);
        runListView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        runListView.setLayoutManager(manager);

        collapsingToolbarLayout.setTitle(friendUsername + " 跑步详情");
        Utility.loadFriendProfileImg(getApplicationContext(), friendUsername, profileImg);
        String menuBgUri = (String) prefs.get("menu_bg_uri", null);
        if (!TextUtils.isEmpty(menuBgUri)) {
            Glide.with(this).load(Uri.parse(menuBgUri))
                    .bitmapTransform(new BlurTransformation(this, 25, 5))
                    .into(bgImg);
        } else {
            Glide.with(this).load(R.drawable.menu_bg)
                    .bitmapTransform(new BlurTransformation(this, 25, 5))
                    .into(bgImg);
        }

        adapter.setOnItemClickListener((adapter1, view, position) -> {
            RunningInfoSection section = adapter.getItem(position);
            if (section.runningInfo == null) return;
            Intent intent = new Intent(FriendDetailsActivity.this, RunDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("running_info", section.runningInfo);
            bundle.putBoolean("is_friend", true);
            intent.putExtras(bundle);
            startActivity(intent);
        });

    }

    //获取好友跑步详情
    private void getData() {
        refreshLayout.setRefreshing(true);
        RunningModel.getRunningInfo(friendUsername, new RunningModel.GetRunningInfoCallback() {
            @Override
            public void onFailure(String msg) {
                refreshLayout.setRefreshing(false);
                showSnackBar(msg);
            }

            @Override
            public void onSuccess(Running running) {
                refreshLayout.setRefreshing(false);
                if (running.dataList.isEmpty()) {
                    noRunTipLayout.setVisibility(View.VISIBLE);
                    return;
                }
                List<RunningInfoSection> infoList = DataQueryModel.getSectionList(running.dataList);
                sectionList.clear();
                sectionList.addAll(infoList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void deleteFriend() {
        showProgressDialog("正在删除...");
        HttpUtil.load(URLs.DELETE_FRIEND)
                .addParam("UserA", username)
                .addParam("UserB", friendUsername)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            closeProgressDialog();
                            showSnackBar("网络连接失败。");
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Status status = new Gson().fromJson(response.body().string(), Status.class);
                        runOnUiThread(() -> {
                            closeProgressDialog();
                            if (status == null) {
                                showSnackBar("服务器错误。");
                            } else {
                                if (status.getResult()) {
                                    toActivity(FriendDetailsActivity.this, FriendActivity.class);
                                    finish();
                                } else {
                                    showSnackBar(status.getMessage());
                                }
                            }
                        });
                    }
                });
    }

    private void showSnackBar(String text) {
        Snackbar.make(refreshLayout, text, Snackbar.LENGTH_LONG).show();
    }

}
