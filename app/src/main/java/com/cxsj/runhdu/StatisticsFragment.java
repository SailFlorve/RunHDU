package com.cxsj.runhdu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxsj.runhdu.controller.DataPresentUtil;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.NumberView;

/**
 * 数据统计的Fragment
 */
public class StatisticsFragment extends Fragment {
    private NumberView allSteps;
    private NumberView allEnergy;
    private NumberView allDis;
    private NumberView averSteps;
    private NumberView averEnergy;
    private NumberView averDis;
    private NumberView allTimes;
    private NumberView averTimes;
    private NumberView allTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        allSteps = (NumberView) view.findViewById(R.id.all_steps);
        allEnergy = (NumberView) view.findViewById(R.id.all_energies);
        allDis = (NumberView) view.findViewById(R.id.all_distances);
        averSteps = (NumberView) view.findViewById(R.id.day_ave_step);
        averEnergy = (NumberView) view.findViewById(R.id.day_ave_energy);
        averDis = (NumberView) view.findViewById(R.id.day_ave_dis);
        allTimes = (NumberView) view.findViewById(R.id.all_run_times);
        averTimes = (NumberView) view.findViewById(R.id.day_ave_times);
        allTime = (NumberView) view.findViewById(R.id.all_run_time);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    //设置统计数据
    public void updateData() {
        DataPresentUtil.setStatisticsData((allStepsNum, allEnergyNum, allDisNum,
                                           averStepsNum, averEnergyNum, averDisNum,
                                           allTimesNum, averTimesNum, allTimeNum) -> {
            allSteps.setText(handleBigInt(allStepsNum));
            allEnergy.setText(handleBigInt(allEnergyNum));
            allDis.setText(Utility.formatDecimal(allDisNum / 1000, 1));
            averSteps.setText(handleBigDouble(averStepsNum));
            averEnergy.setText(handleBigDouble(averEnergyNum));
            averDis.setText(Utility.formatDecimal(averDisNum / 1000, 1));
            allTimes.setText(String.valueOf(allTimesNum));
            averTimes.setText(Utility.formatDecimal(averTimesNum, 1));
            allTime.setText(Utility.formatDecimal(allTimeNum / 60.0, 1));
        });
    }

    //处理大数据，如果超过10000，则显示万
    private String handleBigInt(int num) {
        if (num > 10000) {
            return Utility.formatDecimal(num / 10000.0, 2) + "万";
        } else {
            return String.valueOf(num);
        }
    }

    private String handleBigDouble(double num) {
        if (num > 10000) {
            return Utility.formatDecimal(num / 10000.0, 2) + "万";
        } else {
            return Utility.formatDecimal(num, 1);
        }
    }
}
