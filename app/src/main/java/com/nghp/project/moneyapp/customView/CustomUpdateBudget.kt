package com.nghp.project.moneyapp.customView

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackString
import com.nghp.project.moneyapp.databinding.CustomeUpdateBudgetBinding
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CustomUpdateBudget @Inject constructor(@ActivityContext private val context: Context) :
    Dialog(context, R.style.CustomDialog) {

    private var binding = CustomeUpdateBudgetBinding.inflate(LayoutInflater.from(context))

    var callback: ICallBackString? = null

    init {
        setContentView(binding.root)
        setCancelable(true)

        evenClick()
    }

    fun showDialog(str: String) {
        binding.etNumber.setText(str)
        show()
    }

    private fun evenClick() {
        binding.tvCancel.setOnUnDoubleClickListener { dismiss() }
        binding.tvSave.setOnUnDoubleClickListener {
            val amount = binding.etNumber.text.toString()
            callback?.callBack(null, amount)

            dismiss()
        }
    }
}