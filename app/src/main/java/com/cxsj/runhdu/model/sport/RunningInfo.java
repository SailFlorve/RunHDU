package com.cxsj.runhdu.model.sport;

import com.cxsj.runhdu.utils.ZipUtil;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class RunningInfo extends DataSupport implements Serializable {


    private String runId;

    private String runMode;

    private String year;

    private String month;

    private String date;

    private String startTime;

    private String duration;

    private int steps;

    private int distance;

    private int energy;

    private float speed;

    private String trailList;

    public RunningInfo() {
    }

    public RunningInfo(String runId,
                       String runMode,
                       String year,
                       String month,
                       String date,
                       String startTime,
                       String duration,
                       int steps,
                       int distance,
                       int energy,
                       float speed,
                       String trailList) {
        this.runId = runId;
        this.runMode = runMode;
        this.year = year;
        this.month = month;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.steps = steps;
        this.distance = distance;
        this.energy = energy;
        this.speed = speed;
        this.trailList = ZipUtil.compress(trailList);
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getTrailList() {
        return ZipUtil.decompress(trailList);
    }

    public String getCompressedTrailList() {
        return trailList;
    }

    public void setTrailList(String trailList) {
        this.trailList = ZipUtil.compress(trailList);
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }
}
