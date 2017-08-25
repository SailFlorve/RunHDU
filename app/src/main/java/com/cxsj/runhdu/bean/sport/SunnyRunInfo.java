package com.cxsj.runhdu.bean.sport;

/**
 * 阳光长跑信息实体类
 */
public class SunnyRunInfo {

    private boolean isValid;

    private String date;

    private String domain;

    private String mileage;

    private double speed;

    private String number;

    public SunnyRunInfo() {

    }

    public SunnyRunInfo(String number, String date, String time, String distance, double speed, boolean valid) {
        this.date = date;
        this.mileage = distance;
        this.number = number;
        this.speed = speed;
        this.domain = time;
        this.isValid = valid;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
