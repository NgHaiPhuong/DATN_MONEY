package com.nghp.project.moneyapp.customView

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.databinding.DeleteDialogBinding
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.callback.ICallBackCheck
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CustomDeleteBudgetDialog @Inject constructor(@ActivityContext private val context: Context) :
    Dialog(context, R.style.CustomDialog) {

    private val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context))

    var callback: ICallBackCheck? = null

    init {
        setContentView(binding.root)
        setCancelable(true)

        evenClick()
    }

    fun showDialog() {
        binding.tvDesDelete.text = context.getString(R.string.str_delete_budget)
        binding.tvTitleDelete.text = context.getString(R.string.delete_this_budget)
        show()
    }

    private fun evenClick() {
        binding.tvNo.setOnUnDoubleClickListener { dismiss() }

        binding.tvYes.setOnUnDoubleClickListener {
            callback?.check(true)
            dismiss()
        }
    }
}