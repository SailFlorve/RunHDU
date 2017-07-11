package com.cxsj.runhdu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.cxsj.runhdu.controller.DataSyncUtil;
import com.cxsj.runhdu.model.sport.RunningInfo;
import com.cxsj.runhdu.utils.ImageSaveUtil;
import com.cxsj.runhdu.utils.ShareUtil;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.NumberView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;

/**
 * 跑步数据详情页面
 */
public class RunDetailsActivity extends BaseActivity {

    private TextView disText;
    private NumberView stepNumber;
    private NumberView speedNumber;
    private NumberView dateNumber;
    private NumberView startTimeNumber;
    private NumberView durationNumber;
    private NumberView energyNumber;
    private MapView mapView;
    private BaiduMap baiduMap;
    private TextView noTrailText;
    private LinearLayout floatInfo;

    private boolean isFriend;
    private RunningInfo mRunningInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_run_details);

        disText = (TextView) findViewById(R.id.dis_text_details);
        stepNumber = (NumberView) findViewById(R.id.step_text_details);
        speedNumber = (NumberView) findViewById(R.id.speed_text_details);
        dateNumber = (NumberView) findViewById(R.id.date_text_details);
        stepNumber = (NumberView) findViewById(R.id.step_text_details);
        durationNumber = (NumberView) findViewById(R.id.duration_details);
        energyNumber = (NumberView) findViewById(R.id.energy_details);
        startTimeNumber = (NumberView) findViewById(R.id.start_time_text_details);
        mapView = (MapView) findViewById(R.id.map_view_details);
        noTrailText = (TextView) findViewById(R.id.no_trail_text);
        floatInfo = (LinearLayout) findViewById(R.id.run_detail_float_info);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        setToolbar(R.id.details_toolbar, true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            //是否为好友的跑步信息
            isFriend = bundle.getBoolean("is_friend", false);
            //获得序列化runningInfo
            mRunningInfo = (RunningInfo) intent.getSerializableExtra("running_info");
            setAllData(mRunningInfo);
        } else {
            Toast.makeText(this, "发生异常。", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //如果是好友的跑步信息，则不显示删除图标
        if (!isFriend) {
            getMenuInflater().inflate(R.menu.run_details_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete_run_item:
                new AlertDialog.Builder(this)
                        .setTitle("删除跑步记录")
                        .setMessage("你确定删除此条跑步信息吗？")
                        .setPositiveButton("删除", (dialog, which) -> requestDeleteItem())
                        .setNegativeButton("不删除", null).create().show();
                break;
            case R.id.share_run_item:
                baiduMap.snapshot(bitmap -> {
                    Bitmap backBitmap = ShareUtil.takeScreenShot(this);
                    Canvas canvas = new Canvas(backBitmap);
                    canvas.drawBitmap(bitmap, 0, getSupportActionBar().getHeight(), null);
                    canvas.drawBitmap(ShareUtil.takeScreenShot(floatInfo), 0, getSupportActionBar().getHeight(), null);
                    String imagePath = ImageSaveUtil.saveToSDCard(this, backBitmap, "share_tmp.png");
                    ShareUtil.openShareDialog(this, imagePath);
                });
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAllData(RunningInfo info) {
        if (info == null) return;
        disText.setText(Utility.formatDecimal(info.getDistance() / 1000.0, 2));
        stepNumber.setText(String.valueOf(info.getSteps()));
        speedNumber.setText(String.valueOf(info.getSpeed()));
        dateNumber.setText(info.getMonth() + "月" + info.getDate() + "日");
        startTimeNumber.setText(info.getStartTime());
        durationNumber.setText(info.getDuration());
        energyNumber.setText(info.getEnergy() + "");
        String pointStr = info.getTrailList();
        String[] points = pointStr.split(",");
        if (points.length <= 3) {
            noTrailText.setText("无轨迹");
            noTrailText.setVisibility(View.VISIBLE);
            baiduMap.getUiSettings().setAllGesturesEnabled(false);
            return;
        } else if (points.length % 2 != 0) {
            noTrailText.setText("轨迹异常");
            noTrailText.setVisibility(View.VISIBLE);
            baiduMap.getUiSettings().setAllGesturesEnabled(false);
            return;
        }
        List<LatLng> pointList = new ArrayList<>();
        for (int i = 0; i < points.length; i += 2) {
            double longitude = Double.parseDouble(points[i]);
            double latitude = Double.parseDouble(points[i + 1]);
            pointList.add(new LatLng(latitude, longitude));
        }
        drawTrailLines(pointList);
    }

    /**
     * 把地图移动到坐标位置
     */
    private void moveToLocation(LatLng latLng) {
        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();
        MapStatusUpdate msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(msUpdate);
    }

    private void drawTrailLines(List<LatLng> pointList) {
        baiduMap.clear();

        //把地图移动到point点
        moveToLocation(pointList.get(pointList.size() / 2));

        //绘制跑步开始标记
        if (!pointList.isEmpty()) {
            addMarker(pointList.get(0), R.drawable.ic_loc_start);
        }
        //绘制跑步结束标记
        if (!pointList.isEmpty()) {
            addMarker(pointList.get(pointList.size() - 1), R.drawable.ic_loc_end);
        }

        //画路径线
        PolylineOptions polyline;
        if (pointList.size() >= 2 && pointList.size() < 10000) {
            polyline = new PolylineOptions()
                    .width(6)
                    .color(ContextCompat.getColor(this, R.color.colorPrimary))
                    .zIndex(0)
                    .points(pointList);
            baiduMap.addOverlay(polyline);//添加Marker
        }
    }

    private void addMarker(LatLng point, int markerResId) {
        BitmapDescriptor locBitmap = BitmapDescriptorFactory.fromResource(markerResId);
        MarkerOptions overlay = new MarkerOptions().position(point)
                .icon(locBitmap).zIndex(9).draggable(false);//地图Marker标记
        baiduMap.addOverlay(overlay);
    }

    private void deleteLocalItem() {
        DataSupport.deleteAll(RunningInfo.class.getSimpleName(), "runId = ?", mRunningInfo.getRunId());
        toActivity(this, MainActivity.class);
        finish();
    }

    private void requestDeleteItem() {
        if (isSyncOn) {
            showProgressDialog("正在同步删除至服务器...");
            DataSyncUtil.deleteSingleLocalAndServerData(username, mRunningInfo.getRunId(), new DataSyncUtil.SyncDataCallback() {
                @Override
                public void onSyncFailure(String msg) {
                    closeProgressDialog();
                    Toast.makeText(RunDetailsActivity.this, "网络连接错误，删除失败。", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSyncSuccess() {
                    closeProgressDialog();
                    Toast.makeText(RunDetailsActivity.this, "删除成功。", Toast.LENGTH_SHORT).show();
                    deleteLocalItem();
                }
            });
        } else {
            deleteLocalItem();
            Toast.makeText(RunDetailsActivity.this, "删除成功。", Toast.LENGTH_SHORT).show();
        }
    }
}
