package com.cxsj.runhdu.application;

import android.annotation.SuppressLint;
import android.content.Context;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        if (context == null) {
            throw new NullPointerException("Context is null");
        }
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        context = null;
    }
}
