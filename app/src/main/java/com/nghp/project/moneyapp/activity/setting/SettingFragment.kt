package com.nghp.project.moneyapp.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.nghp.project.moneyapp.activity.base.BaseFragment
import com.nghp.project.moneyapp.activity.choose.ChooseCurrencyActivity
import com.nghp.project.moneyapp.databinding.ActivitySettingBinding
import com.nghp.project.moneyapp.extensions.gone
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.helpers.IS_RATED
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.ActionUtils

class SettingFragment : BaseFragment<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    override fun setUp() {
        if (DataLocalManager.getBoolean(IS_RATED, true)) {
            binding.cvRate.gone()
            binding.line1.gone()
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
        fun newInstance(): SettingFragment {
            val args = Bundle()

            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }
}