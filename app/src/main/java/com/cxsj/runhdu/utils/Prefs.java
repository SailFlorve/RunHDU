package com.cxsj.runhdu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Sail on 2017/4/3 0003.
 * SharedPreference
 */

public class Prefs {

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public Prefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }

    public void put(String key, Object object) {
        if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        }

        if (object instanceof String) {
            editor.putString(key, (String) object);
        }

        if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        }
        editor.apply();
    }

    public Object get(String key, Object defaultObj) {
        if (defaultObj instanceof Integer) {
            return prefs.getInt(key, (int) defaultObj);
        }

        if (defaultObj instanceof String || defaultObj == null) {
            return prefs.getString(key, (String) defaultObj);
        }

        if (defaultObj instanceof Boolean) {
            return prefs.getBoolean(key, (Boolean) defaultObj);
        }
        return null;
    }
}
