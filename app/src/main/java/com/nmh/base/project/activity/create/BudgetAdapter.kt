package com.nmh.base.project.activity.create

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.nmh.base.project.R
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.databinding.ItemChooseBudgetBinding
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class BudgetAdapter @Inject constructor(@ActivityContext private val context: Context) :
    Adapter<BudgetAdapter.BudgetDetailHolder>() {

    private var listBudget = mutableListOf<BudgetModel>()
    var callback: ICallBackItem? = null

    fun setData(list: MutableList<BudgetModel>) {
        this.listBudget = list
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetDetailHolder {
        return BudgetDetailHolder(
            ItemChooseBudgetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = if (listBudget.isEmpty()) 0 else listBudget.size

    override fun onBindViewHolder(holder: BudgetDetailHolder, position: Int) {
        holder.onBind(position)
    }

    inner class BudgetDetailHolder(private val binding: ItemChooseBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            val item = listBudget[position]

            binding.tvName.text = item.name
            binding.javView.setJarColor(item.color)

            if (item.isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_item)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_item_white)
            }

            binding.root.setOnUnDoubleClickListener {
                setCurrent(position)
                callback?.callBack(item, position)
            }
        }
    }

    fun setCurrent(position: Int) {
        for (pos in listBudget.indices) listBudget[pos].isSelected = pos == position

        changeNotify()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeNotify() {
        notifyDataSetChanged()
    }
}