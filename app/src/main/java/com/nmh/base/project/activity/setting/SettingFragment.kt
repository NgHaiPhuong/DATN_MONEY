package com.nmh.base.project.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.nlbn.ads.util.AppOpenManager
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.SettingActivity
import com.nmh.base.project.activity.base.BaseFragment
import com.nmh.base.project.activity.choose.ChooseCurrencyActivity
import com.nmh.base.project.activity.language.LanguageActivity
import com.nmh.base.project.callback.ICallBackCheck
import com.nmh.base.project.databinding.ActivitySettingBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.IS_RATED
import com.nmh.base.project.helpers.IS_SHOW_BACK
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.ActionUtils
import com.nmh.base.project.utils.UtilsRate

class SettingFragment : BaseFragment<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    override fun setUp() {
        if (DataLocalManager.getBoolean(IS_RATED, true)) {
            binding.cvRate.gone()
            binding.line1.gone()
        }

        binding.cvLang.setOnUnDoubleClickListener {
            DataLocalManager.setBoolean(IS_SHOW_BACK, true)
            startIntent(Intent(requireContext(), LanguageActivity::class.java), false)
        }
        binding.cvRate.setOnUnDoubleClickListener {
            AppOpenManager.getInstance().disableAppResumeWithActivity(SettingActivity::class.java)
            UtilsRate.showRate(activity as MainActivity, false, object : ICallBackCheck {
                override fun check(isCheck: Boolean) {
                    if (isCheck) binding.cvRate.gone()
                }
            })
        }
        binding.cvShare.setOnUnDoubleClickListener { ActionUtils.shareApp(requireContext()) }
        binding.cvPolicy.setOnUnDoubleClickListener { ActionUtils.openPolicy(requireContext()) }
        binding.cvCurrency.setOnUnDoubleClickListener {
            startIntent(Intent(requireContext(), ChooseCurrencyActivity::class.java), false)
        }
    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        nameActivity: String,
        isFinish: Boolean
    ) {

    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        intent: Intent,
        isFinish: Boolean
    ) {

    }

    override fun startIntent(intent: Intent, isFinish: Boolean) {
        startActivity(intent, null)
        if (isFinish) requireActivity().finish()
    }

    companion object {
        fun newInstance() : SettingFragment {
            val args = Bundle()

            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }
}