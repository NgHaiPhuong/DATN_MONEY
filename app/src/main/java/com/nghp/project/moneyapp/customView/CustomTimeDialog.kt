package com.nghp.project.moneyapp.customView

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackString
import com.nghp.project.moneyapp.databinding.CustomeTimeDialogBinding
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import dagger.hilt.android.qualifiers.ActivityContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class CustomTimeDialog @Inject constructor(@ActivityContext private val context: Context) :
    Dialog(context, R.style.CustomDialog) {

    private var binding = CustomeTimeDialogBinding.inflate(LayoutInflater.from(context))

    var callback: ICallBackString? = null

    init {
        setContentView(binding.root)
        setCancelable(true)

        evenClick()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun evenClick() {
        binding.tvCancel.setOnUnDoubleClickListener { dismiss() }

        binding.tvSave.setOnUnDoubleClickListener {
            val date = binding.singleDateTimePicker.date

            val sdfTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val formattedTime = sdfTime.format(date)

            callback?.callBack(null, formattedTime)
            dismiss()
        }
    }
}