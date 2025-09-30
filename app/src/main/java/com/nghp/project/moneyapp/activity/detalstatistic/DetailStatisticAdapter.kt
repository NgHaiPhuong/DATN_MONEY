package com.nghp.project.moneyapp.activity.detalstatistic

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.databinding.ItemDateBinding
import com.nghp.project.moneyapp.databinding.ItemTransactionByDateBinding
import com.nghp.project.moneyapp.db.model.TransactionItem
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.helpers.TypeLoan
import com.nghp.project.moneyapp.helpers.TypeModel
import com.nghp.project.moneyapp.utils.FormatNumber
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class DetailStatisticAdapter @Inject constructor(@ActivityContext private val context: Context) :
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

                val stringAmount = FormatNumber.convertNumberToNumberDotString(item.model.amount)
                when (item.model.typeModel) {
                    TypeModel.EXPENSES-> {
                        binding.tvAmount.text = "-$${stringAmount}"
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_red))
                    }
                    TypeModel.INCOME, TypeModel.LOAN -> {
                        binding.tvAmount.text = "$${stringAmount}"
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_green))
                    }
                    else -> Unit
                }

                when(item.model.typeLoan) {
                    TypeLoan.TYPE_BORROW -> {
                        binding.tvAmount.text = "-$$stringAmount"
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_red))
                    }

                    TypeLoan.TYPE_LOAN -> {
                        binding.tvAmount.text = "$$stringAmount"
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

            if(item is TransactionItem.DateHeader) {
                binding.tvDate.text = item.date

                val amountByDate = listTransaction
                    .filterIsInstance<TransactionItem.Transaction>()
                    .filter { it.model.date  == item.date}
                    .sumOf { it.model.amount }

                val formatNumber = FormatNumber.convertNumberToNumberDotString(amountByDate)
                binding.tvTotalByDate.text = if (amountByDate >= 0) "$$formatNumber" else "-$$formatNumber"
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}