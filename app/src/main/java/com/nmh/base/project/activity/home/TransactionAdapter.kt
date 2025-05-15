package com.nmh.base.project.activity.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.nmh.base.project.R
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.databinding.ItemDateBinding
import com.nmh.base.project.databinding.ItemTransactionByDateBinding
import com.nmh.base.project.db.model.TransactionItem
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.SYMBOL_CURRENCY
import com.nmh.base.project.helpers.TypeLoan
import com.nmh.base.project.helpers.TypeModel
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.FormatNumber
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class TransactionAdapter @Inject constructor(@ActivityContext private val context: Context) :
    Adapter<ViewHolder>() {

    private var listTransaction = mutableListOf<TransactionItem>()

    var callback: ICallBackItem? = null

    companion object {
        const val ITEM_DATE = 0
        const val ITEM_TRANSACTION = 1
    }

    fun setData(lst: MutableList<TransactionItem>) {
        this.listTransaction = lst
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == ITEM_TRANSACTION) TransactionHolder(ItemTransactionByDateBinding.inflate(inflater, parent, false))
        else DateHolder(ItemDateBinding.inflate(inflater, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return when(listTransaction[position]) {
            is TransactionItem.DateHeader -> ITEM_DATE
            is TransactionItem.Transaction -> ITEM_TRANSACTION
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TRANSACTION) (holder as TransactionHolder).onBind(position)
        else (holder as DateHolder).onBind(position)
    }

    override fun getItemCount(): Int = if (listTransaction.isEmpty()) 0 else listTransaction.size

    inner class TransactionHolder(private val binding: ItemTransactionByDateBinding) :
        ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val item = listTransaction[position]

            if (item is TransactionItem.Transaction) {

                Glide.with(context)
                    .load(item.model.category.img)
                    .into(binding.imvCategory)

                binding.tvNameCategory.text = item.model.category.name
                binding.tvNote.text = item.model.note
                binding.tvTime.text = item.model.time

                val currency = DataLocalManager.getOption(SYMBOL_CURRENCY)
                val stringAmount = FormatNumber.convertNumberToNumberDotString(item.model.amount)
                when (item.model.typeModel) {
                    TypeModel.EXPENSES-> {
                        binding.tvAmount.text = "-$currency${stringAmount}"
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_red))
                    }
                    TypeModel.INCOME, TypeModel.LOAN -> {
                        binding.tvAmount.text = "$currency${stringAmount}"
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_green))
                    }
                    else -> Unit
                }

                when(item.model.typeLoan) {
                    TypeLoan.TYPE_BORROW -> {
                        binding.tvAmount.text = "-$currency$stringAmount"
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_red))
                    }

                    TypeLoan.TYPE_LOAN -> {
                        binding.tvAmount.text = "$currency$stringAmount"
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_green))
                    }
                    else -> Unit
                }

                binding.root.setOnUnDoubleClickListener {
                    callback?.callBack(item, position)
                    changeNotify()
                }
            }
        }
    }

    inner class DateHolder(private val binding: ItemDateBinding) :
        ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val item = listTransaction[position]

            val currency = DataLocalManager.getOption(SYMBOL_CURRENCY)

            if(item is TransactionItem.DateHeader) {
                binding.tvDate.text = item.date

                val amountByDate = listTransaction
                    .filterIsInstance<TransactionItem.Transaction>()
                    .filter { it.model.date  == item.date}
                    .sumOf { it.model.amount }

                val formatNumber = FormatNumber.convertNumberToNumberDotString(amountByDate)
                binding.tvTotalByDate.text = if (amountByDate >= 0) "$currency$formatNumber" else "-$currency$formatNumber"
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}