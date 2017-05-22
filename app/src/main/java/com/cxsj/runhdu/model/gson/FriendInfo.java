package com.cxsj.runhdu.model.gson;

/**
 * Created by Sail on 2017/5/22 0022.
 * 好友实体类
 */

public class FriendInfo {
    private String username;
    private int numToday;
    private int numAll;

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
}
