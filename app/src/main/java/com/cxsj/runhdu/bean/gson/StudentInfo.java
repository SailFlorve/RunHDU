package com.cxsj.runhdu.bean.gson;

/**
 * Created by Sail on 2017/5/8 0008.
 * 阳光长跑返回Json的学生总体信息实体类
 */

public class StudentInfo {
    private String validTimes;
    private String state;
    private String name;
    private String speed;
    private String mileages;

    public String getValidTimes() {
        return validTimes;
    }

    public void setValidTimes(String validTimes) {
        this.validTimes = validTimes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getMileages() {
        return mileages;
    }

    public void setMileages(String mileages) {
        this.mileages = mileages;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
