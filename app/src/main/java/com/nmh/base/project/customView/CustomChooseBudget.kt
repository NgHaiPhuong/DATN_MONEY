package com.nmh.base.project.customView

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.nmh.base.project.R
import com.nmh.base.project.activity.create.BudgetAdapter
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.callback.ICallBackString
import com.nmh.base.project.databinding.CustomeChooseBudgetBinding
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import dagger.hilt.android.qualifiers.ActivityContext
import okhttp3.internal.notify
import javax.inject.Inject

class CustomChooseBudget @Inject constructor(@ActivityContext private val context: Context) :
    Dialog(context, R.style.CustomDialog) {

    private var binding = CustomeChooseBudgetBinding.inflate(LayoutInflater.from(context))

    private lateinit var budgetAdapter: BudgetAdapter

    var callback: ICallBackItem? = null

    private var budgetModel = BudgetModel()

    init {
        setContentView(binding.root)
        setCancelable(true)

        setupLayout()
        evenClick()
    }

    fun showDialog(list: MutableList<BudgetModel>) {
        show()
        resetDialogData(list)
    }

    private fun setupLayout() {
        budgetAdapter = BudgetAdapter(context)
        budgetAdapter.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if (ob is BudgetModel) budgetModel = ob
            }
        }

        binding.rcvBudget.apply {
            adapter = budgetAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun evenClick() {
        binding.tvCancel.setOnUnDoubleClickListener { dismiss() }
        binding.rlNone.setOnUnDoubleClickListener {
            callback?.callBack(null, -1)
            dismiss()
        }
        binding.tvSave.setOnUnDoubleClickListener {
            callback?.callBack(budgetModel, -1)
            dismiss()
        }
    }

    private fun resetDialogData(list: MutableList<BudgetModel>) {
        val listReset = list.map {
            it.copy(isSelected = false) // thay đổi một vài thuộc tính trong khi giữ nguyên các thuộc tính khác.
        }.toMutableList()

        budgetAdapter.setData(listReset)
    }
}