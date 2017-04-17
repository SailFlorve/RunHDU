package com.cxsj.runhdu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.sport.RunningInfo;
import com.cxsj.runhdu.utils.Prefs;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.GradeProgressView;
import com.cxsj.runhdu.adapters.MyFragmentPagerAdapter;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CollapsingToolbarLayoutState state;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private GradeProgressView circleProgress;
    private ImageView menuButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView dataDescription;
    private TextView progressRunTimesText;
    private TextView progressStepsText;
    private TextView progressDisEnergyText;
    private TextView welcomeText;
    private CircleImageView profileImage;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private ColumnChartView columnChart;
    private TodayFragment todayFragment;
    private HistoryFragment historyFragment;
    private FrameLayout progressView;
    private LinearLayout chartView;
    private List<String> mTitle = new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private List<RunningInfo> runningInfoList = new ArrayList<>();
    private List<String> chartLabels = new ArrayList<>();
    private List<Float> chartValues = new ArrayList<>();
    ;

    private Prefs prefs;
    private LitePalDB litePalDB;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERMEDIATE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new Prefs(this);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        progressRunTimesText = (TextView) findViewById(R.id.progress_times);
        progressStepsText = (TextView) findViewById(R.id.progress_step_num);
        progressDisEnergyText = (TextView) findViewById(R.id.progress_dis_energy);
        circleProgress = (GradeProgressView) findViewById(R.id.circle_progress);
        menuButton = (ImageView) findViewById(R.id.menu_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        columnChart = (ColumnChartView) findViewById(R.id.column_chart);
        welcomeText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.welcome_text);
        profileImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        dataDescription = (TextView) findViewById(R.id.data_description);
        progressView = (FrameLayout) findViewById(R.id.progress_view_layout);
        chartView = (LinearLayout) findViewById(R.id.chart_view_layout);
        setSupportActionBar(toolbar);

        menuButton.setOnClickListener(this);
        fab.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        initView();

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


        columnChart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                String[] dates = chartLabels.get(columnIndex).split("/");
                String date = dates[0] + "月" + dates[1] + "日";
                int dis = 0;
                runningInfoList = DataSupport.where("date = ?", date)
                        .find(RunningInfo.class);
                for (RunningInfo info : runningInfoList) {
                    dis += info.getDistance();
                }
                dataDescription.setText(date + "\n跑步" + runningInfoList.size() + "次 | " + (int) value.getValue() + "步 | " + dis + "米");
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void initView() {
        //初始化进度条数值
        circleProgress.setProgressWidthAnimation(0);
        //初始化Title
        collapsingToolbarLayout.setTitle(" ");
        //初始化TabLayout
        mTitle.add("今日跑步情况");
        mTitle.add("跑步数据统计");
        mFragment.add(new TodayFragment());
        mFragment.add(new HistoryFragment());
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mTitle, mFragment);
        todayFragment = (TodayFragment) adapter.getItem(0);
        historyFragment = (HistoryFragment) adapter.getItem(1);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //初始化ColumnChart
        columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        columnChart.setInteractive(true);
        columnChart.setZoomEnabled(false);
        columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chartView.setVisibility(View.INVISIBLE);
        //初始化welcome文字
        String username = (String) prefs.get("username", null);
        if (!TextUtils.isEmpty(username)) {
            welcomeText.setText(Utility.getTime(Types.TYPE_AM_PM) + "好，" + username + "！");
        } else {
            exitLogin();
        }
        //初始化数据库
        litePalDB = LitePalDB.fromDefault((String) prefs.get("username", null));
        LitePal.use(litePalDB);

        setMainData();
    }

    private void setMainData() {
        setProgressView();
        setChartData();
    }


    private void setAllData() {
        setMainData();
        try {
            todayFragment.updateData();
            historyFragment.updateData();
        } catch (NullPointerException e) {
            Toast.makeText(this, "APP异常，现已恢复。如果出现此提示，请联系开发者！", Toast.LENGTH_LONG).show();
            recreate();
        }
    }

    private void setProgressView() {
        //从数据库里读出数据。
        runningInfoList = DataSupport
                .where("date = ?", Utility.getTime(Types.TYPE_MONTH_DATE))
                .find(RunningInfo.class);
        int steps = 0;
        int times = 0;
        int dis = 0;
        int energy = 0;
        for (RunningInfo info : runningInfoList) {
            steps += info.getSteps();
            times++;
            dis += info.getDistance();
            energy += info.getEnergy();
        }
        progressRunTimesText.setText("今日跑步" + times + "次");
        progressStepsText.setText(String.valueOf(steps));
        progressDisEnergyText.setText(Utility.formatDecimal(dis / 1000.0, 2) + "KM" + " | " + energy + "千卡");
        circleProgress.setProgressWidthAnimation((int) (steps / 10000.0 * 100));
    }

    private void setChartData() {
        chartLabels.clear();
        chartValues.clear();
        //初始化ColumnChart
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("M/d", Locale.CHINA);
        for (int i = 0; i < 30; i++) {
            chartLabels.add(sdf.format(c.getTime()));
            runningInfoList = DataSupport
                    .where("date = ?", c.get(Calendar.MONTH) + 1 + "月" + c.get(Calendar.DATE) + "日")
                    .find(RunningInfo.class);
            int steps = 0;
            for (RunningInfo info : runningInfoList) {
                steps += info.getSteps();
            }
            chartValues.add((float) steps);
            c.add(Calendar.DATE, -1);
        }
        Collections.reverse(chartLabels);
        Collections.reverse(chartValues);
        columnChart.setColumnChartData(getChartData(chartLabels, chartValues));

        Viewport v1 = new Viewport(columnChart.getMaximumViewport());
        v1.bottom = 0;
        if (!chartValues.isEmpty()) {
            v1.top = Collections.max(chartValues);
        } else {
            v1.top = 5000;
        }
        columnChart.setMaximumViewport(v1);
        //设置当前的窗口显示多少个坐标数据
        Viewport v2 = new Viewport(columnChart.getMaximumViewport());
        v2.right = chartLabels.size();
        v2.left = chartLabels.size() - 7;
        columnChart.setCurrentViewport(v2);

        dataDescription.setText("此处显示30天内跑步统计图\n点击柱形图，查看详细信息");

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

    @Override
    protected void onNewIntent(Intent intent) {
        setAllData();
        super.onNewIntent(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();
        switch (id) {
            case R.id.quit:
                finish();
                break;
            case R.id.sunlight_long_run:
                intent = new Intent(MainActivity.this, SunnyRunActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.clear:
                DataSupport.deleteAll(RunningInfo.class);
                setAllData();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.fab:
                requestPermission();
                break;
            case R.id.profile_image:
                String[] items = {"修改头像", "查看大图", "退出登录"};
                new AlertDialog.Builder(this)
                        .setTitle("头像设置")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        exitLogin();
                                        break;
                                    default:
                                }
                            }
                        }).show();
                break;
        }
    }

    //开启跑步Activity，此方法包括一些跑步条件检查
    public void startRunActivity() {
        if (!Utility.isNetworkAvailable(getApplicationContext())) {
            Snackbar.make(collapsingToolbarLayout, "开始跑步前，请连接网络。", Snackbar.LENGTH_LONG)
                    .setAction("知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
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
                            Snackbar.make(collapsingToolbarLayout, "必须允许定位权限，才可以开始跑步。", Snackbar.LENGTH_LONG)
                                    .setAction("知道了", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).show();
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

    private ColumnChartData getChartData(List<String> labels, List<Float> nums) {
        if (labels.size() != nums.size()
                || labels.isEmpty()
                || nums.isEmpty()) return null;

        ColumnChartData data;
        // 列，每列1个柱状图。
        int numSubcolumns = 1;
        int numColumns = labels.size();
        //圆柱对象集合
        List<Column> columns = new ArrayList<Column>();
        //子列数据集合
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; ++j) {
                //为每一柱图添加颜色和数值
                values.add(new SubcolumnValue(nums.get(i), Color.parseColor("#03A9F4")));
            }
            //创建Column对象
            Column column = new Column(values);
            //是否有数据标注
            column.setHasLabels(true);
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(labels.get(i)));
        }
        //创建一个带有之前圆柱对象column集合的ColumnChartData
        data = new ColumnChartData(columns);

        //定义x轴y轴相应参数
        Axis axisX = new Axis();

        axisX.setTextColor(Color.parseColor("#ffffff"));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        data.setAxisXBottom(axisX);
        //data.setAxisYLeft(axisY);
        return data;
    }

    private void exitLogin() {
        prefs.put("username", "");
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
