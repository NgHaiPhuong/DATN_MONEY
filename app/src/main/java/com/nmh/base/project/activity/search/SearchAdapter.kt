package com.nmh.base.project.activity.search

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nmh.base.project.R
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.databinding.ItemSearchBinding
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.TypeLoan
import com.nmh.base.project.helpers.TypeModel
import com.nmh.base.project.utils.FormatNumber
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class SearchAdapter @Inject constructor(@ActivityContext private val context: Context) :
    RecyclerView.Adapter<SearchAdapter.SearchHolder>() {

    private var originalList = mutableListOf<TransactionModel>()
    private var lstTransaction = mutableListOf<TransactionModel>()

    var callback : ICallBackItem? = null

    fun setData(list: MutableList<TransactionModel>) {
        this.lstTransaction = list
        this.originalList = list
        changeNotify()
    }

    fun filter(str: String) {
        if (str.isNotBlank()) {
            val lowerQuery = str.lowercase()

            val lst = originalList.filter {
                it.category.name.lowercase().contains(lowerQuery)
            }
            lstTransaction = lst.toMutableList()
        } else lstTransaction = originalList
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        return SearchHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = if(lstTransaction.isEmpty()) 0 else lstTransaction.size

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class SearchHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val item = lstTransaction[position]

            Glide.with(context)
                .load(item.category.img)
                .into(binding.imvCategory)

            binding.tvNameCategory.text = item.category.name
            binding.tvNote.text = item.note
            binding.tvTime.text = item.time

            val stringAmount = FormatNumber.convertNumberToNumberDotString(item.amount)
            when (item.typeModel) {
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

            when(item.typeLoan) {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}