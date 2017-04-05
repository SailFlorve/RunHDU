package com.sailflorve.runhdu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sailflorve.runhdu.utils.Utility;
import com.sailflorve.runhdu.view.GradeProgressView;
import com.sailflorve.runhdu.adapters.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CollapsingToolbarLayoutState state;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private GradeProgressView circleProgress;
    private ImageView menuButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView dataDescription;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private ColumnChartView columnChart;
    private FrameLayout progressView;
    private LinearLayout chartView;

    private List<String> mTitle = new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private String[] chartLabels;
    private float[] chartValues = new float[]{1231, 1233, 1334, 1456, 1345, 1234, 1267, 2345, 2567, 2234, 3467, 2345, 2345, 3245};


    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERMEDIATE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        circleProgress = (GradeProgressView) findViewById(R.id.circle_progress);
        menuButton = (ImageView) findViewById(R.id.menu_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        columnChart = (ColumnChartView) findViewById(R.id.column_chart);
        dataDescription = (TextView) findViewById(R.id.data_description);
        progressView = (FrameLayout) findViewById(R.id.progress_view_layout);
        chartView = (LinearLayout) findViewById(R.id.chart_view_layout);

        setSupportActionBar(toolbar);

        initView();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {//完全展开
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;
                        //完全展开后的动作
                        collapsingToolbarLayout.setTitle(" ");
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//完全折叠
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        //完全折叠后的动作
                        collapsingToolbarLayout.setTitle(getResources().getString(R.string.title_activity_main));
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                } else {//中间状态
                    if (state != CollapsingToolbarLayoutState.INTERMEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            //由折叠变为中间状态时需要做的操作
                            //collapsingToolbarLayout.setTitle("");
                        }
                        //中间状态时的动作
                        state = CollapsingToolbarLayoutState.INTERMEDIATE;//修改状态标记为中间
                    }
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    chartView.setVisibility(View.INVISIBLE);
                    progressView.setVisibility(View.VISIBLE);
                } else {
                    chartView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

        columnChart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                String[] date = chartLabels[columnIndex].split("/");
                dataDescription.setText(date[0] + "月" + date[1] + "日  " + (int) value.getValue() + "步  跑步 1 次");
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void initView() {
        //初始化Title
        collapsingToolbarLayout.setTitle(" ");
        //初始化TabLayout
        mTitle.add("本周跑步统计");
        mTitle.add("历史跑步数据");

        mFragment.add(new TodayFragment());
        mFragment.add(new HistoryFragment());

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mTitle, mFragment);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //初始化进度条数值
        circleProgress.setProgressWidthAnimation(60);
        //初始化ColumnChart
        chartView.setVisibility(View.INVISIBLE);
        chartLabels = new String[]{"03/05", "03/06", "03/07",
                "03/08", "03/09", "03/10", "03/11", "03/12", "03/13", "03/14",
                "03/15", "03/16", "03/17", "03/18"};

        chartValues = new float[]{1231, 1233, 1334, 1456, 1345, 1234, 1267, 2345, 2567, 2234, 3467, 2345, 2345, 3245};

        columnChart.setColumnChartData(Utility.setChartData(chartLabels, chartValues));
        columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        columnChart.setInteractive(true);
        columnChart.setZoomEnabled(false);
        columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        setColumnChartValue();

        String[] date = chartLabels[chartLabels.length - 1].split("/");
        dataDescription.setText(date[0] + "月" + date[1] + "日  " + (int) chartValues[chartValues.length - 1] + "步  跑步 1 次");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.quit:
                finish();
                break;
            case R.id.sunlight_long_run:
                Intent intent = new Intent(MainActivity.this, SunnyRunActivity.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //修改ChartValues后，调用此方法
    private void setColumnChartValue() {
        columnChart.setColumnChartData(Utility.setChartData(chartLabels, chartValues));
        final Viewport v1 = new Viewport(columnChart.getMaximumViewport());
        v1.bottom = 1000;
        v1.top = 4000;
        columnChart.setMaximumViewport(v1);
        //设置当前的窗口显示多少个坐标数据
        Viewport v2 = new Viewport(columnChart.getMaximumViewport());
        v2.left = 7;
        v2.right = 14;
        columnChart.setCurrentViewport(v2);
    }

    private void startRunActivity() {
        if (!Utility.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "开始跑步前，请连接网络。", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, RunningActivity.class);
        startActivity(intent);
    }

    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            startRunActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限，才可开始跑步。", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    startRunActivity();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
