package com.nghp.project.moneyapp.activity.totalbalance

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nghp.project.moneyapp.databinding.ItemStatisticTableBinding
import com.nghp.project.moneyapp.db.model.StatisticModel
import com.nghp.project.moneyapp.utils.FormatNumber
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class TotalBalanceAdapter @Inject constructor(@ActivityContext private var context: Context) :
    RecyclerView.Adapter<TotalBalanceAdapter.TotalBalanceHolder>() {

    private var listTotalTransaction = mutableListOf<StatisticModel>()

    fun setData(list: MutableList<StatisticModel>) {
        this.listTotalTransaction = list
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalBalanceHolder {
        return TotalBalanceHolder(
            ItemStatisticTableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int =
        if (listTotalTransaction.isEmpty()) 0 else listTotalTransaction.size

    override fun onBindViewHolder(holder: TotalBalanceHolder, position: Int) {
        holder.onBind(position)
    }

    inner class TotalBalanceHolder(private var binding: ItemStatisticTableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            val item = listTotalTransaction[position]

            binding.tvMonth.text = item.month
            binding.tvExpenses.text = FormatNumber.convertNumberToCommaString(item.expenses)
            binding.tvIncome.text = FormatNumber.convertNumberToCommaString(item.income)
            binding.tvLoan.text = FormatNumber.convertNumberToCommaString(item.loan)
            binding.tvBorrow.text = FormatNumber.convertNumberToCommaString(item.borrow)
            binding.tvBalance.text = FormatNumber.convertNumberToCommaString(item.balance)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}