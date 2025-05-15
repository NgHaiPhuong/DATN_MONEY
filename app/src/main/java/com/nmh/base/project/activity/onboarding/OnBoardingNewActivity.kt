package com.nmh.base.project.activity.onboarding

import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.choose.ChooseCurrencyActivity
import com.nmh.base.project.activity.setbudget.SetBudgetActivity
import com.nmh.base.project.adapter.DepthPageTransformer
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.databinding.ActivityOnBoardingNewBinding
import com.nmh.base.project.db.model.OnBoardingModel
import com.nmh.base.project.helpers.CHOOSE_CURRENCY
import com.nmh.base.project.helpers.SET_START_BUDGET
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.AdsConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingNewActivity : BaseActivity<ActivityOnBoardingNewBinding>(ActivityOnBoardingNewBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    @Inject
    lateinit var pageAdapter: PagerOnBoardingNewAdapter

    override fun setUp() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (AdsConfig.isLoadFullAds())
                AdsConfig.loadNativeHome(this@OnBoardingNewActivity)
        }

        pageAdapter.setData(mutableListOf<OnBoardingModel>().apply {
            add(
                OnBoardingModel(
                    getString(R.string.title_onboarding_1),
                    getString(R.string.des_onboarding_1),
                    R.drawable.img_on_boarding_1,
                    getString(R.string.native_intro1),
                    AdsConfig.is_load_native_intro1,
                    false
                )
            )
            add(
                OnBoardingModel(
                    getString(R.string.title_onboarding_2),
                    getString(R.string.des_onboarding_2),
                    R.drawable.img_on_boarding_2,
                    "",
                    false
                )
            )
            if (AdsConfig.is_load_native_introfull_2)
                add(OnBoardingModel("", "", -1, getString(R.string.native_introfull_3), AdsConfig.is_load_native_introfull_2,true))
            add(
                OnBoardingModel(
                    getString(R.string.title_onboarding_3),
                    getString(R.string.des_onboarding_3),
                    R.drawable.img_on_boarding_3,
                    "",
                    false
                )
            )
            if (AdsConfig.is_load_native_introfull_3)
                add(OnBoardingModel("", "", -1, getString(R.string.native_introfull_3), AdsConfig.is_load_native_introfull_3,true))
            add(
                OnBoardingModel(
                    getString(R.string.title_onboarding_4),
                    getString(R.string.des_onboarding_4),
                    R.drawable.img_on_boarding_4,
                    getString(R.string.native_intro4),
                    AdsConfig.is_load_native_intro4,
                    false
                )
            )
        })
        pageAdapter.actionNext = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if (binding.viewPager.currentItem == pageAdapter.itemCount - 1) loadInterIntro()
                else binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            }
        }

        binding.viewPager.apply {
            setPageTransformer(DepthPageTransformer())
            offscreenPageLimit = 3
            adapter = pageAdapter
            isUserInputEnabled = true
        }
    }

    private fun loadInterIntro() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.isLoadFullAds() && AdsConfig.is_load_inter_intro) {
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

    fun nextScreen() {
        if (DataLocalManager.getBoolean(CHOOSE_CURRENCY, false)) {
            if (DataLocalManager.getBoolean(SET_START_BUDGET, false))
                startIntent(MainActivity::class.java.name, true)
            else startIntent(SetBudgetActivity::class.java.name, true)
        } else startIntent(ChooseCurrencyActivity::class.java.name, true)
    }
}