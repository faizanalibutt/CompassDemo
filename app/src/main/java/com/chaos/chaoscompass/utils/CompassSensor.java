package com.chaos.chaoscompass.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import com.chaos.chaoscompass.R;
import com.chaos.chaoscompass.activity.CompassActivity;
import com.google.android.material.snackbar.Snackbar;

public class CompassSensor implements SensorEventListener {

    private static final String TAG = "CompassSensor";
    private float currentAzimuth;
    public float mDirection;

    public interface CompassListener {
        void onNewAzimuth(float azimuth, float pitch, float oldDegree);

        void magneticField(float magneticField);

        void sensorCalibration(String type);

        void orientationField(float sensorVal);
    }

    private CompassListener listener;

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private Sensor osensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] Rr = new float[9];
    private float[] I = new float[9];

    private float azimuth;
    private float azimuthFix;
    Context context;
    private float sensorVal = 0f;

    public CompassSensor(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        osensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mDirection = 0.0f;
    }

    public boolean start() {
        if (!isDeviceCompassSensorAvailable()) return false;
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, osensor,
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
        // Log.d(TAG, "will set rotation from " + currentAzimuth + " to " + azimuth);
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


            boolean success = SensorManager.getRotationMatrix(Rr, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(Rr, orientation);
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

            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                sensorVal = event.values[0];
                if (listener != null)
                    listener.orientationField(sensorVal);

                //addToLogFile(sensorEvent);
                //if (isViewAttachedToWindow()) {}

                float[] fArr = event.values;
                if (orientaionSensorNonExistent(sensorManager)) {
                    float[] fArr2 = new float[9];
                    SensorHelper.quatToRotMat(fArr2, (float[]) event.values.clone());
                    SensorHelper.rotMatToOrient(fArr, fArr2);
                }
                mDirection = Utils.normalizeDegree(fArr[0] * -1.0f);
                float f = fArr[1];
                float f2 = fArr[2];
                if (msensor == null) {
                    checkCompassAccuracy(event.accuracy);
                }
                //initGradientScreenViewStub();
                /*if (mCompassScreen != null) {
                    mCompassScreen.setCompassDirection(mDirection);
                }*/
                mGradienterScreen.setGradienterDirection(f, f2);
                if (CompassDeviceUtils.isSupportRotateCalibration() && mIsCalibrating) {
                    mTiltRotateView.update(f, f2);
                }
                float abs = Math.abs(f);
                if (!mIsCalibrating && mCompassScreen != null) {
                    mCompassScreen.setFaceDirection(abs);
                }
                mGradienterScreen.setFaceDirection(abs);
                int visibility = mCameraMask.getVisibility();
                Animation animation = mCameraMask.getAnimation();
                int i = (abs > 45.0f ? 1 : (abs == 45.0f ? 0 : -1));
                String str = "show camera mask view ";
                String str2 = CompassActivity.TAG;
                if (i > 0 && abs < 135.0f) {
                    mCameraView.setBackgroundResource(R.drawable.shape_camera_foreground);
                    if (animation == null || !animation.hasStarted() || animation.hasEnded()) {
                        if ((!mIsCalibrating || mScreen.getCurrentScreenIndex() == 1) && visibility == 0) {
                            mCameraMask.startAnimation(mCameraMaskOut);
                            mMoreBtn.startAnimation(mCameraMaskOut);
                            Log.i(str2, "invisible camera mask view ");
                        } else if (mScreen.getCurrentScreenIndex() == 0 && mIsCalibrating && visibility == 8) {
                            mCameraMask.startAnimation(mCameraMaskIn);
                            mMoreBtn.startAnimation(mCameraMaskIn);
                            Log.i(str2, str);
                        }
                    }
                } else if (visibility == 8 && (animation == null || !animation.hasStarted() || animation.hasEnded())) {
                    mCameraMask.startAnimation(mCameraMaskIn);
                    mMoreBtn.startAnimation(mCameraMaskIn);
                    Log.i(str2, str);
                }


            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    //Timber.e("SENSOR_STATUS_ACCURACY_LOW");
                    //setType(context.getString(com.compassfree.digitalcompass.forandroid.app.Rr.string.low));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    //Timber.e("SENSOR_STATUS_ACCURACY_MEDIUM");
                    //setType(context.getString(com.compassfree.digitalcompass.forandroid.app.Rr.string.medium));

                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    //Timber.e("SENSOR_STATUS_ACCURACY_HIGH");
//                    setType("High");
                    break;
            }
        }

        if (msensor == null) {
            //checkCompassAccuracy(i);
        }

    }

    void setType(String type) {
        if (listener != null) {
            listener.sensorCalibration(type);
        }
    }

    public void isSensorAvailable(View compassPane) {

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (sensor == null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensor == null) {
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (sensor == null)
                    Snackbar.make(compassPane, "Alas! your device HAS_NO_SENSOR.",
                            Snackbar.LENGTH_INDEFINITE
                    ).show();
                else
                    Snackbar.make(compassPane, "Hurrah! your device has accelerometer.",
                            Snackbar.LENGTH_INDEFINITE
                    ).show();
            } else
                Snackbar.make(compassPane, "Hurrah! your device has magnetic.",
                        Snackbar.LENGTH_INDEFINITE
                ).show();
        } else
            Snackbar.make(compassPane, "Hurrah! your device has orientation.",
                    Snackbar.LENGTH_INDEFINITE
            ).show();

        /*Resources res = compassPane.getContext().getResources();
        new AlertDialog.Builder(compassPane.getContext()).setTitle(res.getString(R.string.no_sensor))
                .setMessage(res.getString(R.string.no_sensor_explain))
                .setOnDismissListener(DialogInterface::dismiss)
                .setPositiveButton(res.getString(R.string.okay), (dialog, which) -> dialog.dismiss())
                .create()
                .show();*/
    }

    /*private val mOrientationSensorEventListener: SensorEventListener =
        object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                //addToLogFile(sensorEvent)

                val fArr = sensorEvent.values
                if (compassActivity.orientaionSensorNonExistent(compassActivity.mSensorManager)) {
                    val fArr2 = FloatArray(9)
                    SensorHelper.quatToRotMat(fArr2, sensorEvent.values.clone())
                    SensorHelper.rotMatToOrient(fArr, fArr2)
                }
                mDirection = Utils.normalizeDegree(fArr[0] * -1.0f)
                val f = fArr[1]
                val f2 = fArr[2]
                if (mMagneticSensor == null) {
                    checkCompassAccuracy(sensorEvent.accuracy)
                }
                initGradientScreenViewStub()
                if (mCompassScreen != null) {
                    mCompassScreen.setCompassDirection(mDirection)
                }
                mGradienterScreen.setGradienterDirection(f, f2)
                if (CompassDeviceUtils.isSupportRotateCalibration() && mIsCalibrating) {
                    mTiltRotateView.update(f, f2)
                }
                val abs = Math.abs(f)
                if (!mIsCalibrating && mCompassScreen != null) {
                    mCompassScreen.setFaceDirection(abs)
                }
                mGradienterScreen.setFaceDirection(abs)
                val visibility: Int = mCameraMask.getVisibility()
                val animation: Animation = mCameraMask.getAnimation()
                val i = if (abs > 45.0f) 1 else if (abs == 45.0f) 0 else -1
                val str = "show camera mask view "
                val str2: String = CompassActivity.TAG
                if (i > 0 && abs < 135.0f) {
                    mCameraView.setBackgroundResource(Rr.drawable.shape_camera_foreground)
                    if (animation == null || !animation.hasStarted() || animation.hasEnded()) {
                        if ((!mIsCalibrating || mScreen.getCurrentScreenIndex() === 1) && visibility == 0) {
                            mCameraMask.startAnimation(mCameraMaskOut)
                            mMoreBtn.startAnimation(mCameraMaskOut)
                            Log.i(str2, "invisible camera mask view ")
                        } else if (mScreen.getCurrentScreenIndex() === 0 && mIsCalibrating && visibility == 8) {
                            mCameraMask.startAnimation(mCameraMaskIn)
                            mMoreBtn.startAnimation(mCameraMaskIn)
                            Log.i(str2, str)
                        }
                    }
                } else if (visibility == 8 && (animation == null || !animation.hasStarted() || animation.hasEnded())) {
                    mCameraMask.startAnimation(mCameraMaskIn)
                    mMoreBtn.startAnimation(mCameraMaskIn)
                    Log.i(str2, str)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {
                if (isViewAttachedToWindow() && mMagneticSensor == null) {
                    checkCompassAccuracy(i)
                }
            }
        }

    */

    public boolean orientaionSensorNonExistent(SensorManager sensorManager) {
        return osensor == null;
    }

    public void checkCompassAccuracy(int i) {
        /*if (i >= 3 || System.currentTimeMillis() - this.mLastCalibrateSuccessTime <= 3000) {
            if (msensor != null) {
                completeCalibration();
            } else if (this.mIsCalibrating && System.currentTimeMillis() - this.mLastUnreliableTime > 3000) {
                completeCalibration();
            }
        } else if (!this.mIsCalibrating && CompassDeviceUtils.isSupportCalibration()) {
            enterCalibration();
            sendCalibrateTimeOut();
        }*/
    }

}
