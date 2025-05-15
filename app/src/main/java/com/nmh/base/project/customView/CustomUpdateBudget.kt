package com.nmh.base.project.customView

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.nmh.base.project.R
import com.nmh.base.project.callback.ICallBackString
import com.nmh.base.project.databinding.CustomeUpdateBudgetBinding
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
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