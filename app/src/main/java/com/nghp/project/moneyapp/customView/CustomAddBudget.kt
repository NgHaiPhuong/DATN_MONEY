package com.nghp.project.moneyapp.customView

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.databinding.CustomeAddBudgetBinding
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.utils.UtilsBitmap
import dagger.hilt.android.qualifiers.ActivityContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CustomAddBudget @Inject constructor(@ActivityContext private val context: Context) :
    Dialog(context, R.style.CustomDialog) {

    private val binding = CustomeAddBudgetBinding.inflate(LayoutInflater.from(context))

    var callBack: ICallBackItem? = null

    init {
        setContentView(binding.root)
        setCancelable(true)

        evenClick()
    }

    fun showDialog() {
        reloadData()
        show()
    }

    private fun evenClick() {
        val formatDate = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)
        val date = formatDate.format(Date())

        binding.tvCancel.setOnUnDoubleClickListener { dismiss() }
        binding.tvSave.setOnUnDoubleClickListener {
            val name = binding.etBudgetName.text.toString().trim()
            val amountText = binding.etNumber.text.toString().trim()

            if (name.isBlank() || amountText.isBlank()) {
                Toast.makeText(context, context.getString(R.string.str_please_fill), Toast.LENGTH_SHORT).show()
                return@setOnUnDoubleClickListener
            }

            val amount = amountText.toLongOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, context.getString(R.string.str_please_fill), Toast.LENGTH_SHORT).show()
                return@setOnUnDoubleClickListener
            }

            val colorRandom = UtilsBitmap.getRandomPastelColor()

            val budget = BudgetModel(
                name = name,
                amount = amount,
                date = date,
                color = colorRandom
            )

            callBack?.callBack(budget, -1)
        }
    }

    fun hideDialog() {
        dismiss()
    }

    private fun reloadData() {
        binding.etBudgetName.setText("")
        binding.etNumber.setText("")
    }
}