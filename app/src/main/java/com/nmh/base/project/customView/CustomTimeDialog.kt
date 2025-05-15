package com.nmh.base.project.customView

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.nmh.base.project.R
import com.nmh.base.project.callback.ICallBackString
import com.nmh.base.project.databinding.CustomeTimeDialogBinding
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
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