package com.cxsj.runhdu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxsj.runhdu.model.sport.RunningInfo;
import com.cxsj.runhdu.utils.QueryUtil;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.NumberView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private NumberView allSteps;
    private NumberView allEnergy;
    private NumberView allDis;
    private NumberView averSteps;
    private NumberView averEnergy;
    private NumberView averDis;
    private NumberView allTimes;
    private NumberView averTimes;
    private NumberView allTime;
    private List<RunningInfo> list = new ArrayList<>();

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
        updateData();
        super.onActivityCreated(savedInstanceState);
    }

    public void updateData() {
        int allStepsNum = DataSupport.sum(RunningInfo.class, "steps", int.class);
        int allEnergyNum = DataSupport.sum(RunningInfo.class, "energy", int.class);
        double allDisNum = DataSupport.sum(RunningInfo.class, "distance", int.class);
        double averStepsNum = DataSupport.average(RunningInfo.class, "steps");
        double averEnergyNum = DataSupport.average(RunningInfo.class, "energy");
        double averDisNum = DataSupport.average(RunningInfo.class, "distance");
        int allTimesNum = DataSupport.count(RunningInfo.class);
        double averTimesNum = 0;
        double allTimeNum = 0;

        list = QueryUtil.findAllOrder();
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

        allSteps.setText(handleBigInt(allStepsNum));
        allEnergy.setText(handleBigInt(allEnergyNum));
        allDis.setText(Utility.formatDecimal(allDisNum / 1000, 1));
        averSteps.setText(handleBigDouble(averStepsNum));
        averEnergy.setText(handleBigDouble(averEnergyNum));
        averDis.setText(Utility.formatDecimal(averDisNum / 1000, 1));
        allTimes.setText(String.valueOf(allTimesNum));
        averTimes.setText(Utility.formatDecimal(averTimesNum, 1));
        allTime.setText(Utility.formatDecimal(allTimeNum / 60.0, 1));
    }

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
