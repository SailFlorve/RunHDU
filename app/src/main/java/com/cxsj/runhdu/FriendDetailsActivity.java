package com.cxsj.runhdu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cxsj.runhdu.adapters.RecyclerViewSectionAdapter;
import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.controller.DataPresentUtil;
import com.cxsj.runhdu.controller.DataSyncUtil;
import com.cxsj.runhdu.model.gson.Running;
import com.cxsj.runhdu.model.sport.RunningInfo;
import com.cxsj.runhdu.model.sport.RunningInfoSection;
import com.cxsj.runhdu.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

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
        Glide.with(this).load(URLs.PROFILE_URL + friendUsername + ".JPEG")
                .crossFade(0).error(R.drawable.photo).into(profileImg);
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
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void getData() {
        refreshLayout.setRefreshing(true);
        DataSyncUtil.downloadFromServer(friendUsername, new DataSyncUtil.DownloadRunDataCallback() {
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

                DataPresentUtil.getSectionList(running.dataList, infoList -> {
                    sectionList.clear();
                    sectionList.addAll(infoList);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void showSnackBar(String text) {
        Snackbar.make(refreshLayout, text, Snackbar.LENGTH_LONG).show();
    }

}
