package com.cxsj.runhdu.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.cxsj.runhdu.MainActivity;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.model.gson.Running;
import com.cxsj.runhdu.model.sport.RunningInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Sail on 2017/5/10 0010.
 * 同步数据相关的类
 */

public class SyncUtil {

    private static final String TAG = "SyncUtil";
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public interface checkDataCallback {
        void onCheckFailure(String msg);

        void onCheckSuccess(int serverTimes, int localTimes);
    }

    public interface SyncDataCallback {
        void onSyncFailure(String msg);

        void onSyncSuccess();
    }

    /**
     * 将本地的数据和服务器的数据进行比较。
     */
    public static void checkServerData(String username, checkDataCallback callback) {
        HttpUtil.load(URLs.GET_RUN_TIMES)
                .addParam("userName", username)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onCheckFailure("网络连接失败。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int serverTimes;
                        String result = response.body().string();
                        Log.d(TAG, "onResponse: 返回检查次数" + result);
                        try {
                            serverTimes = Integer.parseInt(result);
                        } catch (NumberFormatException e) {
                            mHandler.post(() -> callback.onCheckFailure("返回格式有误。" + result));
                            e.printStackTrace();
                            return;
                        }
                        int localTimes = DataSupport.count(RunningInfo.class);
                        int finalServerTimes = serverTimes;
                        mHandler.post(() -> callback.onCheckSuccess(finalServerTimes, localTimes));
                    }
                });
    }

    public static void syncFromServer(String username, SyncDataCallback callback) {
        HttpUtil.load(URLs.GET_RUN_INFO)
                .addParam("userName", username)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onSyncFailure("无法同步，网络连接失败。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Running running = new Gson().fromJson(result, Running.class);
                        DataSupport.deleteAll(RunningInfo.class);
                        List<RunningInfo> serverInfo = running.dataList;
                        DataSupport.saveAll(serverInfo);
                        mHandler.post(callback::onSyncSuccess);
                    }
                });
    }

    public static void uploadAllToServer(Context context, String username, SyncDataCallback callback) {
        Running running = new Running();
        running.username = username;
        List<RunningInfo> infoList = QueryUtil.findAllOrder();
        if (infoList.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle("本地无数据")
                    .setMessage("本地没有跑步数据，执行此操作将会清空服务器的数据。是否选择从服务器同步？")
                    .setNegativeButton("继续", (dialog, which) -> {
                        running.times = infoList.size();
                        running.dataList = infoList;
                        String json = new Gson().toJson(running);
                        Log.d(TAG, json);
                        HttpUtil.load(URLs.UPLOAD_ALL_INFO)
                                .addParam("json", json)
                                .post(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        mHandler.post(() -> callback.onSyncFailure("上传失败，未连接网络。"));
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result = response.body().string();
                                        mHandler.post(() -> {
                                            if (result.equals("true")) {
                                                callback.onSyncSuccess();
                                            } else {
                                                callback.onSyncFailure("上传失败，返回格式错误。");
                                            }
                                        });
                                    }
                                });
                    })
                    .setPositiveButton("从服务器同步", (dialog, which) -> {
                        MainActivity mainActivity = (MainActivity) context;
                        mainActivity.syncFromServer();
                    })
                    .setOnCancelListener(dialog -> mHandler.post(
                            () -> callback.onSyncFailure(null))).create().show();
        }
    }

    public static void uploadSingleToServer(String username, RunningInfo runningInfo, SyncDataCallback callback) {
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
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onSyncFailure("网络连接失败。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        mHandler.post(() -> {
                            if (result.equals("true")) {
                                callback.onSyncSuccess();
                            } else {
                                callback.onSyncFailure("返回错误，上传失败。");
                            }
                        });
                    }
                });
    }

    public static void deleteSingleLocalAndServerData(String username, String deleteId, SyncDataCallback callback) {
        HttpUtil.load(URLs.DELETE_ITEM)
                .addParam("userName", username)
                .addParam("runId", deleteId)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onSyncFailure("网络连接失败。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        mHandler.post(() -> {
                            if (result.equals("true")) {
                                callback.onSyncSuccess();
                            } else {
                                callback.onSyncFailure("返回格式错误。");
                            }
                        });
                    }
                });
    }

    public static void deleteAllLocalAndServerData(String username, SyncDataCallback callback) {
        HttpUtil.load(URLs.CLEAR_DATA)
                .addParam("userName", username)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onSyncFailure("网络连接失败，无法删除服务器数据。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        mHandler.post(() -> {
                            if (result.equals("true")) {
                                DataSupport.deleteAll(RunningInfo.class);
                                callback.onSyncSuccess();
                            } else {
                                callback.onSyncFailure("删除失败，返回错误。");
                            }
                        });
                    }
                });
    }
}
