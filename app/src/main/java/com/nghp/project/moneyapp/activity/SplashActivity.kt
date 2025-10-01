package com.nghp.project.moneyapp.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.choose.ChooseCurrencyActivity
import com.nghp.project.moneyapp.activity.setbudget.SetBudgetActivity
import com.nghp.project.moneyapp.databinding.ActivitySplashBinding
import com.nghp.project.moneyapp.db.model.LanguageModel
import com.nghp.project.moneyapp.helpers.CHOOSE_CURRENCY
import com.nghp.project.moneyapp.helpers.CURRENT_LANGUAGE
import com.nghp.project.moneyapp.helpers.IS_SHOW_BACK
import com.nghp.project.moneyapp.helpers.SET_START_BUDGET
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    override fun setUp() {
        if (DataLocalManager.getLanguage(CURRENT_LANGUAGE) == null) {
            DataLocalManager.setLanguage(
                CURRENT_LANGUAGE,
                LanguageModel(
                    "English",
                    "flag_language",
                    getString(R.string.english),
                    Locale.ENGLISH,
                    true
                )
            )
        }
        Handler(Looper.getMainLooper()).postDelayed({ startActivity() }, 1500)
    }


    private fun startActivity() {
        DataLocalManager.setBoolean(IS_SHOW_BACK, false)
        if (DataLocalManager.getBoolean(CHOOSE_CURRENCY, false)) {
            if (DataLocalManager.getBoolean(SET_START_BUDGET, false))
                startIntent(MainActivity::class.java.name, true)
            else startIntent(SetBudgetActivity::class.java.name, true)
        } else startIntent(ChooseCurrencyActivity::class.java.name, true)
        startIntent(intent, true)
    }
}