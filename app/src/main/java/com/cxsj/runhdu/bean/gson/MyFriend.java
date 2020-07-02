package com.cxsj.runhdu.bean.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sail on 2017/5/24 0024.
 * 获取好友列表和申请列表的json实体类
 */

public class MyFriend {
    @SerializedName("MyFriends")
    private List<FriendInfo> friendList;

    @SerializedName("ApplyLists")
    private List<ApplicantInfo> applyList;

    public List<FriendInfo> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<FriendInfo> friendList) {
        this.friendList = friendList;
    }

    public List<ApplicantInfo> getApplyList() {
        return applyList;
    }

    public void setApplyList(List<ApplicantInfo> applyList) {
        this.applyList = applyList;
    }

    public class ApplicantInfo {
        @SerializedName("Applicant")
        private String applicant;

        @SerializedName("ApplyDate")
        private String date;

        public String getApplicant() {
            return applicant;
        }

        public void setApplicant(String applicant) {
            this.applicant = applicant;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
