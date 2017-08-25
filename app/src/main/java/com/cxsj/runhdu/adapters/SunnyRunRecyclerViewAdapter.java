package com.cxsj.runhdu.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.bean.sport.SunnyRunInfo;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.NumberView;

import java.util.List;

/**
 * Created by Sail on 2017/4/3 0003.
 * 阳光长跑Adapter
 */

public class SunnyRunRecyclerViewAdapter extends BaseQuickAdapter<SunnyRunInfo, BaseViewHolder> {
    public SunnyRunRecyclerViewAdapter(int resId, List<SunnyRunInfo> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SunnyRunInfo item) {
        helper.setText(R.id.sun_run_number, "No." + item.getNumber());
        helper.setText(R.id.sun_run_date, item.getDate());

        NumberView time = helper.getView(R.id.sun_run_time);
        NumberView dis = helper.getView(R.id.sun_run_dis);
        NumberView speed = helper.getView(R.id.sun_run_speed);

        time.setText(item.getDomain());
        dis.setText(item.getMileage());
        speed.setText(Utility.formatDecimal(item.getSpeed(), 2));

        ImageView valid = helper.getView(R.id.sun_run_valid);
        if (item.isValid()) {
            valid.setImageResource(R.drawable.ic_valid);
        } else {
            valid.setImageResource(R.drawable.ic_invalid);
        }
    }
}
