package com.cxsj.runhdu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cxsj.runhdu.adapters.MyFragmentPagerAdapter;
import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.controller.DataPresentUtil;
import com.cxsj.runhdu.controller.DataSyncUtil;
import com.cxsj.runhdu.model.gson.Running;
import com.cxsj.runhdu.model.gson.UpdateInfo;
import com.cxsj.runhdu.model.sport.RunningInfo;
import com.cxsj.runhdu.utils.ActivityManager;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.ImageSaveUtil;
import com.cxsj.runhdu.utils.QueryUtil;
import com.cxsj.runhdu.utils.ShareUtil;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.GradeProgressView;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private GradeProgressView circleProgress;
    private ImageView menuButton;
    private ImageView menuBgImg;
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
    private RunListFragment runListFragment;
    private StatisticsFragment statisticsFragment;
    private RelativeLayout progressViewLayout;
    private RelativeLayout chartView;

    private List<String> mTitle = new ArrayList<>();
    private List<Fragment> mFragment = new ArrayList<>();

//    private SocialService socialService;
//    private SocialServiceConn conn;
//    private SocialReceiver receiver;

    private int chartColumnNum;//图表显示的列数
    private float targetSteps;
    private UpdateInfo mUpdateInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (TextUtils.isEmpty(username)) exitLogin();

        //初始化view
        initView();
        //检查更新
        checkUpdate();

//        bindService(new Intent(this, SocialService.class), conn, BIND_AUTO_CREATE);
//        registerSocialReceiver();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    chartView.setVisibility(View.INVISIBLE);
                    progressViewLayout.setVisibility(View.VISIBLE);
                } else {
                    chartView.setVisibility(View.VISIBLE);
                    progressViewLayout.setVisibility(View.INVISIBLE);
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
            @SuppressLint({"SetTextI18n", "WrongConstant"})
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, -1 * (chartColumnNum - columnIndex - 1));
                String year = String.valueOf(calendar.get(Calendar.YEAR));
                String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                String day = String.valueOf(calendar.get(Calendar.DATE));

                int dis = 0;
                List<RunningInfo> runningInfoList = QueryUtil.findOrder(
                        "year = ? and month = ? and date = ?",
                        year, month, day);
                for (RunningInfo info : runningInfoList) {
                    dis += info.getDistance();
                }
                dataDescription.setText(
                        String.format("%s月%s日 跑步%d次 | %d步 | %d米",
                                month, day, runningInfoList.size(), (int) value.getValue(), dis));
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: 已经调用");
        super.onResume();
        //更新设置选项
        initSettings();
        //设置ProgressView和柱状图
        setMainData();
        //检查服务器数据是否与本地一致
        checkServerData();
    }

    @Override
    protected void onDestroy() {
        closeProgressDialog();
        HttpUtil.load(URLs.OFF_LINE)
                .addParam("UserName", username)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    }
                });
