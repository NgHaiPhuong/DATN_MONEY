package com.nghp.project.moneyapp.activity.choose

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.databinding.ItemChooseCurrencyBinding
import com.nghp.project.moneyapp.db.model.CurrencyModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CurrencyAdapter @Inject constructor(@ActivityContext private val context: Context) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var listCurrency = mutableListOf<CurrencyModel>()
    private var w = context.resources.displayMetrics.widthPixels / 100f
    var callBack: ICallBackItem? = null

    fun setData(list: MutableList<CurrencyModel>) {
        this.listCurrency = list
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            ItemChooseCurrencyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listCurrency.size

    inner class CurrencyViewHolder(private val binding: ItemChooseCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.layoutParams.height = (15.55f * w).toInt()
        }

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = listCurrency[position]

            Glide.with(context)
                .asBitmap()
                .load("file:///android_asset/${item.uri}/${item.country}.webp")
                .into(binding.ivFlag)

            binding.tvCurrencyCode.text = item.code
            binding.tvCurrencyName.text = item.name
            binding.tvSymbol.text = item.symbol

            if (item.isSelected) {
                binding.tvSymbol.setTextColor(ContextCompat.getColor(context, R.color.color_F4F9FF))
                binding.tvCurrencyCode.setTextColor(ContextCompat.getColor(context, R.color.color_F4F9FF))
                binding.tvCurrencyName.setTextColor(ContextCompat.getColor(context, R.color.color_F4F9FF))

                binding.root.setBackgroundResource(R.drawable.bg_currency_selected)
            } else {
                binding.tvSymbol.setTextColor(ContextCompat.getColor(context, R.color.color_001C41))
                binding.tvCurrencyCode.setTextColor(ContextCompat.getColor(context, R.color.color_001C41))
                binding.tvCurrencyName.setTextColor(ContextCompat.getColor(context, R.color.color_001C41))

                binding.root.setBackgroundResource(R.drawable.bg_currency_unselected)
            }

            binding.root.setOnUnDoubleClickListener {
                setCurrent(position)
                callBack?.callBack(item, position)
                changeNotify()
            }
        }
    }

    fun setCurrent(position: Int) {
        for (pos in listCurrency.indices) listCurrency[pos].isSelected = pos == position

        changeNotify()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}