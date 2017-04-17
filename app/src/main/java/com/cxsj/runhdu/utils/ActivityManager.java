package com.cxsj.runhdu.utils;


import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    private static  List<Activity> activities=new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static  void finishAll()
    {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
