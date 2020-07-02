package com.cxsj.runhdu.adapters;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.bean.sport.RunningInfo;
import com.cxsj.runhdu.bean.sport.RunningInfoSection;
import com.cxsj.runhdu.constant.Strings;
import com.cxsj.runhdu.utils.Utility;

import java.util.List;

/**
 * Created by Sail on 2017/5/3 0003.
 * RecyclerView的带Head实体类，用于展示跑步列表。
 */

public class RecyclerViewSectionAdapter extends BaseSectionQuickAdapter<RunningInfoSection, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId      The layout resource id of each item.
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public RecyclerViewSectionAdapter(int layoutResId, int sectionHeadResId, List<RunningInfoSection> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, RunningInfoSection item) {
        helper.setText(R.id.run_header_month_text, item.getHeader());
        helper.setText(R.id.run_header_times_text, item.getTimes());
    }

    @Override
    protected void convert(BaseViewHolder helper, RunningInfoSection item) {
        RunningInfo runningInfo = item.runningInfo;
        if (runningInfo == null) return;

        int distance = runningInfo.getDistance();
        String distanceKM = Utility.formatDecimal((float) distance / 1000, 2);
        String runModeStr = runningInfo.getRunMode();
        if (runModeStr.equals(Strings.RUN_INDOORS)) {
            helper.setImageResource(R.id.run_mode_img, R.drawable.ic_run_indoor);
        } else if (runModeStr.equals(Strings.RUN_OUTDOORS)) {
            helper.setImageResource(R.id.run_mode_img, R.drawable.ic_run_blue);
        }
        helper.setText(R.id.run_mode_text, runModeStr);
        helper.setText(R.id.run_distance_text, distanceKM);
        helper.setText(R.id.run_date_text, runningInfo.getDate());
        helper.setText(R.id.run_start_time_text, runningInfo.getStartTime());
        helper.setText(R.id.run_duration_text, runningInfo.getDuration());
        helper.setText(R.id.run_steps_text, String.valueOf(runningInfo.getSteps()));
    }
}
