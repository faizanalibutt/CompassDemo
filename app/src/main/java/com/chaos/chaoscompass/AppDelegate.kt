package com.chaos.chaoscompass

import android.app.Application
import timber.log.Timber


class AppDelegate : Application() {

    //var splashInterstitial: InterAdPair? = null

    override fun onCreate() {
        super.onCreate()
        /*
        loadInterstitialAd(
            ADUnitPlacements.SPLASH_INTERSTITIAL,
            onLoaded = { splashInterstitial = it })
        initBP()
         */

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        /*RemoteConfigUtils.createConfigSettings().fetchAndActivate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)*/
    }

    /*override fun attachBaseContext(base: Context?) {
        localeManager = LocaleManagerX(base)
        super.attachBaseContext(base)
        Utility.bypassHiddenApiRestrictions()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager!!.setLocale(this)
        Log.d("TAG", "onConfigurationChanged: " + newConfig.locale.getLanguage())
    }*/

    companion object {

        /*var bp: BillingProcessor? = null
        var localeManager: LocaleManagerX? = null

        fun Context.initBP() {
            // TODO: 7/24/2020 in app purchase key put here
            bp = this.initBilling(
                "",
                { start(this) },
                {
                    onPurchased()
                })
        }

        fun Context.onPurchased() {
            val isPurchased = bp?.isPurchased(productKey) ?: false
            if (isPurchased) {
                start(this)
            }
        }

        fun start(context: Context) {
            val mainIntent = Intent(context, MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(mainIntent)
        }*/

    }
}