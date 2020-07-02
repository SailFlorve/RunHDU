package com.cxsj.runhdu.appfunctions.running;

import android.util.Log;

import com.cxsj.runhdu.base.BaseModel;
import com.cxsj.runhdu.bean.gson.Running;
import com.cxsj.runhdu.bean.sport.RunningInfo;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.RunningQueryUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by SailFlorve on 2017/8/25 0025.
 * 跑步数据请求封装
 */

public class RunningModel extends BaseModel {
    public interface GetTimesCallback {
        void onFailure(String msg);

        void onSuccess(int serverTimes, int localTimes);
    }

    public interface GetRunningInfoCallback {
        void onFailure(String msg);

        void onSuccess(Running running);
    }

    /**
     * 获取服务器上的跑步次数和本地跑步次数
     */
    public static void getRunningTimes(String username, GetTimesCallback callback) {
        HttpUtil.load(URLs.GET_RUN_TIMES)
                .addParam("userName", username)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onFailure(CONNECT_FAILED));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        mHandler.post(() -> {
                            int serverTimes;
                            try {
                                serverTimes = Integer.parseInt(result);
                            } catch (NumberFormatException e) {
                                callback.onFailure(RETURN_DATA_ERROR);
                                e.printStackTrace();
                                return;
                            }
                            int localTimes = DataSupport.count(RunningInfo.class);
                            int finalServerTimes = serverTimes;
                            callback.onSuccess(finalServerTimes, localTimes);
                        });
                    }
                });
    }

    public static void getRunningInfo(String username, GetRunningInfoCallback callback) {
        HttpUtil.load(URLs.GET_RUN_INFO)
                .addParam("userName", username)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onFailure("网络连接失败。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        mHandler.post(() -> {
                            Running running = null;
                            try {
                                running = new Gson().fromJson(result, Running.class);
                                callback.onSuccess(running);
                            } catch (JsonSyntaxException e) {
                                callback.onFailure(RETURN_DATA_ERROR);
                                e.printStackTrace();
                            }
                        });
                    }
                });
    }

    public static void uploadAll(String username, BaseCallback callback) {
        Running running = new Running();
        running.username = username;
        List<RunningInfo> infoList = RunningQueryUtil.findAllOrder();
        running.times = infoList.size();
        running.dataList = infoList;
        String json = new Gson().toJson(running);
        Log.d(TAG, json);
        HttpUtil.load(URLs.UPLOAD_ALL_INFO)
                .addParam("json", json)
                .post(getStatusHttpCallback(callback));
    }

    public static void upload(String username, RunningInfo runningInfo, BaseCallback callback) {
        HttpUtil.load(URLs.UPLOAD_RUN_INFO)
                .addParam("userName", username)
                .addParam("runId", runningInfo.getRunId())
                .addParam("runMode", runningInfo.getRunMode())
                .addParam("year", runningInfo.getYear())
                .addParam("month", runningInfo.getMonth())
                .addParam("date", runningInfo.getDate())
                .addParam("startTime", runningInfo.getStartTime())
                .addParam("duration", runningInfo.getDuration())
                .addParam("steps", String.valueOf(runningInfo.getSteps()))
                .addParam("distance", String.valueOf(runningInfo.getDistance()))
                .addParam("energy", String.valueOf(runningInfo.getEnergy()))
                .addParam("speed", String.valueOf(runningInfo.getSpeed()))
                .addParam("trailList", runningInfo.getCompressedTrailList())
                .post(getStatusHttpCallback(callback));
    }

    public static void delete(String username, String deleteId, BaseCallback callback) {
        HttpUtil.load(URLs.DELETE_ITEM)
                .addParam("userName", username)
                .addParam("runId", deleteId)
                .post(getStatusHttpCallback(callback));
    }

    public static void deleteAll(String username, BaseCallback callback) {
        HttpUtil.load(URLs.CLEAR_DATA)
                .addParam("userName", username)
                .post(getStatusHttpCallback(callback));
    }
}
