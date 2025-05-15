package com.nmh.base.project.activity.uninstall

import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.databinding.ActivitySurveyUserBinding
import com.nmh.base.project.databinding.AdsNativeBotHorizontalMediaLeftBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.openSettingPermission
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.showToast
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.utils.AdsConfig

class SurveyUserActivity : BaseActivity<ActivitySurveyUserBinding>(ActivitySurveyUserBinding::inflate) {

    private var isFeature = false
    private var isTooMany = false
    private var isDoNotIt = false
    private var isOther = false

    override fun setUp() {
        loadNative()

        binding.btnBack.setOnUnDoubleClickListener { finish() }

        binding.llFeature.setOnClickListener { actionChoose(0) }
        binding.llTooMany.setOnClickListener { actionChoose(1) }
        binding.llDonotNeed.setOnClickListener { actionChoose(2) }
        binding.llOther.setOnClickListener { actionChoose(3) }

        binding.btnCancel.setOnUnDoubleClickListener {
            startIntent(MainActivity::class.java.name, true)
            finishAffinity()
        }

        binding.btnUninstall.setOnClickListener {
            if (isFeature || isTooMany || isDoNotIt || isOther) {
                AppOpenManager.getInstance().disableAppResumeWithActivity(SurveyUserActivity::class.java)
                openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            } else showToast(getString(R.string.please_choose_issues), Gravity.CENTER)
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(SurveyUserActivity::class.java)
    }

    private fun actionChoose(pos: Int) {
        when(pos) {
            0 -> {
                binding.icFeature.setImageResource(if (isFeature) R.drawable.ic_check_box else R.drawable.ic_check_box_done)
                isFeature = !isFeature
            }
            1 -> {
                binding.icTooMany.setImageResource(if (isTooMany) R.drawable.ic_check_box else R.drawable.ic_check_box_done)
                isTooMany = !isTooMany
            }
            2 -> {
                binding.icDonot.setImageResource(if (isDoNotIt) R.drawable.ic_check_box else R.drawable.ic_check_box_done)
                isDoNotIt = !isDoNotIt
            }
            3 -> {
                binding.icOther.setImageResource(if (isOther) R.drawable.ic_check_box else R.drawable.ic_check_box_done)
                isOther = !isOther
            }
        }
    }

    private fun loadNative() {
        if (AdsConfig.haveNetworkConnection(this) && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.isLoadFullAds() /* thêm điều kiện remote nữa*/) {
            AdsConfig.nativeSurveyUser ?.let {
                pushViewAds(it)
            } ?: run {
                binding.layoutNative.visible()
                Admob.getInstance().loadNativeAd(
                    this,
                    getString(R.string.native_survey_user),
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