package com.nghp.project.moneyapp.activity.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.databinding.ItemMonthBinding
import com.nghp.project.moneyapp.db.model.MonthModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener

class MonthAdapter (private val context: Context) : RecyclerView.Adapter<MonthAdapter.MonthHolder>() {

    private var lstMonth = mutableListOf<MonthModel>()
    var callback: ICallBackItem? = null

    fun setData(lst: MutableList<MonthModel>) {
        this.lstMonth = lst
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthHolder {
        return MonthHolder(
            ItemMonthBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = if (lstMonth.isEmpty()) 0 else lstMonth.size

    override fun onBindViewHolder(holder: MonthHolder, position: Int) {
        holder.onBind(position)
    }

    inner class MonthHolder(private val binding: ItemMonthBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            val item = lstMonth[position]

            binding.tvMonth.text = item.month

            if (item.isSelected) {
                binding.tvMonth.setTextColor(ContextCompat.getColor(context, R.color.color_F4F9FF))
                binding.tvMonth.setBackgroundResource(R.drawable.bg_month_select)
            }
            else {
                binding.tvMonth.setTextColor(ContextCompat.getColor(context, R.color.color_001C41))
                binding.tvMonth.setBackgroundResource(R.drawable.bg_month_unselect)
            }

            binding.root.setOnUnDoubleClickListener {
                currentItem(position)
                callback?.callBack(item, position)
            }
        }
    }

    private fun currentItem(position: Int) {
        for (pos in lstMonth.indices) lstMonth[pos].isSelected = pos == position
        changeNotify()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}