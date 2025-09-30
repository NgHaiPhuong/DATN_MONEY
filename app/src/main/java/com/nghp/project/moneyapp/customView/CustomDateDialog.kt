package com.nghp.project.moneyapp.customView

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackString
import com.nghp.project.moneyapp.databinding.CustomeDateDialogBinding
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import dagger.hilt.android.qualifiers.ActivityContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class CustomDateDialog @Inject constructor(@ActivityContext private val context: Context): Dialog(context, R.style.CustomDialog) {

    private var binding = CustomeDateDialogBinding.inflate(LayoutInflater.from(context))

    var callback : ICallBackString?= null

    init {
        setContentView(binding.root)
        setCancelable(true)

        evenClick()
    }

    fun showDialog() {
        show()
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER
        window?.attributes = layoutParams
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun evenClick() {
        binding.tvCancel.setOnUnDoubleClickListener { dismiss() }

        binding.tvSave.setOnUnDoubleClickListener {
            val date = binding.singleDateTimePicker.date

            val sdf = SimpleDateFormat("MMMM, dd yyyy", Locale.ENGLISH)
            val formattedDate = sdf.format(date)

            callback?.callBack(null, formattedDate)
            dismiss()
        }
    }
}