package com.cxsj.runhdu.gson;

import com.cxsj.runhdu.sport.RunningInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 获取跑步信息的gson实体类
 */

public class Running {
    @SerializedName("userName")
    public String username;
    public int times;
    public List<RunningInfo> dataList;
}

