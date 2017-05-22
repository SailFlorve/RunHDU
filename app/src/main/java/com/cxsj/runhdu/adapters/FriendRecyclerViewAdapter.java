package com.cxsj.runhdu.adapters;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.model.gson.FriendInfo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sail on 2017/5/22 0022.
 * 好友列表RecyclerView的Adapter
 */

public class FriendRecyclerViewAdapter extends BaseQuickAdapter<FriendInfo, BaseViewHolder> {

    public FriendRecyclerViewAdapter(int resId, List<FriendInfo> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfo item) {
        CircleImageView profileImg = helper.getView(R.id.friend_profile_list_item);
        Glide.with(mContext).load(URLs.PROFILE_URL + item.getUsername() + ".JPEG")
                .error(R.drawable.photo).into(profileImg);
        helper.setText(R.id.friend_name_list_item, item.getUsername());
        helper.setText(R.id.friend_des_list_item,
                String.format("今日跑步%d次，共跑步%d次。", item.getNumToday(), item.getNumAll()));
    }
}
