package com.nghp.project.moneyapp.activity.setbudget

import android.content.Intent
import android.view.Gravity
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.MainActivity
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.databinding.ActivitySetBudgetBinding
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.extensions.showToast
import com.nghp.project.moneyapp.helpers.BUDGET
import com.nghp.project.moneyapp.helpers.SET_START_BUDGET
import com.nghp.project.moneyapp.sharepref.DataLocalManager

class SetBudgetActivity :
    BaseActivity<ActivitySetBudgetBinding>(ActivitySetBudgetBinding::inflate) {

    override fun setUp() {
        evenClick()
    }

    private fun evenClick() {
        binding.tvSave.setOnUnDoubleClickListener {
            val input = binding.edtSetBudget.text.toString().trim()
            val value = input.toLongOrNull()

            if (value == null) {
                showToast(getString(R.string.enter_your_budget), Gravity.CENTER)
            } else {
                DataLocalManager.setBoolean(SET_START_BUDGET, true)
                DataLocalManager.setLong(value, BUDGET)

                startIntent(Intent(this@SetBudgetActivity, MainActivity::class.java), false)
            }
        }
    }
}