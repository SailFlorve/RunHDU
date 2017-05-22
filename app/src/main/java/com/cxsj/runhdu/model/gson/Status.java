package com.cxsj.runhdu.model.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sail on 2017/5/19 0019.
 * 服务器返回的json解析实体类
 */

public class Status {
    @SerializedName("Result")
    private boolean result;

    @SerializedName("Message")
    private String message;

    @SerializedName("Which")
    private int which;

    public boolean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public int getWhich() {
        return which;
    }

}
