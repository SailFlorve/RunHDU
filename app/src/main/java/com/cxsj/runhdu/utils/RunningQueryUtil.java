package com.cxsj.runhdu.utils;

import com.cxsj.runhdu.bean.sport.RunningInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Sail on 2017/5/10 0010.
 * 查询跑步数据操作数据库的封装工具类
 */

public class RunningQueryUtil {
    /**
     * 普通查找
     * @param conditions 查找条件
     * @return
     */
    public static List<RunningInfo> find(String... conditions) {
        return DataSupport.where(conditions).find(RunningInfo.class);
    }

    /**
     * 按跑步时间排序查找
     * @param conditions 查找条件
     * @return
     */
    public static List<RunningInfo> findOrder(String... conditions) {
        return DataSupport.where(conditions).order("runId").find(RunningInfo.class);
    }

    /**
     * 按跑步时间跑需，查找所有
     * @return
     */
    public static List<RunningInfo> findAllOrder() {
        return DataSupport.order("runId").find(RunningInfo.class);
    }
}
