package com.nghp.project.moneyapp.activity.budgetdetail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.nghp.project.moneyapp.callback.ICallBackItemCheck
import com.nghp.project.moneyapp.databinding.ItemBudgetDetailBinding
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.helpers.SYMBOL_CURRENCY
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.FormatNumber
import com.nghp.project.moneyapp.utils.UtilsBitmap
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class BudgetDetailAdapter @Inject constructor(@ActivityContext private val context: Context) :
    Adapter<BudgetDetailAdapter.BudgetDetailHolder>() {

    var listBudget = mutableListOf<BudgetModel>()

    var callBack: ICallBackItemCheck? = null

    fun setData(lst: MutableList<BudgetModel>) {
        this.listBudget = lst
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetDetailHolder {
        return BudgetDetailHolder(
            ItemBudgetDetailBinding.inflate(
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

    inner class BudgetDetailHolder(private val binding: ItemBudgetDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "UseKtx")
        fun onBind(position: Int) {
            val item = listBudget[position]

            val currency = DataLocalManager.getOption(SYMBOL_CURRENCY)

            binding.tvName.text = item.name
            binding.tvAmount.text = "$currency${FormatNumber.convertNumberToNumberDotString(item.amount)}"

            binding.pillProgressView.setDarkColor(item.color)

            val lightColor = UtilsBitmap.lightenColorIntToHex(item.color)
            binding.pillProgressView.setLightColor(Color.parseColor(lightColor))

            val totalBudget = listBudget.sumOf { it.amount }
            val percent = item.amount * 100 / totalBudget
            binding.pillProgressView.setProgress(percent.toInt())

            binding.ivEdit.setOnUnDoubleClickListener {
                callBack?.check(item, false)
                changeNotify()
            }

            binding.tvAmount.setOnUnDoubleClickListener {
                callBack?.check(item, false)
                changeNotify()
            }

            binding.root.setOnUnDoubleClickListener {
                callBack?.check(item, true)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}