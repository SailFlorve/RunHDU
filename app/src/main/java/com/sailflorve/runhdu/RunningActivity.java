package com.sailflorve.runhdu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
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
import com.sailflorve.runhdu.utils.Utility;
import com.sailflorve.runhdu.view.NumberView;

import java.util.ArrayList;
import java.util.List;

public class RunningActivity extends AppCompatActivity {

    private CircularProgressButton startButton;
    private TextView latLngText;
    private NumberView speedNumber;
    private NumberView distanceNumber;
    private Chronometer timer;

    private MapView mapView = null;
    private BaiduMap baiduMap = null;
    private LocationClient client = null;
    private MapStatusUpdate msUpdate = null;
    private BitmapDescriptor locBitmap;
    private OverlayOptions overlay;  //地图覆盖物
    private PolylineOptions polyline = null;  //线覆盖
    private int locTime = 2000;//定位时间
    private List<LatLng> pointList = new ArrayList<>();

    private int locTimes = 0;//定位的次数
    private boolean isRunningStart = false;//跑步前的定位
    private int satelliteNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_running);

        init();
        initMapLocation();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButton.getProgress() == 0) {
                    startRunning();
                } else {
                    stopRunning();
                    startButton.setProgress(0);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.stop();
    }

    @Override
    public void onBackPressed() {
        if (isRunningStart) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
            dialog.setTitle("正在跑步").setMessage("如果退出，数据将会丢失。你确定退出吗？");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RunningActivity.super.onBackPressed();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.create().show();
        } else {
            super.onBackPressed();
        }
    }

    //初始化变量
    private void init() {

        client = new LocationClient(getApplicationContext());
        client.registerLocationListener(new RunningActivity.MyLocationListener());
        mapView = (MapView) findViewById(R.id.bmapView);
        startButton = (CircularProgressButton) findViewById(R.id.cpb_button);
        latLngText = (TextView) findViewById(R.id.lat_lng_text);
        speedNumber = (NumberView) findViewById(R.id.speed_text);
        distanceNumber = (NumberView) findViewById(R.id.distance_text);
        timer = (Chronometer) findViewById(R.id.timer);
        baiduMap = mapView.getMap();
        startButton.setEnabled(false);
        startButton.setIdleText("等待定位...");
    }

    /**
     * 打开activity时把地图移动到现在所在的位置
     */
    private void initMapLocation() {
        initLocation(0);
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
            dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.create().show();
            return;
        }

        if (!Utility.isGPSOpen(getApplicationContext())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
            dialog.setTitle("GPS未开启").setMessage("请开启GPS后，再开始跑步。");
            dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            dialog.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent
                            (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            dialog.create().show();
            return;
        }

        //如果卫星数量小于最小允许跑步的卫星数，不允许跑步
        if (satelliteNum <= 5) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
            dialog.setTitle("GPS无法工作").setMessage("GPS信号较弱或无信号。请走到开阔地带再开始跑步。");
            dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.create().show();
            return;
        }

        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0" + String.valueOf(hour) + ":%s");
        timer.start();//开始计时

        startButton.setProgress(100);
        isRunningStart = true;
        pointList.clear();//开始跑步前，把点列表清空
        initLocation(locTime);
        client.start();
        //记步算法
    }

    private void stopRunning() {
        timer.stop();
        startButton.setEnabled(false);
        startButton.setIdleText("跑步完成，感谢测试");
        client.stop();
        msUpdate = MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().zoom(18).build()
        );
        baiduMap.setMapStatus(msUpdate);
        isRunningStart = false;
        locTimes = 0;//重置定位次数
        // TODO: 结束RunningActivity，转到结果Activity
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

        // TODO: 跑步开始后，绘制跑步开始标记

        //画定位图标
        locBitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        overlay = new MarkerOptions().position(point)
                .icon(locBitmap).zIndex(9).draggable(true);//地图Marker标记
        baiduMap.addOverlay(overlay);//添加路径折线

        if (!isRunningStart) return;//未跑步的定位不画点

        //画路径线

        polyline = null;
        if (pointList.size() >= 2 && pointList.size() < 10000) {
            polyline = new PolylineOptions().width(6).color(Color.RED).points(pointList);//路径折线
        }
        if (polyline != null) {
            baiduMap.addOverlay(polyline);//添加Marker
        }

    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            if (bdLocation == null) return;
            if (isRunningStart && bdLocation.getLocType() != 61) return;
            final LatLng latLng =
                    new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

            //跑步前的定位中，保存卫星数量
            if (!isRunningStart) {
                satelliteNum = bdLocation.getSatelliteNumber();
            }

            // TODO 忽略非GPS定位

            final StringBuilder builder = new StringBuilder();
            pointList.add(latLng);
            builder.append("调试日志: 定位" + (++locTimes) + "次，类型")
                    .append(bdLocation.getLocType())
                    .append("，卫星")
                    .append(bdLocation.getSatelliteNumber())
                    .append("，精度")
                    .append(Utility.formatDecimal(bdLocation.getRadius()))
                    .append("米\n经度: ")
                    .append(latLng.longitude)
                    .append("，")
                    .append("纬度: ")
                    .append(latLng.latitude);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    latLngText.setText(builder.toString());
                    //开始跑步后，才显示速度和里程。
                    if (isRunningStart) {
                        speedNumber.setText(Utility.formatDecimal(bdLocation.getSpeed() / 3.6));//速度数字
                        distanceNumber.setText(String.valueOf(Utility.getRunningDistance(pointList)));//路程数字
                    }
                    drawCurrentPoint(latLng);
                }
            });

            //跑步前的定位中，如果出现定位错误，弹出对话框
            if (!isRunningStart && (bdLocation.getLocType() != 61 && bdLocation.getLocType() != 161)) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(RunningActivity.this);
                dialog.setTitle("无法定位").setMessage("请检查网络连接，或确认授予了定位权限。错误码" + bdLocation.getLocType());
                dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.create().show();
                    }
                });
            }

            //进行一次定位后，才可以点击跑步按钮。
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startButton.setEnabled(true);
                    startButton.setIdleText("开始跑步");
                }
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }
}
