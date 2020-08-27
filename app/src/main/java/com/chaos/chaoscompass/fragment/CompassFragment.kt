package com.chaos.chaoscompass.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chaos.chaoscompass.utils.CurrentLocation
import com.chaos.chaoscompass.R
import com.google.android.material.snackbar.Snackbar
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_compass.*

@SuppressLint("SetTextI18n")
class CompassFragment() : Fragment(), CurrentLocation.LocationResultListener {

    private var mView: View? = null
    private var compassType = ""
    private var mSensorManager: SensorManager? = null
    private var mSensorEventListener: SensorEventListener? = null
    private var sensorVal = 0f
    private var currentLocation: CurrentLocation? = null
    private var mCurrentLocation: Location? = null

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
        mSensorManager = view.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?

        mSensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                sensorVal = event.values[0]
                chaosCompassView?.setVal(sensorVal)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        var sensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        if (sensor == null)
        {
            sensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            if (sensor == null)
            {
                sensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
                if (sensor == null)
                    Snackbar.make(compassPane, "Alas! your device HAS_NO_SENSOR.", Snackbar.LENGTH_INDEFINITE)
                        .show()
                else
                    Snackbar.make(compassPane, "Hurrah! your device has magnetic.", Snackbar.LENGTH_INDEFINITE)
                        .show()
            }
            else
                Snackbar.make(compassPane, "Hurrah! your device has rotation.", Snackbar.LENGTH_INDEFINITE)
                    .show()
        }
        else
            Snackbar.make(compassPane, "Hurrah! your device has orientation.", Snackbar.LENGTH_INDEFINITE)
                .show()

        /*AlertDialog.Builder(view.context).setTitle(R.string.no_sensor)
                .setMessage(R.string.no_sensor_explain)
                .setOnDismissListener { it.dismiss() }
                .setPositiveButton(
                    R.string.okay
                ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                .create()
                .show()*/

        mSensorManager?.registerListener(
            mSensorEventListener, sensor,
            SensorManager.SENSOR_DELAY_GAME
        )

        currentLocation = CurrentLocation(view.context)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA)
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
        mSensorManager?.unregisterListener(mSensorEventListener)
    }

    override fun gotLocation(location: Location?) {

    }

}