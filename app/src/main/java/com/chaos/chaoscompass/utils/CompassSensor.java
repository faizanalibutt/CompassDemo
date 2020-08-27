package com.chaos.chaoscompass.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class CompassSensor implements SensorEventListener {
    private static final String TAG = "CompassSensor";
    private float currentAzimuth;

    public interface CompassListener {
        void onNewAzimuth(float azimuth, float pitch, float oldDegree);

        void magneticField(float magneticField);

        void sensorCalibration(String type);
    }

    private CompassListener listener;

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] R = new float[9];
    private float[] I = new float[9];

    private float azimuth;
    private float azimuthFix;
    Context context;

    public CompassSensor(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public boolean start() {
        if (!isDeviceCompassSensorAvailable()) return false;
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
        return true;
    }

    public boolean isDeviceCompassSensorAvailable() {
        return gsensor != null && msensor != null;
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setAzimuthFix(float fix) {
        azimuthFix = fix;
    }

    public void resetAzimuthFix() {
        setAzimuthFix(0);
    }

    public void setListener(CompassListener l) {
        listener = l;
    }

    public void setCompassAnimation(float azimuth, View view) {
        // Log.d(TAG, "will set rotation from " + currentAzimuth + " to "                + azimuth);
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        view.startAnimation(an);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];

                // mGravity = event.values;

                // Log.e(TAG, Float.toString(mGravity[0]));
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
                // Log.e(TAG, Float.toString(event.values[0]));
                float magneticField = (float) Math.sqrt(mGeomagnetic[0] * mGeomagnetic[0]
                        + mGeomagnetic[1] * mGeomagnetic[1]
                        + mGeomagnetic[2] * mGeomagnetic[2]);
                if (listener != null) {
                    listener.magneticField(magneticField);
                }

            }


            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + azimuthFix + 360) % 360;
                float pitch = (float) Math.toDegrees(orientation[1]);
                float roll = (float) Math.toDegrees(orientation[2]);
                // azimuth = (azimuth + azimuthFix + 360) % 294;
                // Log.d(TAG, "azimuth (deg): " + azimuth);
                if (listener != null) {
                    listener.onNewAzimuth(azimuth, pitch, roll);
                    // Log.d(TAG, "azimuth (deg): " + azimuth);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD ) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    //Timber.e("SENSOR_STATUS_ACCURACY_LOW");
                    //setType(context.getString(com.compassfree.digitalcompass.forandroid.app.R.string.low));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    //Timber.e("SENSOR_STATUS_ACCURACY_MEDIUM");
                    //setType(context.getString(com.compassfree.digitalcompass.forandroid.app.R.string.medium));

                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    //Timber.e("SENSOR_STATUS_ACCURACY_HIGH");
//                    setType("High");
                    break;
            }
        }
    }

    void setType(String type){
        if (listener != null) {
            listener.sensorCalibration(type);
        }
    }
}
