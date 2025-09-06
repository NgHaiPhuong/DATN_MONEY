package com.nmh.base.project.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.choose.ChooseCurrencyActivity
import com.nmh.base.project.activity.setbudget.SetBudgetActivity
import com.nmh.base.project.databinding.ActivitySplashBinding
import com.nmh.base.project.db.model.LanguageModel
import com.nmh.base.project.helpers.CHOOSE_CURRENCY
import com.nmh.base.project.helpers.CURRENT_LANGUAGE
import com.nmh.base.project.helpers.IS_SHOW_BACK
import com.nmh.base.project.helpers.SET_START_BUDGET
import com.nmh.base.project.sharepref.DataLocalManager
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