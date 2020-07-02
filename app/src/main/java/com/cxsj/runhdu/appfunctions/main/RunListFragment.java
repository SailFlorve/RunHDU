package com.cxsj.runhdu.appfunctions.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cxsj.runhdu.R;
import com.cxsj.runhdu.adapters.RecyclerViewSectionAdapter;
import com.cxsj.runhdu.bean.sport.RunningInfo;
import com.cxsj.runhdu.bean.sport.RunningInfoSection;
import com.cxsj.runhdu.utils.RunningQueryUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示跑步详情页面的Fragment
 */
public class RunListFragment extends Fragment {

    private static final String TAG = "RunListFragment";
    private RecyclerView todayRecyclerView;
    private LinearLayout neverRunLayout;
    private RecyclerViewSectionAdapter viewAdapter;
    private View view;

    private List<RunningInfoSection> infoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_today, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        initView();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        setListData();
    }

    private void initView() {
        todayRecyclerView = (RecyclerView) view.findViewById(R.id.today_recycler_view);
        neverRunLayout = (LinearLayout) view.findViewById(R.id.never_run_layout);

        viewAdapter = new RecyclerViewSectionAdapter(R.layout.run_list_item, R.layout.run_list_header_item, infoList);
        viewAdapter.openLoadAnimation();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        todayRecyclerView.setLayoutManager(layoutManager);
        todayRecyclerView.setAdapter(viewAdapter);

        viewAdapter.setOnItemClickListener((adapter, view, position) -> {
            RunningInfoSection section = (RunningInfoSection) adapter.getItem(position);
            if (section.runningInfo == null) return;
            Intent intent = new Intent(getContext(), RunDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("running_info", section.runningInfo);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    public void setListData() {
        List<RunningInfo> list = RunningQueryUtil.findAllOrder();
        List<RunningInfoSection> sectionList = DataQueryModel.getSectionList(list);
        if (infoList == null) {
            infoList = new ArrayList<>();
        } else {
            infoList.clear();
        }
        if (sectionList != null) {
            infoList.addAll(sectionList);
        }
        viewAdapter.notifyDataSetChanged();
        if (!infoList.isEmpty()) {
            neverRunLayout.setVisibility(View.GONE);
        } else {
            neverRunLayout.setVisibility(View.VISIBLE);
        }
    }
}
