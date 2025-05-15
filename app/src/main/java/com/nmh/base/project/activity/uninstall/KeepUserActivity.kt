package com.nmh.base.project.activity.uninstall

import android.view.LayoutInflater
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.databinding.ActivityKeepUserBinding
import com.nmh.base.project.databinding.AdsNativeBotHorizontalMediaLeftBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.utils.AdsConfig

class KeepUserActivity : BaseActivity<ActivityKeepUserBinding>(ActivityKeepUserBinding::inflate) {

    override fun setUp() {
        loadNative()

        binding.btnTryAgain.setOnUnDoubleClickListener {
            startIntent(MainActivity::class.java.name, true)
            finishAffinity()
        }

        binding.tvStill.setOnUnDoubleClickListener {
            startIntent(SurveyUserActivity::class.java.name, false)
        }
    }

    private fun loadNative() {
        if (AdsConfig.haveNetworkConnection(this) && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.isLoadFullAds() /* thêm điều kiện remote nữa*/) {
            AdsConfig.nativeKeepUser ?.let {
                pushViewAds(it)
            } ?: run {
                binding.layoutNative.visible()
                Admob.getInstance().loadNativeAd(
                    this,
                    getString(R.string.native_keep_user),
                    object : NativeCallback() {
                        override fun onNativeAdLoaded(nativeAd: NativeAd) {
                            pushViewAds(nativeAd)
                        }

                        override fun onAdFailedToLoad() {
                            binding.frAds.removeAllViews()
                        }
                    })
            }
        } else binding.layoutNative.gone()
    }

    private fun pushViewAds(nativeAd: NativeAd) {
        val adView: NativeAdView
        if (AdsConfig.isLoadFullAds())
            adView = AdsNativeBotHorizontalMediaLeftBinding.inflate(LayoutInflater.from(this)).root
        else adView = AdsNativeBotHorizontalMediaLeftBinding.inflate(LayoutInflater.from(this)).root

        binding.frAds.removeAllViews()
        binding.frAds.addView(adView)
        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
    }
}