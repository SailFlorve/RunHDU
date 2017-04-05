package com.sailflorve.runhdu.utils;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;

/**
 * Created by Sail on 2017/3/15 0030.
 * 工具类集合
 */
public class Utility {

    /**
     * 生成ColumnCharData对象，作用于HelloChart表
     *
     * @param labels X轴坐标说明
     * @param nums   每个坐标对应值
     * @return 距离总和
     */
    public static ColumnChartData setChartData(String[] labels, float[] nums) {
        if (labels.length != nums.length) return null;
        ColumnChartData data;
        // 14列，每列1个柱状图。
        int numSubcolumns = 1;
        int numColumns = 14;
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
                values.add(new SubcolumnValue(nums[i], Color.parseColor("#03A9F4")));
            }
            //创建Column对象
            Column column = new Column(values);
            //是否有数据标注
            column.setHasLabels(true);
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(labels[i]));
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


    /**
     * 获取手机IMEI
     *
     * @param context
     * @return IMEI
     */
    public static String getDeviceId(Context context) {
        String Imei = "NULL";
        try {
            Imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            Toast.makeText(context, "获取IMEI码失败", Toast.LENGTH_SHORT);
            Imei = "NULL";
        }
        return Imei;
    }


    /**
     * GPS是否打开
     *
     * @param context
     * @return GPS开启状态
     */
    public static boolean isGPSOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean result = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return result;
    }

    /**
     * 位置集合里每连续两点距离的总和
     *
     * @param points 位置集合
     * @return 距离总和
     */
    public static int getRunningDistance(List<LatLng> points) {
        if (points.size() == 0 || points.size() == 1) return 0;
        double result = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            result += DistanceUtil.getDistance(points.get(i), points.get(i + 1));
        }
        return (int) result;
    }


    /**
     * 是否有网络
     *
     * @param context
     * @return 网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param d 需要处理的数字
     * @return 保留一位小数后的字符串
     */
    public static String formatDecimal(double d) {
        return String.format("%.1f",d);
    }

    /**
     * 获取AlertDialog.Builder实例。
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static AlertDialog.Builder getDialogBuilder(Context context,String title,String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(title);
        b.setMessage(message);
        b.setCancelable(false);
        return b;
    }

    public static String checkLogin(String username, String password) {
        return null;
    }
}
