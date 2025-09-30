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

class CustomDeleteDialog @Inject constructor(@ActivityContext private val context: Context) :
    Dialog(context, R.style.CustomDialog) {

    private val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context))

    var callback: ICallBackCheck? = null

    init {
        setContentView(binding.root)
        setCancelable(true)

        evenClick()
    }

    private fun evenClick() {
        binding.tvNo.setOnUnDoubleClickListener { dismiss() }

        binding.tvYes.setOnUnDoubleClickListener {
            callback?.check(true)
            dismiss()
        }
    }
}