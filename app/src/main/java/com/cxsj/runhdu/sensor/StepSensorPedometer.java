package com.cxsj.runhdu.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by Sail on 2017/4/7 0007.
 * 直接使用计步传感器实现计步
 */

public class StepSensorPedometer extends StepSensorBase {
    private final String TAG = "StepSensorPedometer";
    private int lastStep = -1;
    private int liveStep = 0;
    private int increment = 0;
    private int sensorMode = 0; // 计步传感器类型
    private int preValue = 0;

    public StepSensorPedometer(Context context, StepCallback stepCallBack) {
        super(context, stepCallBack);
    }

    @Override
    protected void registerStepListener() {
        Sensor detectorSensor = null;
        Sensor countSensor = null;
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            isAvailable = false;
            return;
        }
        /*
         * TYPE_STEP_DETECTOR
         * 每次检测到步数变化，会给出变化值
         *
         * TYPE_STEP_COUNTER
         * 每次检测到步数变化，会给出总步数，精确度也更高
         */
        if (sensorManager.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_GAME)) {
            isAvailable = true;
            sensorMode = 0;
            Log.i(TAG, "计步传感器count可用！");
        } else if (sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_GAME)) {
            isAvailable = true;
            sensorMode = 1;
            Log.i(TAG, "计步传感器detector可用！");
        } else {
            isAvailable = false;
            Log.i(TAG, "计步传感器不可用！");
        }
    }

    @Override
    public void unregisterStep() {
        Log.d(TAG, "unregisterStep: ");
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        liveStep = (int) event.values[0];
        if (sensorMode == 0) {
            StepSensorBase.CURRENT_STEP += liveStep;
        } else if (sensorMode == 1) {
            if (liveStep - preValue <= 3) {
                StepSensorBase.CURRENT_STEP += liveStep - preValue;
            }
            preValue = liveStep;
        }

        stepCallback.onStepChanged(StepSensorBase.CURRENT_STEP);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
