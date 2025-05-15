package com.nmh.base.project.activity.setbudget

import android.content.Intent
import android.view.Gravity
import com.nmh.base.project.R
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.databinding.ActivitySetBudgetBinding
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.showToast
import com.nmh.base.project.helpers.BUDGET
import com.nmh.base.project.helpers.SET_START_BUDGET
import com.nmh.base.project.sharepref.DataLocalManager

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