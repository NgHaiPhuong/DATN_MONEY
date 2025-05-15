package com.nmh.base.project.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.language.LanguageActivity
import com.nmh.base.project.callback.ICallBackCheck
import com.nmh.base.project.databinding.ActivitySettingBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.IS_RATED
import com.nmh.base.project.helpers.IS_SHOW_BACK
import com.nmh.base.project.helpers.IS_SHOW_CD_NATIVE_FULL
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.ActionUtils
import com.nmh.base.project.utils.AdsConfig
import com.nmh.base.project.utils.UtilsRate

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    @SuppressLint("CommitPrefEdits")
    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showInterBack()
            }
        })

        if (DataLocalManager.getBoolean(IS_RATED, true))
            binding.cvRate.gone()

        binding.cvLang.setOnUnDoubleClickListener {
            DataLocalManager.setBoolean(IS_SHOW_BACK, true)
            startIntent(Intent(this, LanguageActivity::class.java), false)
        }
        binding.cvRate.setOnUnDoubleClickListener {
            AppOpenManager.getInstance().disableAppResumeWithActivity(SettingActivity::class.java)
            UtilsRate.showRate(this, false, object : ICallBackCheck {
                override fun check(isCheck: Boolean) {
                    if (isCheck) binding.cvRate.gone()
                }
            })
        }
        binding.cvShare.setOnUnDoubleClickListener { ActionUtils.shareApp(this) }
        binding.cvPolicy.setOnUnDoubleClickListener { ActionUtils.openPolicy(this) }
    }

    override fun onResume() {
        super.onResume()

        AppOpenManager.getInstance().enableAppResumeWithActivity(SettingActivity::class.java)
    }

    private fun showInterBack() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.interBack != null && AdsConfig.checkTimeShowInter()
            && AdsConfig.isLoadFullAds() /* thêm điều kiện remote */) {
            Admob.getInstance().showInterAds(this@SettingActivity, AdsConfig.interBack, object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()

                    finish()
                }

                override fun onAdFailedToShow(p0: AdError?) {
                    super.onAdFailedToShow(p0)
                    DataLocalManager.setBoolean(IS_SHOW_CD_NATIVE_FULL, true)
                }

                override fun onAdFailedToLoad(p0: LoadAdError?) {
                    super.onAdFailedToLoad(p0)
                    DataLocalManager.setBoolean(IS_SHOW_CD_NATIVE_FULL, true)
                }

                override fun onAdClosedByUser() {
                    super.onAdClosedByUser()
                    DataLocalManager.setBoolean(IS_SHOW_CD_NATIVE_FULL, true)

                    AdsConfig.interBack = null
                    AdsConfig.lastTimeShowInter = System.currentTimeMillis()
                    AdsConfig.loadInterBack(this@SettingActivity)
                }
            })
        } else finish()
    }
}