package com.nmh.base.project.activity

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.databinding.ActivitySettingBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.IS_RATED
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.ActionUtils

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