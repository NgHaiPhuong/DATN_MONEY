package com.nghp.project.moneyapp.activity.statistics

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.databinding.ItemStatisticBinding
import com.nghp.project.moneyapp.db.model.TransactionByCategoryModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.helpers.SYMBOL_CURRENCY
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.FormatNumber
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class StatisticAdapter @Inject constructor(@ActivityContext private val context: Context) :
    Adapter<StatisticAdapter.StatisticViewHolder>() {

    private var listTransaction = mutableListOf<TransactionByCategoryModel>()
    private var totalBalance: Long = 0L

    var callback: ICallBackItem? = null

    fun setData(lst: MutableList<TransactionByCategoryModel>, total: Long) {
        this.listTransaction = lst
        this.totalBalance = total
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        return StatisticViewHolder(
            ItemStatisticBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = if (listTransaction.isEmpty()) 0 else listTransaction.size

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class StatisticViewHolder(private val binding: ItemStatisticBinding) :
        ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val item = listTransaction[position]

            Glide.with(context)
                .load(item.category.img)
                .into(binding.imvCategory)

            val currency = DataLocalManager.getOption(SYMBOL_CURRENCY)

            binding.tvNameCategory.text = item.category.name
            binding.tvAmount.text = "$currency${FormatNumber.convertNumberToNumberDotString(item.totalAmount)}"

            val percent = (item.totalAmount / totalBalance) * 100
            val color = ContextCompat.getColor(context, item.category.colorImg)
            binding.vLoading.setCurrentProgress(percent)
            binding.vLoading.setColorProgress(color)

            binding.root.setOnUnDoubleClickListener {
                callback?.callBack(item, position)
                changeNotify()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}