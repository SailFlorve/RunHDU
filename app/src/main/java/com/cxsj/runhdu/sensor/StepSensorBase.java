package com.cxsj.runhdu.sensor;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Sail on 2017/4/7 0007.
 * 记步传感器抽象类，子类分为加速度传感器、速度传感器；
 */

public abstract class StepSensorBase implements SensorEventListener {

    protected Context context;
    protected StepCallback stepCallback;
    protected SensorManager sensorManager;
    protected static int CURRENT_STEP = 0;
    protected boolean isAvailable = false;

    public StepSensorBase(Context context, StepCallback stepCallback) {
        this.context = context;
        this.stepCallback = stepCallback;
    }

    public interface StepCallback {
        void onStepChanged(int stepNum);
    }

    /**
     * 开启记步
     */
    public boolean registerStep() {
        CURRENT_STEP = 0;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
        }
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        registerStepListener();
        return isAvailable;
    }

    /**
     * 注册计步监听器
     */
    protected abstract void registerStepListener();

    /**
     * 注销计步监听器
     */
    public abstract void unregisterStep();
}
