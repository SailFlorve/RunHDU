package com.sailflorve.runhdu.sport;

public class RunningInfo {

    private String date;

    private String startTime;

    private String endTime;

    private String steps;

    private String distance;

    private String energy;

    public RunningInfo(String date, String sTime, String eTime, String steps, String dis, String energy) {
        this.date = date;
        this.startTime = sTime;
        this.endTime = eTime;
        this.steps = steps;
        this.distance = dis;
        this.energy = energy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }
}
