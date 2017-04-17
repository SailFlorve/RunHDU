package com.cxsj.runhdu.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.sport.SunnyRunInfo;
import com.cxsj.runhdu.view.NumberView;

import java.util.List;

/**
 * Created by Sail on 2017/4/3 0003.
 * Adapter
 */

public class SunnyRunRecyclerViewAdapter extends BaseQuickAdapter<SunnyRunInfo, BaseViewHolder> {
    public SunnyRunRecyclerViewAdapter(int resId, List<SunnyRunInfo> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SunnyRunInfo item) {
        helper.setText(R.id.sun_run_time_date, "第" + item.getNumber() + "次 | " + item.getDate());

        NumberView time = (NumberView) helper.getView(R.id.sun_run_time);
        NumberView dis = (NumberView) helper.getView(R.id.sun_run_dis);
        NumberView speed = (NumberView) helper.getView(R.id.sun_run_speed);
        time.setText(item.getTime());
        dis.setText(item.getDistance());
        speed.setText(item.getSpeed());

        ImageView valid = (ImageView) helper.getView(R.id.sun_run_valid);
        if (item.getValid().equals("ok")) {
            valid.setImageResource(R.drawable.ic_valid);
        } else {
            valid.setImageResource(R.drawable.ic_invalid);
        }
    }
}
