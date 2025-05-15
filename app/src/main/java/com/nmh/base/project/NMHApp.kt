package com.nmh.base.project

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.util.Adjust
import com.nlbn.ads.util.AdsApplication
import com.nlbn.ads.util.AppOpenManager
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.AdsConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NMHApp : AdsApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context

        @SuppressLint("StaticFieldLeak")
        var w = 0f
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        DataLocalManager.init(applicationContext)

        ctx = applicationContext
        w = resources.displayMetrics.widthPixels / 100f

        AppOpenManager.getInstance().setResumeCallback(object : AdCallback(){
            override fun onAdClosed() {
                super.onAdClosed()
                if (!AdsConfig.isLoadFullAds())
                    AdsConfig.lastTimeShowInter = System.currentTimeMillis()
            }
        })
    }

    override fun enableAdsResume(): Boolean = true

    override fun getKeyRemoteIntervalShowInterstitial(): String = ""

    override fun getListTestDeviceId(): MutableList<String>?  = null

    override fun getResumeAdId(): String = getString(R.string.openapp_resume)

    override fun buildDebug(): Boolean  = BuildConfig.DEBUG

    override fun enableAdjustTracking(): Boolean = true

    override fun getAdjustToken(): String = getString(R.string.adjust_token)

    override fun logRevenueAdjustWithCustomEvent(p0: Double, p1: String?) {
        Adjust.getInstance().logRevenueWithCustomEvent("token event", p0, p1)
    }
}