package com.cxsj.runhdu.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.sport.RunningInfo;
import com.cxsj.runhdu.view.NumberView;

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
        step.setText(String.valueOf(item.getSteps()));
        energy.setText(String.valueOf(item.getEnergy()));
        dis.setText(String.valueOf(item.getDistance()));
    }
}
