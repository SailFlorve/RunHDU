package com.cxsj.runhdu.adapters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.model.gson.FriendInfo;
import com.cxsj.runhdu.model.gson.MyFriend;
import com.cxsj.runhdu.utils.Utility;

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sail on 2017/5/25 0025.
 * 好友申请列表的Adapter
 */

public class FriendApplyRecyclerViewAdapter extends BaseQuickAdapter<MyFriend.ApplicantInfo, BaseViewHolder> {
    public FriendApplyRecyclerViewAdapter(int resId, List<MyFriend.ApplicantInfo> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyFriend.ApplicantInfo item) {
        CircleImageView profileImg = helper.getView(R.id.apply_profile);
        Utility.loadFriendProfileImg(mContext.getApplicationContext(), item.getApplicant(), profileImg);
        helper.setText(R.id.apply_username, item.getApplicant());
        helper.setText(R.id.apply_time, item.getDate());
        helper.addOnClickListener(R.id.refuse_apply_button);
        helper.addOnClickListener(R.id.agree_apply_button);
    }
}
