package com.nmh.base.project.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nlbn.ads.banner.BannerPlugin
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.BuildConfig
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.language.LanguageActivity
import com.nmh.base.project.activity.uninstall.KeepUserActivity
import com.nmh.base.project.databinding.ActivitySplashBinding
import com.nmh.base.project.extensions.invisible
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.helpers.CURRENT_LANGUAGE
import com.nmh.base.project.helpers.IS_SHOW_BACK
import com.nmh.base.project.helpers.IS_SHOW_CD_NATIVE_FULL_SPLASH
import com.nmh.base.project.helpers.IS_UNINSTALL
import com.nmh.base.project.db.model.LanguageModel
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.AdsConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    private var interCallback: AdCallback? = null
    private var dataUninstall = ""

    private val notificationPer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    else arrayOf("")

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Dexter.withContext(this)
                .withPermissions(Manifest.permission.POST_NOTIFICATIONS)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        val isPermissionGranted = report.areAllPermissionsGranted()
                        val permissionStatus = if (isPermissionGranted) "allow" else "not_allow"

                        val bundle = Bundle().apply {
                            putString("permission_notification", permissionStatus)
                        }
                        FirebaseAnalytics.getInstance(this@SplashActivity)
                            .logEvent("permission_notification", bundle)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>,
                        p1: PermissionToken
                    ) {
                        p1.continuePermissionRequest()
                    }

                }).check()
    }

    override fun setUp() {
        if (DataLocalManager.getLanguage(CURRENT_LANGUAGE) == null) {
            DataLocalManager.setLanguage(
                CURRENT_LANGUAGE,
                LanguageModel("English", "flag_language", getString(R.string.english), Locale.ENGLISH, true)
            )
        }

        dataUninstall = intent.getStringExtra("dataUninstall") ?: ""

        if (!checkPer(notificationPer)) requestNotificationPermission()
        if (haveNetworkConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                val remote = async { loadRemoteConfig() }
                if (remote.await())
                    withContext(Dispatchers.Main) {
                        interCallback = object : AdCallback() {
                            override fun onNextAction() {
                                super.onNextAction()
                                startActivity(true)
                            }

                            override fun onAdFailedToLoad(p0: LoadAdError?) {
                                super.onAdFailedToLoad(p0)
                                DataLocalManager.setBoolean(IS_SHOW_CD_NATIVE_FULL_SPLASH, true)
                                startActivity(true)
                            }

                            override fun onAdClosedByUser() {
                                super.onAdClosedByUser()
                                DataLocalManager.setBoolean(IS_SHOW_CD_NATIVE_FULL_SPLASH, true)
                            }
                        }

                        val consentHelper = ConsentHelper.getInstance(this@SplashActivity)
                        if (!consentHelper.canLoadAndShowAds()) consentHelper.reset()

                        consentHelper.obtainConsentAndShow(this@SplashActivity) {
                            if (dataUninstall == IS_UNINSTALL)
                                loadBanner(getString(R.string.banner_splash_uninstall), false)
                            else loadBanner(getString(R.string.banner_all), true)

                            if (dataUninstall == IS_UNINSTALL) AdsConfig.loadNativeKeepUser(this@SplashActivity)
                            else {
                                //load trước native language
                                AdsConfig.loadNativeLanguage(this@SplashActivity)
                                AdsConfig.loadNativeLanguageSelect(this@SplashActivity)

                                AdsConfig.loadNativeFullSplash(this@SplashActivity)
                            }

                            if (dataUninstall == IS_UNINSTALL) {
                                if (AdsConfig.isLoadFullAds() /* thêm điều kiện remote nữa*/) {
                                    Admob.getInstance().loadSplashInterAds2(this@SplashActivity, getString(R.string.inter_splash_uninstall), AdsConfig.getDelayShowInterSplash(), interCallback)
                                } else startActivity(false)
                            } else Admob.getInstance().loadSplashInterAds2(this@SplashActivity, getString(R.string.inter_splash), AdsConfig.getDelayShowInterSplash(), interCallback)
                        }
                    }
            }
        } else Handler(Looper.getMainLooper()).postDelayed({ startActivity(false) }, 1500)
    }

    override fun onResume() {
        super.onResume()
        Admob.getInstance().onCheckShowSplashWhenFail(this, interCallback, AdsConfig.getDelayShowInterSplash().toInt())
    }

    private suspend fun loadRemoteConfig(): Boolean {
        return suspendCoroutine { continuation ->

            val configSetting = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.Minimum_Fetch)
                .build()

            val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(configSetting)
            }

            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults) /*file này lấy trên firebase*/

            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //chỗ này sẽ get value key remote (làm và lấy theo trên link excel trên nhóm a Huy gửi, nhân bản ra sheet riêng)
                    AdsConfig.is_load_native_intro1 = remoteConfig.getBoolean("is_load_native_intro1")
                    AdsConfig.is_load_native_intro2 = remoteConfig.getBoolean("is_load_native_intro2")
                    AdsConfig.is_load_native_intro3 = remoteConfig.getBoolean("is_load_native_intro3")
                    AdsConfig.is_load_native_intro4 = remoteConfig.getBoolean("is_load_native_intro4")
                    AdsConfig.is_load_inter_intro = remoteConfig.getBoolean("is_load_inter_intro")
                    AdsConfig.is_load_inter_intro1 = remoteConfig.getBoolean("is_load_inter_intro1")
                    AdsConfig.is_load_inter_intro2 = remoteConfig.getBoolean("is_load_inter_intro2")
                    AdsConfig.is_load_inter_intro3 = remoteConfig.getBoolean("is_load_inter_intro3")
                    AdsConfig.is_load_inter_language = remoteConfig.getBoolean("is_load_inter_language")
                    AdsConfig.is_load_onboarding_new = remoteConfig.getBoolean("is_load_onboarding_new")
                    AdsConfig.switch_nativenew_onboarding = remoteConfig.getBoolean("switch_nativenew_onboarding")
                    AdsConfig.is_load_native_introfull_2 = remoteConfig.getBoolean("is_load_native_introfull_2")
                    AdsConfig.is_load_native_introfull_3 = remoteConfig.getBoolean("is_load_native_introfull_3")
                    AdsConfig.is_delay_show_inter_splash = remoteConfig.getLong("is_delay_show_inter_splash")

                    AdsConfig.is_load_native_full_splash = remoteConfig.getBoolean("is_load_native_full_splash")
                    AdsConfig.is_load_native_full = remoteConfig.getBoolean("is_load_native_full")
                    AdsConfig.is_load_fast_app = remoteConfig.getBoolean("is_load_fast_app")
                }
                continuation.resume(true)
            }
        }
    }

    private fun startActivity(isShowNativeFull: Boolean) {
        DataLocalManager.setBoolean(IS_SHOW_BACK, false)
        if (dataUninstall == IS_UNINSTALL) startIntent(KeepUserActivity::class.java.name, true)
        else {
            val intent = Intent(this@SplashActivity, LanguageActivity::class.java).apply {
                putExtra(IS_SHOW_CD_NATIVE_FULL_SPLASH, isShowNativeFull)
            }
            startIntent(intent, true)
        }
    }

    private fun loadBanner(strId: String, isRemote: Boolean) {
        if (haveNetworkConnection() && AdsConfig.isLoadFullAds() && isRemote /* -->điều kiện remote ads*/ ) {
            binding.rlBanner.visible()
            val config = BannerPlugin.Config()
            val cbFetchInterval = AdsConfig.cbFetchInterval /*cbFetchInterval lấy theo remote*/
            config.defaultRefreshRateSec = cbFetchInterval
            config.defaultCBFetchIntervalSec = cbFetchInterval
            config.defaultAdUnitId = strId
            config.defaultBannerType = BannerPlugin.BannerType.Adaptive
            Admob.getInstance().loadBannerPlugin(this, binding.banner, binding.shimmer as ViewGroup, config)
        } else binding.rlBanner.invisible()
    }
}