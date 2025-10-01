package com.nghp.project.moneyapp.activity

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.databinding.ActivitySettingBinding
import com.nghp.project.moneyapp.extensions.gone
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.helpers.IS_RATED
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.ActionUtils

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    @SuppressLint("CommitPrefEdits")
    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        if (DataLocalManager.getBoolean(IS_RATED, true))
            binding.cvRate.gone()

        binding.cvShare.setOnUnDoubleClickListener { ActionUtils.shareApp(this) }
        binding.cvPolicy.setOnUnDoubleClickListener { ActionUtils.openPolicy(this) }
    }
}