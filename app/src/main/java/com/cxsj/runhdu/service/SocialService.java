package com.cxsj.runhdu.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telecom.ConnectionService;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 用于社交数据实时获取的服务。
 */
public class SocialService extends Service {

    int i = 0;
    private Timer timer;
    private String TAG = "SocialService";

    public SocialService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        timer.cancel();
        super.onDestroy();
    }

    public void getData() {
        timer = new Timer();
        new Thread(() -> timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        sendSocialBroadcast("第" + i++);
                    }
                }, 1000, 5000)).start();
    }

    private void sendSocialBroadcast(String str) {
        Intent intent = new Intent();
        intent.setAction("com.sailflorve.runhdu.social");
        intent.putExtra("json", str);
        sendBroadcast(intent);
    }

    public final class LocalBinder extends Binder {
        public SocialService getService() {
            return SocialService.this;
        }
    }
}
