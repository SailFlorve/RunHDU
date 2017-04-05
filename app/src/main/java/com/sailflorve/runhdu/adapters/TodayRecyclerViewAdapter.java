package com.sailflorve.runhdu.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sailflorve.runhdu.R;
import com.sailflorve.runhdu.sport.RunningInfo;
import com.sailflorve.runhdu.view.NumberView;

import java.util.List;


public class TodayRecyclerViewAdapter extends BaseQuickAdapter<RunningInfo, BaseViewHolder> {

    public TodayRecyclerViewAdapter(int resId, List<RunningInfo> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RunningInfo item) {
        helper.setText(R.id.running_time_text,
                item.getDate() + " | " + item.getStartTime() + " ~ " + item.getEndTime());
        NumberView step = (NumberView) helper.getView(R.id.step_number_view);
        NumberView energy = (NumberView) helper.getView(R.id.energy_number_view);
        NumberView dis = (NumberView) helper.getView(R.id.dis_number_view);
        step.setText(item.getSteps());
        energy.setText(item.getEnergy());
        dis.setText(item.getDistance());
}
}
