package com.nmh.base.project.customView

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.nmh.base.project.R
import com.nmh.base.project.activity.home.MonthAdapter
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.callback.ICallBackString
import com.nmh.base.project.databinding.CustomSelectMonthBinding
import com.nmh.base.project.db.model.MonthModel
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.utils.DataHelper
import dagger.hilt.android.qualifiers.ActivityContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CustomSelectMonth @Inject constructor(@ActivityContext private val context: Context) :
    Dialog(context, R.style.CustomDialog) {

    private val binding = CustomSelectMonthBinding.inflate(LayoutInflater.from(context))

    private lateinit var monthAdapter: MonthAdapter

    private var selectMonth: String = ""

    var callback: ICallBackString? = null

    init {
        setContentView(binding.root)
        setCancelable(true)

        setupLayout()
        evenClick()
    }

    private fun setupLayout() {
        val month = SimpleDateFormat("MMM", Locale.ENGLISH)
        selectMonth = month.format(Date())
        binding.tvShowTime.text = selectMonth

        monthAdapter = MonthAdapter(context)
        monthAdapter.setData(DataHelper.getMonth(context))
        monthAdapter.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                val item = ob as MonthModel
                if (item.isSelected) selectMonth = item.month
            }
        }
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        binding.tvYear.text = currentYear.toString()
    }

    private fun evenClick() {
        binding.rcvMonth.apply {
            adapter = monthAdapter
            layoutManager = GridLayoutManager(context, 4)
        }

        binding.ivBackYear.setOnUnDoubleClickListener {
            binding.tvYear.text = (binding.tvYear.text.toString().toInt() - 1).toString()
        }

        binding.ivNextYear.setOnUnDoubleClickListener {
            binding.tvYear.text = (binding.tvYear.text.toString().toInt() - (-1)).toString()
        }

        binding.tvCancel.setOnUnDoubleClickListener { dismiss() }
        binding.tvSave.setOnUnDoubleClickListener {
            val date = selectMonth + " " + binding.tvYear.text
            callback?.callBack(null, date)
            dismiss()
        }
    }
}