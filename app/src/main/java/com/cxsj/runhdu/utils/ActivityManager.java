package com.cxsj.runhdu.utils;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {

    private static String TAG = "ActivityManager";
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        Log.d(TAG, "addActivity: "+activity.getLocalClassName());
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        if (activity != null) {
            Log.d(TAG, "removeActivity: "+activity.getLocalClassName());
            activities.remove(activity);
        }
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (activity != null) {
                Log.d(TAG, "finishAll: " + activity.getLocalClassName());
                activity.finish();
            }
        }
    }
}
