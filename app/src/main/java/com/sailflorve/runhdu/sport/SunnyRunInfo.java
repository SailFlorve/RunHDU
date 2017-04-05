package com.sailflorve.runhdu.sport;

public class SunnyRunInfo {

    private String valid;

    private String date;

    private String time;

    private String distance;

    private String speed;

    private String number;

    public SunnyRunInfo() {

    }

    public SunnyRunInfo(String number, String date, String time, String distance, String speed, String valid) {
        this.date = date;
        this.distance = distance;
        this.number = number;
        this.speed = speed;
        this.time = time;
        this.valid = valid;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
