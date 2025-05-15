package com.nmh.base.project.activity.onboarding

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.BuildConfig
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.databinding.ActivityIntroThreeBinding
import com.nmh.base.project.databinding.AdsNativeBotBinding
import com.nmh.base.project.databinding.AdsNativeBotFullAdsBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.utils.AdsConfig

class IntroThreeActivity: BaseActivity<ActivityIntroThreeBinding>(ActivityIntroThreeBinding::inflate) {
    override fun setUp() {
        FirebaseAnalytics.getInstance(this).logEvent("v_ui_intro3_${BuildConfig.VERSION_CODE}", Bundle())
        showNative()
        AdsConfig.loadNativeIntro4(this@IntroThreeActivity)
        AdsConfig.loadInterIntro3(this@IntroThreeActivity)

        evenClick()
    }

    private fun evenClick(){
        binding.tvAction.setOnUnDoubleClickListener { showInterIntro() }
    }

    private fun showInterIntro() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.interIntro3 != null && AdsConfig.is_load_inter_intro3) {
            Admob.getInstance().showInterAds(this, AdsConfig.interIntro3, object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    startIntent(IntroFourActivity::class.java.name, true)
                }

                override fun onAdClosedByUser() {
                    super.onAdClosedByUser()
                    AdsConfig.interIntro3 = null
                    AdsConfig.lastTimeShowInter = System.currentTimeMillis()
                }
            })
        } else startIntent(IntroFourActivity::class.java.name, true)
    }

    private fun showNative() {
        if (haveNetworkConnection() && AdsConfig.is_load_native_intro3
            && ConsentHelper.getInstance(this).canRequestAds()) {
            binding.layoutNative.visible()
            AdsConfig.nativeIntro3?.let {
                pushViewAds(it)
            } ?: run {
                Admob.getInstance().loadNativeAd(this, getString(R.string.native_intro3),
                    object : NativeCallback() {
                        override fun onNativeAdLoaded(nativeAd: NativeAd) {
                            pushViewAds(nativeAd)
                        }

                        override fun onAdFailedToLoad() {
                            binding.frAds.removeAllViews()
                        }
                    }
                )
            }
        } else binding.layoutNative.gone()
    }

    private fun pushViewAds(nativeAd: NativeAd) {
        val adView: ViewBinding
        if (!AdsConfig.isLoadFullAds()) adView = AdsNativeBotBinding.inflate(layoutInflater)
        else adView = AdsNativeBotFullAdsBinding.inflate(layoutInflater)

        binding.frAds.removeAllViews()
        binding.frAds.addView(adView.root)
        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView.root as NativeAdView)
    }
}