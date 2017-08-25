package com.cxsj.runhdu;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cxsj.runhdu.Model.BaseModel;
import com.cxsj.runhdu.Model.FriendModel;
import com.cxsj.runhdu.adapters.FriendApplyRecyclerViewAdapter;
import com.cxsj.runhdu.bean.gson.MyFriend;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param position 回复第几个申请
     * @param isAgree  是否同意
     */
    private void reply(int position, Boolean isAgree) {
        String agreeName = applicantInfoList.get(position).getApplicant();
        showProgressDialog("正在处理...");
        FriendModel.replyFriendApply(username, agreeName, isAgree, new BaseModel.BaseCallback() {
            @Override
            public void onFailure(String msg) {
                closeProgressDialog();
                Toast.makeText(FriendApplyBoxActivity.this,
                        msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                closeProgressDialog();
                Toast.makeText(FriendApplyBoxActivity.this,
                        "操作成功", Toast.LENGTH_SHORT).show();
                applicantInfoList.remove(position);
                adapter.notifyDataSetChanged();
                if (applicantInfoList.isEmpty()) {
                    noApplyTipLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
