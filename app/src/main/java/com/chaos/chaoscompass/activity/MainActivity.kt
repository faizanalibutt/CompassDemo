package com.chaos.chaoscompass.activity

import android.os.Bundle
import com.chaos.chaoscompass.*
import com.chaos.chaoscompass.adapter.ViewPagerAdapter
import com.chaos.chaoscompass.app.Activity
import com.chaos.chaoscompass.fragment.CompassFragment
import com.chaos.chaoscompass.fragment.LevelFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(CompassFragment("compass"), getString(R.string.text_compass))
        adapter.addFragment(LevelFragment(), getString(R.string.text_level_meter))
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)
        textView.isSelected = true

//        nav_back.setOnClickListener {
//            //startActivity(Intent(this, SpeedometerActivity::class.java))
//            finish()
//        }
//
//        premium_services.setOnClickListener {
//            App.bp?.purchaseRemoveAds(this)
//        }
//
//        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium)))
//            premium_services.visibility = View.GONE
//        else
//            AppUtils.animateProButton(this, premium_services)

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (!(App.bp!!.handleActivityResult(requestCode, resultCode, intent)))
//            super.onActivityResult(requestCode, resultCode, data)
//    }
}