package com.cxsj.runhdu;

import android.content.Intent;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cxsj.runhdu.adapters.RecyclerViewSectionAdapter;
import com.cxsj.runhdu.sport.RunningInfo;
import com.cxsj.runhdu.sport.RunningInfoSection;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodayFragment extends Fragment {

    private static final String TAG = "TodayFragment";
    private RecyclerView todayRecyclerView;
    private LinearLayout neverRunLayout;
    private RecyclerViewSectionAdapter viewAdapter;
    private View view;

    private List<RunningInfoSection> infoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_today, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setListData();
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

        viewAdapter = new RecyclerViewSectionAdapter(R.layout.run_list_item, R.layout.run_list_header_item, infoList);
        viewAdapter.openLoadAnimation();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        todayRecyclerView.setLayoutManager(layoutManager);
        todayRecyclerView.setAdapter(viewAdapter);

        viewAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RunningInfoSection section = (RunningInfoSection) adapter.getItem(position);
                if (section.runningInfo == null) return;
                Intent intent = new Intent(getContext(), RunDetailsActivity.class);
                intent.putExtra("id", section.runningInfo.getId());
                startActivity(intent);
            }
        });
    }

    private void addInfoList(List<RunningInfo> list) {
        if (infoList == null) {
            return;
        }

        for (RunningInfo info : list) {
            RunningInfoSection section = new RunningInfoSection(info);
            infoList.add(section);
        }
    }

    /*
    数据库： 2016 12-2
            2016 12-5
            2016 12-6
            2016 12-7
            2016 12-8
            2016 12-8
            2016 12-8
            --addHeader 2016年12月 7次
            2017 2-3
            2017 2-3
            --addHeader 2017年2月 2次
            2017 5-1
            2017 5-1
            2017 5-1
            --addHeader 2017年5月 3次
    * */

    public void setListData() {
        infoList.clear();
        viewAdapter.notifyDataSetChanged();

        List<RunningInfo> list = DataSupport.findAll(RunningInfo.class);
        if (!list.isEmpty()) {
            neverRunLayout.setVisibility(View.GONE);
        } else {
            neverRunLayout.setVisibility(View.VISIBLE);
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
                addInfoList(tempList);
                infoList.add(section);
                oldYear = year;
                oldMonth = month;
                times = 1;
                tempList = null;
                tempList = new ArrayList<>();
                tempList.add(info);
            }
            if (i == list.size() - 1) {
                RunningInfoSection section = new RunningInfoSection(true, oldYear + "年" + oldMonth + "月");
                section.setTimes(String.valueOf(times));
                addInfoList(tempList);
                infoList.add(section);
            }
        }
        Collections.reverse(infoList);
        viewAdapter.notifyDataSetChanged();

//        for (RunningInfo info : list) {
//            String year = info.getYear();
//            String month = info.getMonth();
//            if (year.equals(oldYear)
//                    && month.equals(oldMonth)) {
//                times++;
//                tempList.add(info);
//            } else {
//                RunningInfoSection section = new RunningInfoSection(true, year + "年" + month + "月");
//                section.setTimes(String.valueOf(times));
//                addInfoList(tempList);
//                tempList = null;
//                tempList = new ArrayList<>();
//                tempList.add(info);
//                infoList.add(section);
//                times = 1;
//                oldYear = year;
//                oldMonth = month;
//            }
//        }
    }
}
