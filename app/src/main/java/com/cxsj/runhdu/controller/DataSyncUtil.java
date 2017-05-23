package com.cxsj.runhdu.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.cxsj.runhdu.MainActivity;
import com.cxsj.runhdu.R;
import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.model.gson.Running;
import com.cxsj.runhdu.model.sport.RunningInfo;
import com.cxsj.runhdu.utils.HttpUtil;
import com.cxsj.runhdu.utils.QueryUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Sail on 2017/5/10 0010.
 * 同步数据相关的类
 * 包括：从服务器下载数据、上传数据到服务器、检查更新、删除数据等
 */

public class DataSyncUtil {

    private static final String TAG = "DataSyncUtil";
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 检查服务器数据与本地数据是否一致的回调
     */
    public interface CheckDataCallback {
        void onCheckFailure(String msg);

        void onCheckSuccess(int serverTimes, int localTimes);
    }

    /**
     * 同步数据回调
     */
    public interface SyncDataCallback {
        void onSyncFailure(String msg);

        void onSyncSuccess();
    }

    /**
     * 检查更新回调
     */
    public interface UpdateCheckCallback {
        void onUpdate(String currentVersion, String latestVersion, String updateStatement);

        void onFailure(String msg);
    }

    /**
     * 将本地的数据和服务器的数据进行比较。
     */
    public static void checkServerData(String username, CheckDataCallback callback) {
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

    /**
     * 从服务器同步数据
     *
     * @param username
     * @param callback
     */
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

    /**
     * 上传所有跑步数据至服务器
     *
     * @param username
     * @param callback
     */
    public static void uploadAllToServer(String username, SyncDataCallback callback) {
        Running running = new Running();
        running.username = username;
        List<RunningInfo> infoList = QueryUtil.findAllOrder();
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
    }

    /**
     * 上传一条跑步数据至服务器
     *
     * @param username
     * @param runningInfo
     * @param callback
     */
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

    /**
     * 删除一条跑步数据并同步至服务器
     *
     * @param username
     * @param deleteId
     * @param callback
     */
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

    /**
     * 删除所有跑步数据并同步至服务器
     *
     * @param username
     * @param callback
     */
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

    /**
     * 检查更新
     *
     * @param context
     * @param callback
     */
    public static void checkUpdate(Context context, UpdateCheckCallback callback) {
        final String currentVersion = context.getResources().getString(R.string.current_version);

        HttpUtil.load(URLs.UPDATE_URL)
                .addParam("version2",
                        currentVersion)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(() -> callback.onFailure("连接更新服务器失败。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONObject updateInfo = null;
                        String isUpdate;
                        String latestVersion = null;
                        String statement = null;
                        try {
                            updateInfo = new JSONObject(response.body().string());
                            isUpdate = updateInfo.getString("isUpdate");
                            if (!updateInfo.isNull("latestVersion")) {
                                latestVersion = updateInfo.getString("latestVersion");
                            }
                            if (!updateInfo.isNull("statement")) {
                                statement = updateInfo.getString("statement");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFailure("JSON格式错误。");
                            return;
                        }

                        switch (isUpdate) {
                            case "true":
                                String finalLatestVersion = latestVersion;
                                String finalStatement = statement;
                                mHandler.post(() -> callback.onUpdate(currentVersion, finalLatestVersion, finalStatement));
                                break;
                            case "false":
                                mHandler.post(() -> callback.onFailure("没有发现新版本。"));
                                break;
                            default:
                                mHandler.post(() -> callback.onFailure("服务器异常。"));
                                break;
                        }
                    }
                });
    }
}
