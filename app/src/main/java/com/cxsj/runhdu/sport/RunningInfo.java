package com.cxsj.runhdu.sport;

import org.litepal.crud.DataSupport;

public class RunningInfo extends DataSupport {

    private String date;

    private String startTime;

    private String endTime;

    private int steps;

    private int distance;

    private int energy;

    private float speed;

    public RunningInfo(String date, String sTime, String eTime, int steps, int dis, int energy, float speed) {
        this.date = date;
        this.startTime = sTime;
        this.endTime = eTime;
        this.steps = steps;
        this.distance = dis;
        this.energy = energy;
        this.speed = speed;
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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
