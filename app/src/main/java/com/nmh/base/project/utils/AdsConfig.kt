package com.nmh.base.project.utils

import android.content.Context
import android.net.ConnectivityManager
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.sharepref.DataLocalManager

object AdsConfig {

    var lastTimeShowInter = 0L

    var nativeHome: NativeAd? = null
    var nativeExitApp: NativeAd? = null
    var nativeBackHome: NativeAd? = null
    var nativeAll: NativeAd? = null
    var nativePermission: NativeAd? = null
    var nativeLanguage: NativeAd? = null
    var nativeLanguageSelect: NativeAd? = null
    var nativeKeepUser: NativeAd? = null
    var nativeSurveyUser: NativeAd? = null
    var nativeIntro1: NativeAd? = null
    var nativeIntro2: NativeAd? = null
    var nativeIntro3: NativeAd? = null
    var nativeIntro4: NativeAd? = null
    var nativeFullSplash: NativeAd? = null
    var nativeFull: NativeAd? = null

    var interBack: InterstitialAd? = null
    var interHome: InterstitialAd? = null
    var interIntro1: InterstitialAd? = null
    var interIntro2: InterstitialAd? = null
    var interIntro3: InterstitialAd? = null

    var is_load_native_intro1 = true
    var is_load_native_intro2 = true
    var is_load_native_intro3 = true
    var is_load_native_intro4 = true
    var is_load_inter_intro = true
    var is_load_inter_intro1 = true
    var is_load_inter_intro2 = true
    var is_load_inter_intro3 = true

    var switch_nativenew_onboarding = true
    var is_load_native_introfull_2 = false
    var is_load_native_introfull_3 = false
    var is_load_onboarding_new = true
    var is_load_inter_language = true

    var is_load_native_full_splash = true
    var is_load_native_full = true
    var is_load_fast_app = true

    var cbFetchInterval = 15
    var interval_show_interstitial = 15
    var is_delay_show_inter_splash = 6L

