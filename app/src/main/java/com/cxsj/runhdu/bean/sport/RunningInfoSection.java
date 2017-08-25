package com.cxsj.runhdu.bean.sport;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * 带Header的跑步列表实体类
 */
public class RunningInfoSection extends SectionEntity<RunningInfo> {

    public RunningInfo runningInfo;
    private String header;
    private String times;

    public RunningInfoSection(boolean isHeader, String header) {
        super(isHeader, header);
        this.header = header;
    }

    public RunningInfoSection(RunningInfo info) {
        super(info);
        runningInfo = info;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }
}
