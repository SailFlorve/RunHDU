package com.cxsj.runhdu.bean.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sail on 2017/5/22 0022.
 * 好友实体类
 */

public class FriendInfo {
    @SerializedName("UserName")
    private String username;

    @SerializedName("TodayTimes")
    private int numToday;

    @SerializedName("SumTimes")
    private int numAll;

    @SerializedName("IsOnLine")
    private boolean isOnline;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumToday() {
        return numToday;
    }

    public void setNumToday(int numToday) {
        this.numToday = numToday;
    }

    public int getNumAll() {
        return numAll;
    }

    public void setNumAll(int numAll) {
        this.numAll = numAll;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
