package com.cxsj.runhdu.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.ImageView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.constant.Types;
import com.cxsj.runhdu.constant.URLs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sail on 2017/3/15 0030.
 * 工具类集合
 */
public class Utility {
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
                // 当前所连接的网络可用
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    /**
     * @param d   需要处理的数字
     * @param num 保留位数(只能是1或者2)
     * @return 保留num位小数后的字符串
     */
    public static String formatDecimal(double d, int num) {
        if (num == 2) {
            return String.format("%.2f", d);
        } else {
            return String.format("%.1f", d);
        }

    }

    /**
     * 获取特定类型的时间的字符串表示
     *
     * @param type 获取的时间类型，
     *             Types.TYPE_AM_PM：返回上午/下午/晚上
     *             Types.TYPE_MONTH_DATE：返回日期，例如2月15日
     *             Types.TYPE_CURRENT_TIME：返回时间，例如18：23
     *             其他类型：Calendar.TYPE
     * @return 类型字符串
     */
    @SuppressLint("WrongConstant")
    public static String getTime(int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        switch (type) {
            case Types.TYPE_AM_PM:
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour >= 6 && hour < 9) {
                    return "早上";
                } else if (hour >= 9 && hour < 12) {
                    return "上午";
                } else if (hour >= 12 && hour < 13) {
                    return "中午";
                } else if (hour >= 13 && hour < 19) {
                    return "下午";
                } else {
                    return "晚上";
                }
            case Types.TYPE_MONTH_DATE:
                return new SimpleDateFormat("M月d日", Locale.getDefault()).format(new Date());
            case Types.TYPE_CURRENT_TIME:
                return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            case Types.TYPE_MONTH:
                return String.valueOf(calendar.get(Calendar.MONTH) + 1);
            case Types.TYPE_STRING_FORM:
                return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            default:
                return String.valueOf(calendar.get(type));
        }
    }

    /**
     * 返回计时器时间字符串的分钟数量。
     *
     * @return 两个时间间隔的分钟数
     */
    public static int getMinutes(String time) {
        String[] times = time.split(":");
        int minutes = Integer.parseInt(times[1]);
        int hours = Integer.parseInt(times[0]);
        int seconds = Integer.parseInt(times[2]);
        return hours * 60 + minutes + seconds / 60;
    }

    public static Uri getDownloadUri(String latestVersion) {
        return Uri.parse(URLs.DOWNLOAD + latestVersion.replace(".", "") + ".apk");
    }

    public static void loadFriendProfileImg(Context context, String username, ImageView view) {
        Glide.with(context).load(URLs.PROFILE_URL + username + ".JPEG")
                .signature(new StringSignature(Utility.getTime(Calendar.HOUR_OF_DAY)))
                .dontAnimate().error(R.drawable.photo).into(view);
    }
}
