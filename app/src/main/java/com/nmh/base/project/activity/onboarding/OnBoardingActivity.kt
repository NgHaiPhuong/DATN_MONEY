package com.nmh.base.project.activity.onboarding

import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.choose.ChooseCurrencyActivity
import com.nmh.base.project.activity.setbudget.SetBudgetActivity
import com.nmh.base.project.adapter.DepthPageTransformer
import com.nmh.base.project.databinding.ActivityOnBoardingBinding
import com.nmh.base.project.databinding.AdsNativeBotBinding
import com.nmh.base.project.databinding.AdsNativeTopFullAdsBinding
import com.nmh.base.project.databinding.NativeButtonBotLoadingBinding
import com.nmh.base.project.databinding.NativeTopFullAsdLoadingBinding
import com.nmh.base.project.db.model.OnBoardingModel
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.helpers.CHOOSE_CURRENCY
import com.nmh.base.project.helpers.FIRST_INSTALL
import com.nmh.base.project.helpers.SET_START_BUDGET
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.AdsConfig
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    @Inject
    lateinit var pageAdapter: PagerOnBoardingAdapter

    override fun setUp() {
        if (!AdsConfig.isLoadFullAds()) {
            val vLoad = NativeButtonBotLoadingBinding.inflate(layoutInflater)
            binding.frNativeAds.removeAllViews()
            binding.frNativeAds.addView(vLoad.root)
        } else {
            val vLoad = NativeTopFullAsdLoadingBinding.inflate(layoutInflater)
            binding.frNativeAds.removeAllViews()
            binding.frNativeAds.addView(vLoad.root)

            //màn per chỉ chạy vào lần đầu cài app nên check điều kiện này khi load trước native màn per
            if (DataLocalManager.getBoolean(FIRST_INSTALL, true))
                AdsConfig.loadNativePermission(this@OnBoardingActivity)
        }

        binding.tvAction.setOnUnDoubleClickListener {
            when (binding.viewPager.currentItem) {
                0 -> binding.viewPager.setCurrentItem(1, true)
                1 -> binding.viewPager.setCurrentItem(2, true)
                2 -> binding.viewPager.setCurrentItem(3, true)
                3 -> startActivity()
            }
        }

        pageAdapter.setData(mutableListOf<OnBoardingModel>().apply {
            add(OnBoardingModel(getString(R.string.title_onboarding_1), getString(R.string.des_onboarding_1), R.drawable.img_on_boarding_1))
            add(OnBoardingModel(getString(R.string.title_onboarding_2), getString(R.string.des_onboarding_2), R.drawable.img_on_boarding_2))
            add(OnBoardingModel(getString(R.string.title_onboarding_3), getString(R.string.des_onboarding_3), R.drawable.img_on_boarding_3))
            add(OnBoardingModel(getString(R.string.title_onboarding_4), getString(R.string.des_onboarding_4), R.drawable.img_on_boarding_4))
        })


        binding.viewPager.apply {
            setPageTransformer(DepthPageTransformer())
            offscreenPageLimit = 4
            isUserInputEnabled = !AdsConfig.isLoadFullAds()
            adapter = pageAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            binding.tvAction.text = getString(R.string.next)
                            binding.tvTitle.text = getString(R.string.title_onboarding_1)
                            binding.tvDes.text = getString(R.string.des_onboarding_1)

                            if (true /*trong này check điều kiện remote*/) {
                                loadNative(0)
                            } else {
                                binding.layoutNative.gone()
                                binding.vTrans.visible()
                            }
                        }

                        1 -> {
                            binding.tvAction.text = getString(R.string.next)
                            binding.tvTitle.text = getString(R.string.title_onboarding_2)
                            binding.tvDes.text = getString(R.string.des_onboarding_2)

                            if (true /*trong này check điều kiện remote*/) {
                                if (AdsConfig.isLoadFullAds()) loadNative(1)
                                else {
                                    binding.layoutNative.gone()
                                    binding.vTrans.visible()
                                }
                            } else {
                                binding.layoutNative.gone()
                                binding.vTrans.visible()
                            }
                        }

                        2 -> {
                            binding.tvAction.text = getString(R.string.next)
                            binding.tvTitle.text = getString(R.string.title_onboarding_3)
                            binding.tvDes.text = getString(R.string.des_onboarding_3)

                            if (true /*trong này check điều kiện remote*/) {
                                loadNative(2)
                            } else {
                                binding.layoutNative.gone()
                                binding.vTrans.visible()
                            }
                        }
                        3 -> {
                            binding.tvAction.text = getString(R.string.start)
                            binding.tvTitle.text = getString(R.string.title_onboarding_4)
                            binding.tvDes.text = getString(R.string.des_onboarding_4)

                            if (true /*trong này check điều kiện remote*/) {
                                if (AdsConfig.isLoadFullAds()) loadNative(3)
                                else {
                                    binding.layoutNative.gone()
                                    binding.vTrans.visible()
                                }
                            } else {
                                binding.layoutNative.gone()
                                binding.vTrans.visible()
                            }
                        }
                    }
                }
            })
        }
        binding.indicator.attachTo(binding.viewPager)
    }

    private fun loadInter() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.isLoadFullAds() /* thêm điều kiện remote */){
            val callback = object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    startActivity()
                }

                override fun onInterstitialLoadFaild() {
                    super.onInterstitialLoadFaild()
                    startActivity()
                }

                override fun onAdFailedToShow(p0: AdError?) {
                    super.onAdFailedToShow(p0)
                    startActivity()
                }
            }
            Admob.getInstance().loadAndShowInter(this, getString(R.string.inter_intro), true, callback)
        } else startActivity()
    }

    private fun startActivity() {
        if (DataLocalManager.getBoolean(CHOOSE_CURRENCY, false)) {
            if (DataLocalManager.getBoolean(SET_START_BUDGET, false))
                startIntent(MainActivity::class.java.name, true)
            else startIntent(SetBudgetActivity::class.java.name, true)
        } else startIntent(ChooseCurrencyActivity::class.java.name, true)
    }

    private fun loadNative(position : Int) {
        val strId = when(position) {
            0 -> getString(R.string.native_intro1)
            1 -> getString(R.string.native_intro2)
            2 -> getString(R.string.native_intro3)
            3 -> getString(R.string.native_intro4)
            else -> getString(R.string.native_intro1)
        }
        if(haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()) {
            binding.layoutNative.visible()
            binding.vTrans.gone()
            Admob.getInstance().loadNativeAd(this@OnBoardingActivity, strId, object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    pushViewAds(nativeAd)
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()
                    binding.frNativeAds.removeAllViews()
                }
            })
        } else {
            binding.layoutNative.gone()
            binding.vTrans.visible()
        }
    }

    private fun pushViewAds(nativeAd: NativeAd) {
        val adView: ViewBinding
        if (!AdsConfig.isLoadFullAds()) adView = AdsNativeBotBinding.inflate(layoutInflater)
        else adView = AdsNativeTopFullAdsBinding.inflate(layoutInflater)

        binding.layoutNative.visible()
        binding.vTrans.gone()
        binding.frNativeAds.removeAllViews()
        binding.frNativeAds.addView(adView.root)
        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView.root as NativeAdView)
    }
}