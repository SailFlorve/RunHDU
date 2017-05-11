package com.cxsj.runhdu.utils;

import com.cxsj.runhdu.sport.RunningInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Sail on 2017/5/10 0010.
 * 操作数据库的封装
 */

public class QueryUtil {
    public static List<RunningInfo> find(String... conditions) {
        return DataSupport.where(conditions).find(RunningInfo.class);
    }

    public static List<RunningInfo> findOrder(String... conditions) {
        return DataSupport.where(conditions).order("runId").find(RunningInfo.class);
    }

    public static List<RunningInfo> findAllOrder() {
        return DataSupport.order("runId").find(RunningInfo.class);
    }
}
