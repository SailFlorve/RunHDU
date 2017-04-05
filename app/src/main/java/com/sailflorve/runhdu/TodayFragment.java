package com.sailflorve.runhdu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sailflorve.runhdu.sport.RunningInfo;
import com.sailflorve.runhdu.adapters.TodayRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment {

    private RecyclerView todayRecyclerView;
    private static TodayRecyclerViewAdapter viewAdapter;
    private View view;

    private static List<RunningInfo> infoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.today_fragment, container, false);
        infoList.clear();
        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initRecyclerView() {
        todayRecyclerView = (RecyclerView) view.findViewById(R.id.today_recycler_view);
        viewAdapter = new TodayRecyclerViewAdapter(R.layout.run_list_item, infoList);
        viewAdapter.openLoadAnimation();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);

        setRecyclerViewData();

        todayRecyclerView.setLayoutManager(layoutManager);
        todayRecyclerView.setAdapter(viewAdapter);
    }

    private void setRecyclerViewData() {
        addItem(new RunningInfo("03-19", "16:23", "16:44", "6742", "3.53", "244"));
        addItem(new RunningInfo("03-18", "17:53", "18:14", "5672", "2.12", "234"));
        addItem(new RunningInfo("03-17", "15:24", "15:31", "6710", "2.55", "303"));
        addItem(new RunningInfo("03-16", "07:22", "07:38", "8849", "3.67", "355"));
    }

    public static void setList(List<RunningInfo> runningInfoList) {
        infoList.clear();
        infoList = runningInfoList;
        viewAdapter.notifyDataSetChanged();
    }

    public static void addItem(RunningInfo runningInfo) {
        infoList.add(runningInfo);
        viewAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int pos) {
        infoList.remove(pos);
        viewAdapter.notifyDataSetChanged();
    }

    public static void removeAll() {
        infoList.clear();
        viewAdapter.notifyDataSetChanged();
    }
}