    fun loadInterBack(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && interBack == null
            && haveNetworkConnection(context) /* thêm điều kiện remote */) {
            Admob.getInstance().loadInterAds(context, context.getString(R.string.inter_back),
                object : AdCallback() {
                    override fun onInterstitialLoad(interstitialAd: InterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interBack = interstitialAd
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError?) {
                        super.onAdFailedToLoad(p0)
                        interBack = null
                    }
                })
        }
    }

    fun loadInterHome(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && interHome == null
            && haveNetworkConnection(context) /* thêm điều kiện remote */) {
            Admob.getInstance().loadInterAds(context, context.getString(R.string.inter_home),
                object : AdCallback() {
                    override fun onInterstitialLoad(interstitialAd: InterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interHome = interstitialAd
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError?) {
                        super.onAdFailedToLoad(p0)
                        interHome = null
                    }
                })
        }
    }

    fun loadInterIntro1(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)
            && is_load_inter_intro1 && interIntro1 == null && isLoadFullAds()) {
            Admob.getInstance().loadInterAds(context, context.getString(R.string.inter_intro1),
                object : AdCallback() {
                    override fun onInterstitialLoad(interstitialAd: InterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interIntro1 = interstitialAd
                    }
                })
        }
    }
    fun loadInterIntro2(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)
            && is_load_inter_intro2 && interIntro2 == null && isLoadFullAds()) {
            Admob.getInstance().loadInterAds(context, context.getString(R.string.inter_intro2),
                object : AdCallback() {
                    override fun onInterstitialLoad(interstitialAd: InterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interIntro2 = interstitialAd
                    }
                })
        }
    }
    fun loadInterIntro3(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)
            && is_load_inter_intro3 && interIntro3 == null && isLoadFullAds()) {
            Admob.getInstance().loadInterAds(context, context.getString(R.string.inter_intro3),
                object : AdCallback() {
                    override fun onInterstitialLoad(interstitialAd: InterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interIntro3 = interstitialAd
                    }
                })
        }
    }

    fun loadNativeHome(context: Context) {
        if (haveNetworkConnection(context) && ConsentHelper.getInstance(context).canRequestAds()
            && nativeHome == null /* thêm điều kiện remote nữa*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_home),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeHome = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativeHome = null
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeHome = null
                        loadNativeHome(context)
                    }
                }
            )
        }
    }

    fun loadNativeBackHome(context: Context) {
        if (haveNetworkConnection(context) && ConsentHelper.getInstance(context).canRequestAds()
            && nativeBackHome == null /* thêm điều kiện remote nữa*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_back_home),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeBackHome = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        super.onAdFailedToLoad()
                        nativeBackHome = null
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeBackHome = null
                    }
                }
            )
        }
    }

    fun loadNativeExitApp(context: Context) {
        if (haveNetworkConnection(context) && ConsentHelper.getInstance(context).canRequestAds()
            && nativeExitApp == null /*thêm remote config*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_exit),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeExitApp = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        super.onAdFailedToLoad()
                        nativeExitApp = null
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeExitApp = null
                    }
                }
            )
        }
    }

    fun loadNativeAll(context: Context) {
        if (haveNetworkConnection(context) && ConsentHelper.getInstance(context).canRequestAds()
            && nativeAll == null /* thêm điều kiện remote nữa*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_all),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeAll = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativeAll = null
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeAll = null
                    }
                }
            )
        }
    }

    fun loadNativeIntro1(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)
            && isLoadFullAds() && nativeIntro1 == null && is_load_native_intro1) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_intro1),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeIntro1 = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeIntro1 = null
                    }
                }
            )
        }
    }
    fun loadNativeIntro2(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)
            && isLoadFullAds() && nativeIntro2 == null && is_load_native_intro2) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_intro2),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeIntro2 = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeIntro2 = null
                    }
                }
            )
        }
    }
    fun loadNativeIntro3(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)
            && isLoadFullAds() && nativeIntro3 == null && is_load_native_intro3) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_intro3),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeIntro3 = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeIntro3 = null
                    }
                }
            )
        }
    }
    fun loadNativeIntro4(context: Context) {
        if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)
            && isLoadFullAds() && nativeIntro4 == null && is_load_native_intro4) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_intro4),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeIntro4 = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativeIntro4 = null
                    }
                }
            )
        }
    }

    fun loadNativePermission(context: Context) {
        if (haveNetworkConnection(context) && nativePermission == null /* thêm điều kiện remote nữa*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_permission),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativePermission = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativePermission = null
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        nativePermission = null
                    }
                }
            )
        }
    }

    fun loadNativeKeepUser(context: Context) {
        if (haveNetworkConnection(context)
            && nativeKeepUser == null /* thêm điều kiện remote nữa*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_keep_user),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeKeepUser = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativeKeepUser = null
                    }
                }
            )
        }
    }

    fun loadNativeFullSplash(context: Context) {
        if (haveNetworkConnection(context) && ConsentHelper.getInstance(context).canRequestAds()
            && nativeFullSplash == null && is_load_native_full_splash) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_full_splash),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeFullSplash = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativeFullSplash = null
                    }
                }
            )
        }
    }

    fun loadNativeFull(context: Context) {
        if (haveNetworkConnection(context) && ConsentHelper.getInstance(context).canRequestAds()
            && nativeFull == null && is_load_native_full && isLoadFullAds()) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_full),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeFull = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativeFull = null
                    }
                }
            )
        }
    }

    fun loadNativeLanguage(context: Context) {
        if (haveNetworkConnection(context) && nativeLanguage == null /* thêm điều kiện remote nữa*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_language),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeLanguage = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativeLanguage = null
                    }
                }
            )
        }
    }

    fun loadNativeLanguageSelect(context: Context) {
        if (haveNetworkConnection(context) && ConsentHelper.getInstance(context).canRequestAds()
            && nativeLanguageSelect == null /* thêm điều kiện remote nữa*/) {
            Admob.getInstance().loadNativeAd(context, context.getString(R.string.native_language_select),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeLanguageSelect = nativeAd
                    }

                    override fun onAdFailedToLoad() {
                        nativeLanguageSelect = null
                    }
                }
            )
        }
    }

    fun haveNetworkConnection(context: Context): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true))
                if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName.equals("MOBILE", ignoreCase = true))
                if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }

    fun checkTimeShowInter(): Boolean =
        System.currentTimeMillis() - lastTimeShowInter > if (isLoadFullAds()) interval_show_interstitial * 1000 else 20000
    /*interval_show_interstitial chỉ app dụng cho bản full ads, bản normal mặc đinh 20s*/

    fun getDelayShowInterSplash() = is_delay_show_inter_splash * 1000L

    fun isLoadFullAds(): Boolean = true
}