//        unbindService(conn);
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//        }
        super.onDestroy();
    }

    //检查网络数据
    private void checkServerData() {
        if (!isSyncOn) return;
        //获取服务器端的跑步次数
        DataSyncUtil.checkServerData(username, new DataSyncUtil.CheckDataCallback() {
            @Override
            public void onCheckFailure(String msg) {
                showSnackBar(msg);
            }

            @Override
            public void onCheckSuccess(int serverTimes, int localTimes) {
                if (serverTimes != localTimes) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("同步数据")
                            .setMessage(String.format("本地跑步数据与服务器不一致：\n" +
                                    "本地数据：%d条；\n" +
                                    "服务器数据：%d条。\n" +
                                    "请选择操作：", localTimes, serverTimes))
                            .setPositiveButton("服务器数据同步至本地", (dialog, which) ->
                                    syncFromServer())
                            .setNegativeButton("本地数据上传至服务器", (dialog, which) -> {
                                List<RunningInfo> infoList = QueryUtil.findAllOrder();
                                if (infoList.isEmpty()) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("本地无数据")
                                            .setMessage("本地没有跑步数据，执行此操作将会清空服务器的数据。是否选择从服务器同步？")
                                            .setNegativeButton("否，清空服务器", (dialog1, which1) -> uploadToServer())
                                            .setPositiveButton("是，从服务器同步", (dialog12, which12) -> syncFromServer())
                                            .create().show();
                                } else {
                                    uploadToServer();
                                }
                            })
                            .setNeutralButton("以后再说", null).create().show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

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
        menuBgImg = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.menu_bg_img);
        columnChart = (ColumnChartView) findViewById(R.id.column_chart);
        welcomeText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.welcome_text);
        profileImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        dataDescription = (TextView) findViewById(R.id.data_description);
        progressViewLayout = (RelativeLayout) findViewById(R.id.progress_view_layout);
        chartView = (RelativeLayout) findViewById(R.id.chart_view_layout);
        //conn = new SocialServiceConn();
        setToolbar(R.id.toolbar_main, false);

        menuButton.setOnClickListener(this);
        fab.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        menuBgImg.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        //初始化进度条数值
        circleProgress.setProgressWidthAnimation(0);
        //初始化Title
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.title_activity_main));
        //初始化TabLayout
        mTitle.add("跑步详情");
        mTitle.add("数据统计");
        mFragment.add(new RunListFragment());
        mFragment.add(new StatisticsFragment());
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mTitle, mFragment);
        runListFragment = (RunListFragment) adapter.getItem(0);
        statisticsFragment = (StatisticsFragment) adapter.getItem(1);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //初始化ColumnChart
        columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        columnChart.setInteractive(true);
        columnChart.setZoomEnabled(false);
        columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chartView.setVisibility(View.INVISIBLE);

        //初始化welcome文字
        welcomeText.setText(Utility.getTime(Types.TYPE_AM_PM) + "好，" + username + "！");
    }

    private void initSettings() {
        //初始化数据库
        LitePalDB litePalDB = LitePalDB.fromDefault(username);
        LitePal.use(litePalDB);

        //用户名为空时，跳转到WelcomeActivity
        if (TextUtils.isEmpty(username)) {
            ActivityManager.finishAll();
            toActivity(this, WelcomeActivity.class);
        }
        //设置头像
        String fileName = (String) prefs.get(username + "_profile_path", null);
        if (!TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "加载本地图片");
            Uri uri = Uri.fromFile(new File(fileName));
            profileImage.setImageURI(uri);
        } else {
            Log.d(TAG, "使用Glide加载网络图片");
            Glide.with(this).load(URLs.PROFILE_URL + username + ".JPEG")
                    .asBitmap().error(R.drawable.photo).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    profileImage.setImageBitmap(resource);
                    String fileDir = ImageSaveUtil.saveToSDCard(MainActivity.this, resource, "profile.jpg");
                    prefs.put(username + "_profile_path", fileDir);
                }
            });
        }

        //设置菜单栏背景图
        String menuBgUri = (String) prefs.get("menu_bg_uri", null);
        if (!TextUtils.isEmpty(menuBgUri)) {
            Glide.with(this).load(Uri.parse(menuBgUri)).into(menuBgImg);
        }

        chartColumnNum = Integer.parseInt(
                (String) defaultPrefs.get("chart_column_num", "30"));
        targetSteps = Float.parseFloat(
                (String) defaultPrefs.get("target_steps", "5000")
        );

        dataDescription.setText("点击柱形图，查看详细信息");
    }

    private void setMainData() {
        setProgressView();
        setChartData();
    }

    private void setAllData() {
        setMainData();
        runListFragment.setListData();
        statisticsFragment.updateData();
    }

    @SuppressLint("SetTextI18n")
    private void setProgressView() {
        circleProgress.setProgress(0);
        DataPresentUtil.setProgressViewData((steps, times, dis, energy) -> {
            progressRunTimesText.setText("今日跑步" + times + "次");
            progressStepsText.setText(String.valueOf(steps));
            progressDisEnergyText.setText(String.format("%sKM | %d千卡",
                    Utility.formatDecimal(dis / 1000.0, 2), energy));
            circleProgress.setProgressWidthAnimation((int) (steps / targetSteps * 100));
        });
    }

    @SuppressLint("WrongConstant")
    private void setChartData() {
        DataPresentUtil.setColumnChartViewData(chartColumnNum, data -> {
            columnChart.setColumnChartData(data);
            Viewport v2 = new Viewport(columnChart.getMaximumViewport());
            v2.right = chartColumnNum;
            v2.left = chartColumnNum - 7;
            columnChart.setCurrentViewport(v2);
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showUpdateDialogBeforeExit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sport_status:
                break;
            case R.id.quit:
                showUpdateDialogBeforeExit();
                break;
            case R.id.friend:
                toActivity(this, FriendActivity.class);
                break;
            case R.id.sunlight_long_run:
                toActivity(MainActivity.this, SunnyRunActivity.class);
                break;
            case R.id.lab:
                toActivity(this, LabActivity.class);
                break;
            case R.id.share:
                ShareUtil.setDelay(500);
                ShareUtil.takeAndShare(this);
                break;
            case R.id.about:
                toActivity(MainActivity.this, AboutActivity.class);
                break;
            case R.id.settings:
                toActivity(this, SettingsActivity.class);
                break;
            default:
                showComingSoonDialog();
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
                checkRunPermission();
                break;
            case R.id.menu_bg_img:
            case R.id.profile_image:
                String[] items = {"修改头像", "更改背景图", "退出登录"};
                new AlertDialog.Builder(this)
                        .setItems(items, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    choosePhoto(Types.TYPE_CHANGE_PROFILE);
                                    break;
                                case 1:
                                    choosePhoto(Types.TYPE_CHANGE_MENU_BG);
                                    break;
                                case 2:
                                    exitLogin();
                                    break;
                                default:
                            }
                        }).show();
                break;
            default:
                break;
        }
    }

    //开启跑步Activity，此方法包括一些跑步条件检查
    public void startRunActivity() {
        //检查网络
        if (isSyncOn) {
            if (!Utility.isNetworkAvailable(getApplicationContext())) {
                Snackbar.make(collapsingToolbarLayout, R.string.internet_not_connect, Snackbar.LENGTH_LONG)
                        .setAction("知道了", v -> {
                        }).show();
                return;
            }
        }
        toActivity(MainActivity.this, RunningActivity.class);
        //...
    }

    //检查跑步权限
    private void checkRunPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
            @Override
            public void onGranted() {
                startRunActivity();
            }

            @Override
            public void onDenied(List<String> permissions) {
                StringBuilder builder = new StringBuilder();
                builder.append("跑步前请允许以下权限：\n");
                for (String permission : permissions) {
                    switch (permission) {
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            builder.append("定位");
                            break;
                        case Manifest.permission.READ_PHONE_STATE:
                            builder.append("获取手机信息");
                            break;
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            builder.append("读写手机存储");
                            break;
                        default:
                    }
                }

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("权限不足")
                        .setMessage(builder.toString())
                        .setPositiveButton("知道了", null)
                        .create().show();
            }
        });
    }

    //开始选择照片
    private void choosePhoto(int type) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, type);
    }

    //开启裁剪照片Activity
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 192);
        intent.putExtra("outputY", 192);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, Types.TYPE_SAVE_PROFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Types.TYPE_CHANGE_PROFILE:
                Uri profileUri = data.getData();
                startPhotoZoom(profileUri);
                break;
            case Types.TYPE_CHANGE_MENU_BG:
                Uri bgUri = data.getData();
                prefs.put("menu_bg_uri", bgUri.toString());
                Glide.with(MainActivity.this).load(bgUri).into(menuBgImg);
                break;
            case Types.TYPE_SAVE_PROFILE:
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    profileImage.setImageBitmap(bitmap);
                    String saveDir = ImageSaveUtil.saveToSDCard(
                            this, bitmap, "profile.jpg");
                    prefs.put(username + "_profile_path", saveDir);
                    ImageSaveUtil.saveToServer(username, bitmap);
                }
                break;
            default:
                break;
        }
    }

    private void showSnackBar(String text) {
        if (text == null) return;
        Snackbar.make(collapsingToolbarLayout,
                text, Snackbar.LENGTH_LONG)
                .setAction("知道了", v -> {
                }).show();
    }

    //同步从服务器得到的数据到本地并显示
    public void syncFromServer() {
        showProgressDialog("正在从服务器同步...");

        DataSyncUtil.downloadFromServer(username, new DataSyncUtil.DownloadRunDataCallback() {
            @Override
            public void onFailure(String msg) {
                closeProgressDialog();
                showSnackBar(msg);
            }

            @Override
            public void onSuccess(Running running) {
                closeProgressDialog();
                if (running == null) {
                    showSnackBar("同步失败。");
                } else {
                    showSnackBar("同步成功。");
                    DataSupport.deleteAll(RunningInfo.class);
                    List<RunningInfo> serverInfo = running.dataList;
                    DataSupport.saveAll(serverInfo);
                    setAllData();
                }
            }
        });
    }

    //把本地的数据同步至服务器
    private void uploadToServer() {
        showProgressDialog("正在上传至服务器...");
        DataSyncUtil.uploadAllToServer(username, new DataSyncUtil.SyncDataCallback() {
            @Override
            public void onSyncFailure(String msg) {
                closeProgressDialog();
                showSnackBar(msg);
            }

            @Override
            public void onSyncSuccess() {
                closeProgressDialog();
                showSnackBar("上传成功。");
            }
        });
    }

    private void checkUpdate() {
        DataSyncUtil.checkUpdate(this, new DataSyncUtil.UpdateCheckCallback() {
            @Override
            public void onSuccess(UpdateInfo updateInfo) {
                if (updateInfo.isUpdate()) {
                    String ignoreVersion = (String) prefs.get("ignore_version", "");
                    if (!updateInfo.getLatestVersion().equals(ignoreVersion)) {
                        mUpdateInfo = updateInfo;
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //退出前，如果有更新，给予提示。
    private void showUpdateDialogBeforeExit() {
        if (mUpdateInfo == null) {
            finish();
            return;
        }
        String dialogStr = "若不升级，请按返回键直接退出。\n\n" +
                "当前版本：" +
                mUpdateInfo.getCurrentVersion() +
                "\n最新版本：" +
                mUpdateInfo.getLatestVersion() +
                "\n\n" +
                mUpdateInfo.getStatement();
        new AlertDialog.Builder(this)
                .setTitle("退出前，升级一下？")
                .setMessage(dialogStr)
                .setPositiveButton("立即升级", (dialog, which) -> {
                    Intent it = new Intent(Intent.ACTION_VIEW,
                            Utility.getDownloadUri(mUpdateInfo.getLatestVersion()));
                    startActivity(it);
                    mUpdateInfo = null;
                })
                .setNeutralButton("不提示此版本", (dialog, which) -> {
                    prefs.put("ignore_version", mUpdateInfo.getLatestVersion());
                    showSnackBar("不再提示此版本，但是仍然可以手动检测升级。");
                    mUpdateInfo = null;
                })
                .setNegativeButton("立即退出", (dialog, which) -> finish())
                .setOnCancelListener(dialog -> finish())
                .create().show();
    }

//    private void registerSocialReceiver() {
//        receiver = new SocialReceiver();
//        IntentFilter filter = new IntentFilter("com.sailflorve.runhdu.social");
//        registerReceiver(receiver, filter);
//    }
//
//    public class SocialServiceConn implements ServiceConnection {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            socialService = ((SocialService.LocalBinder) service).getService();
//            socialService.getData();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            socialService = null;
//        }
//    }
//
//    public class SocialReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            NotificationManager manager = (NotificationManager)
//                    getSystemService(NOTIFICATION_SERVICE);
//            Notification notification = new NotificationCompat.Builder(MainActivity.this)
//                    .setContentTitle("通知")
//                    .setContentText(intent.getStringExtra("json"))
//                    .setWhen(System.currentTimeMillis())
//                    .setSmallIcon(R.drawable.ic_start_run)
//                    .build();
//            manager.notify(0, notification);
//        }
//    }
}
