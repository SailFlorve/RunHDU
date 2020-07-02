package com.cxsj.runhdu.appfunctions.friend;

import com.cxsj.runhdu.base.BaseModel;
import com.cxsj.runhdu.bean.gson.MyFriend;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SailFlorve on 2017/8/25 0025.
 * 好友相关数据请求
 */

public class FriendModel extends BaseModel {
    public interface GetFriendCallback {
        void onSuccess(String json, MyFriend myFriend);

        void onFailure(String msg);
    }

    public static void getFriendList(String username, GetFriendCallback callback) {
        HttpUtil.load(URLs.GET_FRIEND)
                .addParam("UserName", username)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onFailure(CONNECT_FAILED));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String jsonStr = response.body().string();
                        mHandler.post(() -> {
                            MyFriend myFriend;
                            try {
                                myFriend = new Gson().fromJson(jsonStr, MyFriend.class);
                            } catch (JsonSyntaxException e) {
                                callback.onFailure(RETURN_DATA_ERROR);
                                return;
                            }
                            if (myFriend == null) {
                                callback.onFailure(RETURN_DATA_ERROR);
                                return;
                            }
                            callback.onSuccess(jsonStr, myFriend);
                        });
                    }
                });
    }

    public static void addFriend(String username, String friendName, BaseCallback callback) {
        HttpUtil.load(URLs.APPLY_FRIEND)
                .addParam("UserA", username)
                .addParam("UserB", friendName)
                .post(getJsonStatusHttpCallback(callback));
    }

    public static void replyFriendApply(String username, String friendName, Boolean isAgree, BaseCallback callback) {
        HttpUtil.load(URLs.REPLY_FRIEND)
                .addParam("UserA", friendName)
                .addParam("UserB", username)
                .addParam("IsAgree", isAgree.toString())
                .post(getJsonStatusHttpCallback(callback));
    }
}
