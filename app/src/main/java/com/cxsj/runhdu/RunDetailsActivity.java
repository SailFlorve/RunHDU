package com.cxsj.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.cxsj.runhdu.sport.RunningInfo;
import com.cxsj.runhdu.utils.Utility;
import com.cxsj.runhdu.view.NumberView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class RunDetailsActivity extends AppCompatActivity {

    private TextView disText;
    private NumberView stepNumber;
    private NumberView speedNumber;
    private NumberView dateNumber;
    private NumberView startTimeNumber;
    private NumberView durationNumber;
    private NumberView energyNumber;
    private MapView mapView;
    private BaiduMap baiduMap;

    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        setSupportActionBar((Toolbar) findViewById(R.id.details_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
        }

        setAllData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAllData() {
        RunningInfo info = DataSupport.find(RunningInfo.class, id);
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
            Toast.makeText(this, "轨迹点不存在。", Toast.LENGTH_SHORT).show();
            return;
        } else if (points.length % 2 != 0) {
            Toast.makeText(this, "轨迹点异常。", Toast.LENGTH_SHORT).show();
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
        PolylineOptions polyline = null;
        if (pointList.size() >= 2 && pointList.size() < 10000) {
            polyline = new PolylineOptions()
                    .width(10)
                    .color(ContextCompat.getColor(this,R.color.colorPrimary))
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
}
