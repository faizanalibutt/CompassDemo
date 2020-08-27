package com.chaos.chaoscompass.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.reflect.KParameter

open class Activity : AppCompatActivity() {

    private val TAG = "BaseActivity"

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        //Utility.resetActivityTitle(this)
    }

//    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
//        if (overrideConfiguration != null) {
//            val uiMode: Int = overrideConfiguration.uiMode
//            overrideConfiguration.setTo(baseContext.resources.configuration)
//            overrideConfiguration.uiMode = uiMode
//        }
//        super.applyOverrideConfiguration(overrideConfiguration)
//    }

    protected var dialog: AlertDialog? = null

//    protected open fun showRateExitDialogue(activity: Activity?, isMenu: Boolean): AlertDialog? {
//        val rateUsDialog = ExitDialogue(
//            activity as AppCompatActivity,
//            isMenu
//        )
//        rateUsDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.background_rate_exit_dialog))
//        return rateUsDialog
//    }
//
//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(App.localeManager?.setLocale(base))
//        Log.d(TAG, "attachBaseContext")
//    }

}