package com.cxsj.runhdu.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.model.gson.FriendInfo;
import com.cxsj.runhdu.utils.Utility;

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
        CircleImageView onlineImg = helper.getView(R.id.online_flag_img);
        Glide.with(mContext).load(URLs.PROFILE_URL + item.getUsername() + ".JPEG")
                .signature(new StringSignature(Utility.getTime(Types.TYPE_STRING_FORM)))
                .crossFade(0).error(R.drawable.photo).into(profileImg);
        if (item.isOnline()) {
            onlineImg.setVisibility(View.VISIBLE);
            onlineImg.setImageResource(R.drawable.green);
        } else {
            onlineImg.setVisibility(View.INVISIBLE);
        }

        helper.setText(R.id.friend_name_list_item, item.getUsername());
        helper.setText(R.id.friend_des_list_item,
                String.format("今日跑步%d次 | 共跑步%d次", item.getNumToday(), item.getNumAll()));
    }
}
