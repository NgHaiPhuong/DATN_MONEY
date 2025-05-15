package com.nmh.base.project.activity.onboarding

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.BuildConfig
import com.nmh.base.project.R
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.choose.ChooseCurrencyActivity
import com.nmh.base.project.activity.setbudget.SetBudgetActivity
import com.nmh.base.project.databinding.ActivityIntroFourBinding
import com.nmh.base.project.databinding.AdsNativeBotBinding
import com.nmh.base.project.databinding.AdsNativeBotFullAdsBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.helpers.CHOOSE_CURRENCY
import com.nmh.base.project.helpers.SET_START_BUDGET
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.AdsConfig

class IntroFourActivity: BaseActivity<ActivityIntroFourBinding>(ActivityIntroFourBinding::inflate) {
    override fun setUp() {
        FirebaseAnalytics.getInstance(this).logEvent("v_ui_intro4_${BuildConfig.VERSION_CODE}", Bundle())
        showNative()

        evenClick()
    }

    private fun evenClick(){
        binding.tvAction.setOnUnDoubleClickListener { loadInter() }
    }

    private fun loadInter() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.is_load_inter_intro && AdsConfig.isLoadFullAds()) {
            val callback = object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    nextScreen()
                }

                override fun onAdFailedToLoad(p0: LoadAdError?) {
                    super.onAdFailedToLoad(p0)
                    nextScreen()
                }

                override fun onAdFailedToShow(p0: AdError?) {
                    super.onAdFailedToShow(p0)
                    nextScreen()
                }
            }
            Admob.getInstance().loadAndShowInter(this, getString(R.string.inter_intro), true, callback)
        } else nextScreen()
    }

    private fun nextScreen() {
        if (DataLocalManager.getBoolean(CHOOSE_CURRENCY, false)) {
            if (DataLocalManager.getBoolean(SET_START_BUDGET, false))
                startIntent(MainActivity::class.java.name, true)
            else startIntent(SetBudgetActivity::class.java.name, true)
        } else startIntent(ChooseCurrencyActivity::class.java.name, true)
    }

    private fun showNative() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.is_load_native_intro4) {
            binding.layoutNative.visible()
            AdsConfig.nativeIntro4?.let {
                pushViewAds(it)
            } ?: run {
                Admob.getInstance().loadNativeAd(this, getString(R.string.native_intro4),
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