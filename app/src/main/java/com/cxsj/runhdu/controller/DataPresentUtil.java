package com.cxsj.runhdu.controller;

import android.graphics.Color;

import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.model.sport.RunningInfo;
import com.cxsj.runhdu.model.sport.RunningInfoSection;
import com.cxsj.runhdu.utils.QueryUtil;
import com.cxsj.runhdu.utils.Utility;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;

/**
 * Created by Sail on 2017/5/23 0023.
 * 数据展示相关的类
 * 包括：显示进度、柱状图、跑步详情列表、跑步统计列表
 */

public class DataPresentUtil {
    public interface ProgressViewDataCallback {
        void onDataFound(int steps, int times, int dis, int energy);
    }

    public interface ChartViewDataCallback {
        void onDataFound(ColumnChartData data);
    }

    public interface AllDataCallback {
        void onDataFound(List<RunningInfoSection> infoList);
    }

    public interface StatisticsCallback {
        void onDataFound(int allSteps, int allEnergy, double allDis,
                         double averSteps, double averEnergy, double averDis,
                         int allTimes, double averTimes, double allTime);
    }

    public static void setProgressViewData(ProgressViewDataCallback callback) {
        List<RunningInfo> runningInfoList = QueryUtil.findOrder(
                "year = ? and month = ? and date = ?",
                Utility.getTime(Calendar.YEAR),
                Utility.getTime(Types.TYPE_MONTH),
                Utility.getTime(Calendar.DATE));
        int steps = 0;
        int times = 0;
        int dis = 0;
        int energy = 0;
        for (RunningInfo info : runningInfoList) {
            steps += info.getSteps();
            times++;
            dis += info.getDistance();
            energy += info.getEnergy();
        }
        callback.onDataFound(steps, times, dis, energy);
    }

    public static void setColumnChartViewData(int chartColumnNum, ChartViewDataCallback callback) {
        List<String> chartLabels = new ArrayList<>();
        List<Float> chartValues = new ArrayList<>();
        //初始化ColumnChart
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.CHINA);
        for (int i = 0; i < chartColumnNum; i++) {
            chartLabels.add(sdf.format(c.getTime()));
            List<RunningInfo> runningInfoList = QueryUtil.findOrder(
                    "year = ? and month = ? and date = ?",
                    String.valueOf(c.get(Calendar.YEAR)),
                    String.valueOf(c.get(Calendar.MONTH) + 1),
                    String.valueOf(c.get(Calendar.DATE)));
            int steps = 0;
            for (RunningInfo info : runningInfoList) {
                steps += info.getSteps();
            }
            chartValues.add((float) steps);
            c.add(Calendar.DATE, -1);
        }
        Collections.reverse(chartLabels);
        Collections.reverse(chartValues);

        if (chartLabels.size() != chartValues.size()
                || chartLabels.isEmpty()
                || chartValues.isEmpty()) {
            callback.onDataFound(null);
            return;
        }

        ColumnChartData data;
        // 列，每列1个柱状图。
        int numSubcolumns = 1;
        int numColumns = chartLabels.size();
        //圆柱对象集合
        List<Column> columns = new ArrayList<>();
        //子列数据集合
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; ++j) {
                //为每一柱图添加颜色和数值
                values.add(new SubcolumnValue(chartValues.get(i), Color.parseColor("#03A9F4")));
            }
            //创建Column对象
            Column column = new Column(values);
            //是否有数据标注
            column.setHasLabels(true);
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(chartLabels.get(i)));
        }
        //创建一个带有之前圆柱对象column集合的ColumnChartData
        data = new ColumnChartData(columns);

        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        axisX.setTextColor(Color.parseColor("#ffffff"));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        data.setAxisXBottom(axisX);
        callback.onDataFound(data);
    }

    public static void setAllRunData(AllDataCallback callback) {
        List<RunningInfoSection> runningInfoSectionList = new ArrayList<>();
        List<RunningInfo> list = QueryUtil.findAllOrder();
        if (list.isEmpty()) {
            callback.onDataFound(null);
            return;
        }
        String oldYear = list.get(0).getYear();
        String oldMonth = list.get(0).getMonth();
        int times = 0;
        List<RunningInfo> tempList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RunningInfo info = list.get(i);
            String year = info.getYear();
            String month = info.getMonth();

            if (year.equals(oldYear) && month.equals(oldMonth)) {
                times++;
                tempList.add(info);
            } else {
                RunningInfoSection section = new RunningInfoSection(true, oldYear + "年" + oldMonth + "月");
                section.setTimes(String.valueOf(times));

                if (tempList != null) {
                    for (RunningInfo inf : tempList) {
                        RunningInfoSection s = new RunningInfoSection(inf);
                        runningInfoSectionList.add(s);
                    }
                }

                runningInfoSectionList.add(section);
                oldYear = year;
                oldMonth = month;
                times = 1;
                tempList = new ArrayList<>();
                tempList.add(info);
            }
            if (i == list.size() - 1) {
                RunningInfoSection section = new RunningInfoSection(true, oldYear + "年" + oldMonth + "月");
                section.setTimes(String.valueOf(times));
                if (tempList != null) {
                    for (RunningInfo inf : tempList) {
                        RunningInfoSection s = new RunningInfoSection(inf);
                        runningInfoSectionList.add(s);
                    }
                }
                runningInfoSectionList.add(section);
            }
        }
        Collections.reverse(runningInfoSectionList);
        callback.onDataFound(runningInfoSectionList);
    }

    public static void setStatisticsData(StatisticsCallback callback) {
        int allStepsNum = DataSupport.sum(RunningInfo.class, "steps", int.class);
        int allEnergyNum = DataSupport.sum(RunningInfo.class, "energy", int.class);
        double allDisNum = DataSupport.sum(RunningInfo.class, "distance", int.class);
        double averStepsNum = DataSupport.average(RunningInfo.class, "steps");
        double averEnergyNum = DataSupport.average(RunningInfo.class, "energy");
        double averDisNum = DataSupport.average(RunningInfo.class, "distance");
        int allTimesNum = DataSupport.count(RunningInfo.class);
        double averTimesNum = 0;
        double allTimeNum = 0;

        List<RunningInfo> list = QueryUtil.findAllOrder();
        String oldDate = null;
        int runDays = 0;
        for (RunningInfo info : list) {
            String date = info.getDate();
            if (!date.equals(oldDate)) runDays++;
            oldDate = date;
            allTimeNum += Utility.getMinutes(info.getDuration());

        }

        if (runDays != 0) {
            averTimesNum = (double) allTimesNum / runDays;
        }

        callback.onDataFound(allStepsNum, allEnergyNum, allDisNum,
                averStepsNum, averEnergyNum, averDisNum,
                allTimesNum, averTimesNum, allTimeNum);
    }
}
