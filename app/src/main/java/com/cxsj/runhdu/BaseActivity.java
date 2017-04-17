package com.cxsj.runhdu;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Sail on 2017/4/13 0013.
 * Base activity to observe.
 */

public class BaseActivity extends AppCompatActivity {
    private final String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: ");
        super.onNewIntent(intent);
    }
}
