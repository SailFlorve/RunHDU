package com.cxsj.runhdu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.sport.RunningInfo;
import com.cxsj.runhdu.adapters.TodayRecyclerViewAdapter;
import com.cxsj.runhdu.utils.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodayFragment extends Fragment {

    private static final String TAG = "TodayFragment";
    private RecyclerView todayRecyclerView;
    private LinearLayout neverRunLayout;
    private static TodayRecyclerViewAdapter viewAdapter;
    private View view;

    private List<RunningInfo> infoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.today_fragment, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        updateData();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    private void initView() {
        todayRecyclerView = (RecyclerView) view.findViewById(R.id.today_recycler_view);
        neverRunLayout = (LinearLayout) view.findViewById(R.id.never_run_layout);

        viewAdapter = new TodayRecyclerViewAdapter(R.layout.run_list_item, infoList);
        viewAdapter.openLoadAnimation();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        todayRecyclerView.setLayoutManager(layoutManager);
        todayRecyclerView.setAdapter(viewAdapter);
    }

    public void setList(List<RunningInfo> list) {
        if (infoList == null) {
            infoList = new ArrayList<>();
        } else {
            infoList.clear();
        }

        Collections.reverse(list);
        infoList.addAll(list);
        viewAdapter.notifyDataSetChanged();
    }

    public void updateData() {
        List<RunningInfo> list = DataSupport
                .where("date = ?", Utility.getTime(Types.TYPE_MONTH_DATE))
                .find(RunningInfo.class);
        if (!list.isEmpty()) {
            neverRunLayout.setVisibility(View.GONE);
        } else {
            neverRunLayout.setVisibility(View.VISIBLE);
        }
        setList(list);
    }
}
