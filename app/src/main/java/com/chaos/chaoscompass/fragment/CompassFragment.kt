package com.chaos.chaoscompass.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chaos.chaoscompass.R
import com.chaos.chaoscompass.utils.CompassSensor
import com.chaos.chaoscompass.utils.CurrentLocation
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_compass.*

@SuppressLint("SetTextI18n")
class CompassFragment() : Fragment(), CurrentLocation.LocationResultListener, CompassSensor.CompassListener {

    private var mView: View? = null
    private var compassType = ""
    private var currentLocation: CurrentLocation? = null
    private var mCurrentLocation: Location? = null
    private var mCompassSensor: CompassSensor? = null

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
        mCompassSensor?.isSensorAvailable(compassPane)
        mCompassSensor?.start()

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

    }

    override fun onResume() {
        super.onResume()

        mView.let {
        }

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

    override fun orientationField(sensorVal: Float) {
        chaosCompassView.setVal(sensorVal)
    }

}