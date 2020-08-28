package com.chaos.chaoscompass.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.chaos.chaoscompass.R
import com.chaos.chaoscompass.utils.CompassSensor
import com.chaos.chaoscompass.utils.CurrentLocation
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_compass.*
import timber.log.Timber

@SuppressLint("SetTextI18n")
class CompassFragment() : Fragment(), CurrentLocation.LocationResultListener,
    CompassSensor.CompassListener {

    private var mView: View? = null
    private var compassType = ""
    private var currentLocation: CurrentLocation? = null
    private var mCurrentLocation: Location? = null
    private var mCompassSensor: CompassSensor? = null
    private val TAG = "Compass:CompassFragment"

    //              Animation Views
    var mCameraMaskIn: Animation? = null
    var mCameraMaskOut: Animation? = null

    constructor(report: String) : this() {
        this.compassType = report
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        mCompassSensor = CompassSensor(view.context)
        mCompassSensor?.setListener(this)

        currentLocation = CurrentLocation(view.context)

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )
        val rationale = getString(R.string.text_compass_permission)
        val options = Permissions.Options().setRationaleDialogTitle(getString(R.string.text_info))
            .setSettingsDialogTitle(getString(R.string.text_warning))

        Permissions.check(
            view.context,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {
                override fun onGranted() {
                    currentLocation?.getLocation(this@CompassFragment)
                    // TODO: 8/25/2020 show camera view
                }
            })

        initResources(view)

    }

    private fun initResources(view: View) {

        mCameraMaskIn = AnimationUtils.loadAnimation(view.context, R.anim.camera_mask_in)
        mCameraMaskIn?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                camera_mask.clearAnimation()
                camera_mask.setVisibility(View.VISIBLE)
            }
        })
        mCameraMaskOut = AnimationUtils.loadAnimation(view.context, R.anim.camera_mask_out)
        mCameraMaskOut?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                camera_mask.clearAnimation()
                camera_mask.setVisibility(View.GONE)
            }
        })

    }

    override fun onResume() {
        super.onResume()

        mView.let {
        }
        mCompassSensor?.start()

    }

    override fun onPause() {
        super.onPause()
        mCompassSensor?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompassSensor?.stop()
    }

    override fun gotLocation(location: Location?) {
        mCurrentLocation = location
    }

    override fun onNewAzimuth(azimuth: Float, pitch: Float, oldDegree: Float) {

    }

    override fun magneticField(magneticField: Float) {

    }

    override fun sensorCalibration(type: String?) {

    }

    override fun orientationField(sensorVal: Float, i: Int, abs: Float) {

        chaosCompassView.setVal(sensorVal)

        /*if (!mIsCalibrating && mCompassScreen != null) {
                    mCompassScreen.setFaceDirection(abs);
                }
          mGradienterScreen.setFaceDirection(abs);*/

        val visibility = camera_mask.visibility
        val animation = camera_mask.animation

        //val i = if (abs > 45.0f) 1 else if (abs == 45.0f) 0 else -1
        val str = "show camera mask view"
        if (i > 0 && abs < 135.0f) {
            //cameraPreview.setBackgroundResource(R.drawable.shape_camera_foreground)
            if (animation == null || !animation.hasStarted() || animation.hasEnded()) {
                if (/*(|| mScreen.getCurrentScreenIndex() === 1)*/
                    /*!mIsCalibrating &&*/ visibility == View.VISIBLE) {
                    camera_mask.startAnimation(mCameraMaskOut)
                    chaosCompassView.visibility = View.GONE
                    Timber.tag(TAG).i("invisible camera mask view")
                } /*else if (*//*mScreen.getCurrentScreenIndex() === 0 && mIsCalibrating && *//*visibility == View.GONE) {
                    camera_mask.startAnimation(mCameraMaskIn)
                    Timber.tag(TAG).i(str)
                }*/
            }
        } else if (visibility == View.GONE && (animation == null || !animation.hasStarted() || animation.hasEnded())) {
            camera_mask.startAnimation(mCameraMaskIn)
            chaosCompassView.visibility = View.VISIBLE
            Timber.tag(TAG).i(str)
        }

    }

}