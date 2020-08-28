package com.chaos.chaoscompass.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chaos.chaoscompass.R
import com.chaos.chaoscompass.utils.CompassSensor
import kotlinx.android.synthetic.main.fragment_level.*

class LevelFragment : Fragment(), CompassSensor.CompassListener {

    private var mCompassSensor: CompassSensor? = null
    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_level, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        mCompassSensor = CompassSensor(view.context)
        mCompassSensor?.setListener(this)
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

    companion object {}

    override fun onNewAzimuth(azimuth: Float, pitch: Float, oldDegree: Float) {
        accelerometer_view.sensorValue.setRotation(azimuth, oldDegree, pitch)
    }

    override fun magneticField(magneticField: Float) {
    }

    override fun sensorCalibration(type: String?) {
    }

    override fun orientationField(sensorVal: Float, i: Int, abs: Float) {
    }
}