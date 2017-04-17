package com.cxsj.runhdu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.dd.CircularProgressButton;
import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.sport.RunningInfo;
import com.cxsj.runhdu.sport.StepSensorAcceleration;
import com.cxsj.runhdu.sport.StepSensorBase;
import com.cxsj.runhdu.sport.StepSensorPedometer;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.ImageNumberDisplay;

import java.util.ArrayList;
import java.util.List;

public class RunningActivity extends AppCompatActivity implements StepSensorBase.StepCallback {

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
    private boolean isValid = true;
    private int satelliteNum;
    private PowerManager.WakeLock wakeLock;//唤醒锁

    private String startTime;
    private String endTime;
    private String sensorMode = "未使用传感器";
    private int steps;
    private int energy;
    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_running);
        acquireWakeLock();
        init();
        checkGPS();
        initMapLocation();
        startButton.setOnClickListener(v -> {
            if (startButton.getProgress() == 0) {
                startRunning();
            } else {
                startButton.setProgress(0);
                stopRunning();
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
        releaseWakeLock();
    }

    @Override
    public void onBackPressed() {
        if (runningStatus == 1) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
            dialog.setTitle("正在跑步").setMessage("如果退出，数据将会丢失。你确定退出吗？");
            dialog.setPositiveButton("确定", (d, which) -> {
                RunningActivity.super.onBackPressed();
            });
            dialog.setNegativeButton("取消", (d, which) -> {
            });
            dialog.create().show();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //初始化变量
    private void init() {

        client = new LocationClient(getApplicationContext());
        client.registerLocationListener(new RunningActivity.MyLocationListener());
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        mapView = (MapView) findViewById(R.id.bmapView);
        startButton = (CircularProgressButton) findViewById(R.id.cpb_button);
        latLngText = (TextView) findViewById(R.id.lat_lng_text);
        speedNumber = (TextView) findViewById(R.id.speed_text);
        distanceNumber = (ImageNumberDisplay) findViewById(R.id.distance_text);
        stepNumber = (ImageNumberDisplay) findViewById(R.id.running_step);
        energyNumber = (ImageNumberDisplay) findViewById(R.id.running_energy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.running_toolbar);
        toolbar.setTitle("开始跑步");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timer = (Chronometer) findViewById(R.id.timer);
        baiduMap = mapView.getMap();
        startButton.setClickable(false);
    }

    /**
     * 打开activity时把地图移动到现在所在的位置
     */
    private void initMapLocation() {
        initLocation(1500);
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
        if (!Utility.isNetworkAvailable(getApplicationContext())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
            dialog.setTitle("没有网络").setMessage("请连接网络后，再开始跑步。");
            dialog.setPositiveButton("知道了", (d, which) -> {
            });
            dialog.create().show();
            return;
        }

        if (!checkGPS()) {
            return;
        }

        //如果卫星数量小于最小允许跑步的卫星数，不允许跑步
        if (satelliteNum <= 5) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
            dialog.setTitle("GPS无法工作").setMessage("GPS信号较弱或无信号。请走到开阔地带再开始跑步。");
            dialog.setPositiveButton("知道了", (d, which) -> {
            });
            dialog.create().show();
            return;
        }

        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0" + String.valueOf(hour) + ":%s");
        timer.start();//开始计时

        startButton.setProgress(100);
        runningStatus = 1;
        pointList.clear();//开始跑步前，把点列表清空
        initLocation(locTime);
        client.start();
        startTime = Utility.getTime(Types.TYPE_CURRENT_TIME);

        //开始记步
        registerStepSensor();
    }

    private void stopRunning() {

        runningStatus = 2;
        timer.stop();

        startButton.setClickable(false);
        startButton.setIdleText("跑步完成");
        client.stop();

        if (!pointList.isEmpty()) {
            addMarker(pointList.get(pointList.size() - 1), R.drawable.ic_loc_end);
        }

        msUpdate = MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().zoom(18).build()
        );
        baiduMap.setMapStatus(msUpdate);

        locTimes = 0;//重置定位次数

        stepSensor.unregisterStep();
        saveToDatabase();

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

    private void saveToDatabase() {
        //跑步信息保存数据库
        distance = Integer.parseInt(distanceNumber.getNumber());
        if (distance < 20) {
            isValid = false;
            startButton.setClickable(false);
            startButton.setErrorText("跑步无效");
            startButton.setProgress(-1);
            new AlertDialog.Builder(this)
                    .setTitle("跑步无效")
                    .setCancelable(false)
                    .setMessage("跑步路程太短，本次跑步无效。")
                    .setPositiveButton("知道了", (d, which) -> {
                    }).show();
            return;
        }
        endTime = Utility.getTime(Types.TYPE_CURRENT_TIME);
        steps = Integer.parseInt(stepNumber.getNumber());
        energy = Integer.parseInt(energyNumber.getNumber());
        RunningInfo runningInfo = new RunningInfo(
                Utility.getTime(Types.TYPE_MONTH_DATE),
                startTime,
                endTime,
                steps,
                distance,
                energy,
                0);
        runningInfo.save();
    }

    /**
     * 把地图移动到坐标位置
     */
    private void moveToLocation(LatLng latLng) {
        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(19).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        if (msUpdate != null) {
            baiduMap.setMapStatus(msUpdate);
        }
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
            polyline = new PolylineOptions().width(6).color(Color.RED).points(pointList);//路径折线
        }
        if (polyline != null) {
            baiduMap.addOverlay(polyline);//添加Marker
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
                wakeLock.acquire();
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
        if (!Utility.isGPSOpen(getApplicationContext())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
            dialog.setTitle("GPS未开启").setMessage("请开启GPS后，再开始跑步。");
            dialog.setPositiveButton("知道了", (d, w) -> {
            });

            dialog.setNegativeButton("去设置", (d, w) -> {
                Intent intent = new Intent
                        (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            dialog.create().show();
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

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            if (bdLocation == null) return;
            final int type = bdLocation.getLocType();
            final LatLng latLng =
                    new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            final StringBuilder builder = new StringBuilder();
            builder.append("第" + (++locTimes) + "次定位，类型")
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
                    .append("\n" + sensorMode);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    latLngText.setText(builder.toString());
                    //开始跑步后，才显示速度和里程并保存轨迹点集
                    if (runningStatus == 1) {
                        if (type != 61) return;
                        speedNumber.setText(Utility.formatDecimal(bdLocation.getSpeed() / 3.6, 2));//速度数字
                        distanceNumber.setNumber(String.valueOf(Utility.getRunningDistance(pointList)));//路程数字
                        double dis = Double.parseDouble(distanceNumber.getNumber());
                        pointList.add(latLng);
                        //跑步前的定位中，保存卫星数量;如果不是卫星定位，则等待
                    } else if (runningStatus == 0) {
                        satelliteNum = bdLocation.getSatelliteNumber();
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
                    drawCurrentPoint(latLng);
                }
            });

//            //跑步前的定位中，如果出现定位错误，弹出对话框
//            if (!isRunningStart && (bdLocation.getLocType() != 61 && bdLocation.getLocType() != 161)) {
//                final AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
//                dialog.setTitle("无法定位").setMessage("请检查网络连接，或确认授予了定位权限。错误码" + bdLocation.getLocType());
//                dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.create().show();
//                    }
//                });
//            }

            //进行一次定位后，才可以点击跑步按钮。
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    startButton.setEnabled(true);
//                    startButton.setIdleText("开始跑步");
//                }
//            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }
}
