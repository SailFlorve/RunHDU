package com.cxsj.runhdu.bean.gson;

import com.cxsj.runhdu.bean.sport.RunningInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 获取跑步信息的json实体类
 */

public class Running {
    @SerializedName("userName")
    public String username;
    public int times;
    public List<RunningInfo> dataList;
}

