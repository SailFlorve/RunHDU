package com.cxsj.runhdu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.cxsj.runhdu.constant.Strings;
import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.model.sport.RunningInfo;
import com.cxsj.runhdu.sensor.StepSensorAcceleration;
import com.cxsj.runhdu.sensor.StepSensorBase;
import com.cxsj.runhdu.sensor.StepSensorPedometer;
import com.cxsj.runhdu.utils.SyncUtil;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.ImageNumberDisplay;
import com.dd.CircularProgressButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RunningActivity extends BaseActivity
        implements StepSensorBase.StepCallback {

    private LinearLayout rootLayout;
    private CircularProgressButton startButton;
    private TextView latLngText;
    private TextView speedNumber;
    private ImageNumberDisplay distanceNumber;
    private ImageNumberDisplay stepNumber;
    private ImageNumberDisplay energyNumber;
    private Chronometer timer;
    private StepSensorBase stepSensor = null;
    private MapView mapView = null;
    private BaiduMap baiduMap = null;
    private LocationClient client = null;
    private MapStatusUpdate msUpdate = null;
    private BitmapDescriptor locBitmap;
    private OverlayOptions overlay;  //地图覆盖物
    private PolylineOptions polyline = null;  //线覆盖
    private int locTime = 1500;//定位时间
    private List<LatLng> pointList = new ArrayList<>();

    private int locTimes = 0;//定位的次数
    private int runningStatus = 0;//0:未开始跑步 1：正在跑步 2.已经跑完
    private boolean isOutdoor = false;
    private boolean isSyncing = false;

    private boolean runWithoutGPS = false;
    private int satelliteNum;
    private PowerManager.WakeLock wakeLock;//唤醒锁

    private String startTime;
    private String sensorMode = "未使用传感器";
    private StringBuilder pointListBuilder = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_running);
        //acquireWakeLock();
        initView();
        initSettings();
        checkGPS();
        initMapLocation();

        startButton.setOnClickListener(v -> {
            if (startButton.getProgress() == 0) {
                startRunning();
            } else {
                new AlertDialog.Builder(RunningActivity.this)
                        .setTitle("结束跑步")
                        .setMessage("你确定要结束跑步吗？")
                        .setPositiveButton("确定", (dialog, which) -> stopRunning())
                        .setNegativeButton("取消", null).create().show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (stepSensor != null) {
            stepSensor.unregisterStep();
            stepSensor = null;
        }
        client.stop();
        //releaseWakeLock();
    }

    private void initSettings() {
        locTime = (int) (Double.parseDouble(
                (String) prefs.get("locate_rate", "1.5")) * 1000);
        boolean showLog = (boolean) prefs.get("show_debug_log", false);
        if (showLog) latLngText.setVisibility(View.VISIBLE);
        else latLngText.setVisibility(View.GONE);
        runWithoutGPS = (boolean) prefs.get("only_gps_run", false);
    }

    @Override
    public void onBackPressed() {
        if (runningStatus == 1) {
            new AlertDialog.Builder(RunningActivity.this)
                    .setTitle("正在跑步").setMessage(R.string.exit_ensure)
                    .setPositiveButton("确定", (dialog, which) -> RunningActivity.super.onBackPressed())
                    .setNegativeButton("取消", null).create().show();
        } else if (isSyncing) {
            Toast.makeText(this, "正在同步数据，请稍等...", Toast.LENGTH_SHORT).show();
        } else {
            toActivity(this, MainActivity.class);
            super.onBackPressed();
        }
    }

    //初始化变量

    private void initView() {
        client = new LocationClient(getApplicationContext());
        client.registerLocationListener(new RunningActivity.MyLocationListener());
        rootLayout = (LinearLayout) findViewById(R.id.running_root_layout);
        mapView = (MapView) findViewById(R.id.bmapView);
        startButton = (CircularProgressButton) findViewById(R.id.cpb_button);
        latLngText = (TextView) findViewById(R.id.lat_lng_text);
        speedNumber = (TextView) findViewById(R.id.speed_text);
        distanceNumber = (ImageNumberDisplay) findViewById(R.id.distance_text);
        stepNumber = (ImageNumberDisplay) findViewById(R.id.running_step);
        energyNumber = (ImageNumberDisplay) findViewById(R.id.running_energy);
        setToolbar(R.id.running_toolbar, true);

        timer = (Chronometer) findViewById(R.id.timer);
        baiduMap = mapView.getMap();
        startButton.setClickable(false);
        startButton.setIndeterminateProgressMode(true);
    }

    /**
     * 打开activity时把地图移动到现在所在的位置
     */
    private void initMapLocation() {
        initLocation(2500);
        client.start();
    }

    private void initLocation(int time) {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(time);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        client.setLocOption(option);
    }

    private void startRunning() {
        if (isSyncOn) {
            if (!Utility.isNetworkAvailable(getApplicationContext())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
                dialog.setTitle("没有网络").setMessage(R.string.internet_not_connect);
                dialog.setPositiveButton("知道了", null);
                dialog.create().show();
                return;
            }
        }
        if (!runWithoutGPS) {
            if (!checkGPS()) {
                return;
            }

            //如果卫星数量小于最小允许跑步的卫星数，不允许跑步
            if (satelliteNum <= 5) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
                dialog.setTitle("GPS无法工作").setMessage(R.string.no_gps);
                dialog.setPositiveButton("知道了", null);
                dialog.create().show();
                return;
            }
        }
        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0" + String.valueOf(hour) + ":%s");
        timer.start();//开始计时

        startButton.setProgress(100);
        runningStatus = 1;
        pointList.clear();//开始跑步前，把点列表清空
        pointListBuilder = new StringBuilder();
        initLocation(locTime);
        client.start();
        startTime = Utility.getTime(Types.TYPE_CURRENT_TIME);

        //开始记步
        registerStepSensor();
    }

    private void stopRunning() {
        runningStatus = 2;
        timer.stop();
        client.stop();

        startButton.setClickable(false);
        startButton.setIdleText("跑步完成");
        startButton.setProgress(0);

        if (!pointList.isEmpty()) {
            addMarker(pointList.get(pointList.size() - 1), R.drawable.ic_loc_end);
        }

        msUpdate = MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().zoom(17).build()
        );
        baiduMap.setMapStatus(msUpdate);

        locTimes = 0;//重置定位次数

        stepSensor.unregisterStep();
        saveRunData();

        // 计算平均速度
        float speed = Integer.parseInt(distanceNumber.getNumber()) / getSeconds();
        speedNumber.setText(Utility.formatDecimal(speed, 2));
    }

    private float getSeconds() {
        String[] times = timer.getText().toString().split(":");
        int hours = Integer.parseInt(times[0]);
        int minutes = Integer.parseInt(times[1]);
        int seconds = Integer.parseInt(times[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    private void addPoint(LatLng point) {
        LatLng lastPoint;
        if (pointList.size() > 0) {
            lastPoint = pointList.get(pointList.size() - 1);
            if (lastPoint.latitude == point.latitude
                    && lastPoint.longitude == point.longitude) {
                return;
            }
        }
        pointList.add(point);
        pointListBuilder.append(point.longitude)
                .append(",")
                .append(point.latitude)
                .append(",");
    }

    private void registerStepSensor() {
        if (stepSensor != null) {
            stepSensor.unregisterStep();
            stepSensor = null;
        }

        stepSensor = new StepSensorPedometer(this, this);
        if (!stepSensor.registerStep()) {
            sensorMode = "计步传感器不可用，";
            stepSensor = new StepSensorAcceleration(this, this);
            if (!stepSensor.registerStep()) {
                sensorMode += "加速度传感器不可用，无法计步";
            } else {
                sensorMode += "正在使用加速度传感器";
            }
        } else {
            sensorMode = "正在使用计步传感器";
        }

    }

    private void saveRunData() {
        //跑步信息保存数据库
        int distance = Integer.parseInt(distanceNumber.getNumber());
        if (!runWithoutGPS) {
            if (distance < 20) {
                startButton.setClickable(false);
                startButton.setErrorText("跑步无效");
                startButton.setProgress(-1);
                new AlertDialog.Builder(this)
                        .setTitle("跑步无效")
                        .setCancelable(false)
                        .setMessage(R.string.dis_not_enough)
                        .setPositiveButton("知道了", null).show();
                return;
            }
        }
        int steps = Integer.parseInt(stepNumber.getNumber());
        int energy = Integer.parseInt(energyNumber.getNumber());
        float speed = Float.parseFloat(speedNumber.getText().toString());

        RunningInfo runningInfo = new RunningInfo(
                Utility.getTime(Types.TYPE_STRING_FORM),
                isOutdoor ? Strings.RUN_OUTDOORS : Strings.RUN_INDOORS,
                Utility.getTime(Calendar.YEAR),
                Utility.getTime(Types.TYPE_MONTH),
                Utility.getTime(Calendar.DATE),
                startTime,
                timer.getText().toString(),
                steps,
                distance,
                energy,
                speed,
                pointListBuilder.toString()
        );

        runningInfo.save();
        if (isSyncOn) updateToServer(runningInfo);
    }

    private void updateToServer(RunningInfo runningInfo) {
        isSyncing = true;
        Log.d(TAG, "saveRunData: " + runningInfo.getRunId());
        SyncUtil.uploadSingleToServer(username, runningInfo, new SyncUtil.SyncDataCallback() {
            @Override
            public void onSyncFailure(String msg) {
                isSyncing = false;
                Snackbar.make(rootLayout, msg, Snackbar.LENGTH_LONG)
                        .setAction("重试", v -> updateToServer(runningInfo)).show();
            }

            @Override
            public void onSyncSuccess() {
                isSyncing = false;
                Snackbar.make(rootLayout, "上传跑步数据成功。", Snackbar.LENGTH_SHORT)
                        .setAction("知道了", v -> {
                        }).show();
            }
        });
    }

    /**
     * 把地图移动到坐标位置
     */
    private void moveToLocation(LatLng latLng) {
        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(msUpdate);
    }

    /**
     * 画出实时线路点
     *
     * @param point 最新线路点
     */
    private void drawCurrentPoint(LatLng point) {
        baiduMap.clear();

        //把地图移动到point点
        moveToLocation(point);

        //绘制跑步开始标记
        if (!pointList.isEmpty()) {
            addMarker(pointList.get(0), R.drawable.ic_loc_start);
        }

        //画定位图标
        if (runningStatus == 2) {
            addMarker(point, R.drawable.ic_loc_end);
        } else {
            addMarker(point, R.drawable.ic_loc_normal);
        }

        //画路径线
        polyline = null;
        if (pointList.size() >= 2 && pointList.size() < 10000) {
            polyline = new PolylineOptions()
                    .width(10)
                    .color(R.color.colorPrimary)
                    .zIndex(0)
                    .points(pointList);
            baiduMap.addOverlay(polyline);//添加Marker
        } else if (pointList.size() >= 10000) {
            startButton.callOnClick();
            new AlertDialog.Builder(this)
                    .setTitle("超时提示")
                    .setMessage(R.string.over_time)
                    .setPositiveButton(R.string.got_it, null)
                    .create().show();

        }
    }

    private void addMarker(LatLng point, int markerResId) {
        locBitmap = BitmapDescriptorFactory.fromResource(markerResId);
        overlay = new MarkerOptions().position(point)
                .icon(locBitmap).zIndex(9).draggable(false);//地图Marker标记
        baiduMap.addOverlay(overlay);
    }

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
            }
        }
    }

    //释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    private boolean checkGPS() {
        if (runWithoutGPS) return true;
        if (!Utility.isGPSOpen(getApplicationContext())) {
            new AlertDialog.Builder(RunningActivity.this)
                    .setTitle("GPS未开启").setMessage(R.string.not_open_gps)
                    .setPositiveButton("知道了", null)
                    .setNegativeButton("去设置", (dialog, which) -> {
                        Intent intent = new Intent
                                (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }).create().show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStepChanged(int stepNum) {
        stepNumber.setNumber(String.valueOf(stepNum));
        energyNumber.setNumber(String.valueOf((int) (stepNum * 0.09)));
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            if (bdLocation == null) return;
            final int type = bdLocation.getLocType();
            final LatLng latLng =
                    new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            final StringBuilder builder = new StringBuilder();
            builder.append("第")
                    .append(++locTimes)
                    .append("次定位，类型")
                    .append(type)
                    .append("，卫星数")
                    .append(bdLocation.getSatelliteNumber())
                    .append("，精度")
                    .append(Utility.formatDecimal(bdLocation.getRadius(), 1))
                    .append("米\n当前位置：经度 ")
                    .append(latLng.longitude)
                    .append("，")
                    .append("纬度 ")
                    .append(latLng.latitude)
                    .append("\n")
                    .append(sensorMode);

            runOnUiThread(() -> {
                latLngText.setText(builder.toString());
                //开始跑步后，才显示速度和里程并保存轨迹点集
                if (runningStatus == 1) {
                    if (type != 61
                            || bdLocation.getRadius() > 20) return;
                    speedNumber.setText(Utility.formatDecimal(bdLocation.getSpeed() / 3.6, 2));//速度数字
                    distanceNumber.setNumber(String.valueOf(Utility.getRunningDistance(pointList)));//路程数字
                    isOutdoor = true;
                    // TODO:轨迹纠偏
                    addPoint(latLng);
                } else if (runningStatus == 0) {
                    //跑步前的定位中，保存卫星数量;如果不是卫星定位，则等待
                    satelliteNum = bdLocation.getSatelliteNumber();
                    if (!runWithoutGPS) {
                        if (type != 61) {
                            switch (type) {
                                case 62:
                                case 63:
                                case 68:
                                    startButton.setErrorText("连接失败");
                                    break;
                                case 161:
                                    startButton.setErrorText("等待GPS");
                                    break;
                                case 167:
                                    startButton.setErrorText("没有权限");
                                    Snackbar.make(rootLayout,
                                            "您没有允许定位权限，无法定位。",
                                            Snackbar.LENGTH_LONG)
                                            .setAction("知道了", v -> {
                                            }).show();
                                    client.stop();
                                    break;
                                case 505:
                                    startButton.setErrorText("KEY错误");
                                    break;
                                default:
                                    startButton.setErrorText("等待定位");
                                    break;
                            }
                            startButton.setProgress(-1);
                            startButton.setClickable(false);

                        } else {
                            startButton.setProgress(0);
                            startButton.setClickable(true);
                        }
                    }
                }
                drawCurrentPoint(latLng);
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }
}
