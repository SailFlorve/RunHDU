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

    public Object get(String key, Object defaultValue) {
        if (defaultValue instanceof Integer) {
            return prefs.getInt(key, (int) defaultValue);
        }

        if (defaultValue instanceof String || defaultValue == null) {
            return prefs.getString(key, (String) defaultValue);
        }

        if (defaultValue instanceof Boolean) {
            return prefs.getBoolean(key, (Boolean) defaultValue);
        }
        return null;
    }
}
