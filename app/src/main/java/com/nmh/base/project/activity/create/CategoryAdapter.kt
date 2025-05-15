package com.nmh.base.project.activity.create

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.nmh.base.project.R
import com.nmh.base.project.databinding.ItemCategoryBinding
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.db.model.CategoryModel
import com.nmh.base.project.callback.ICallBackItem
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CategoryAdapter@Inject constructor(@ActivityContext  private val context: Context)
    : Adapter<CategoryAdapter.TransactionHolder>() {

    private var lstCategory = mutableListOf<CategoryModel>()
    var callback: ICallBackItem?= null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst : MutableList<CategoryModel>) {
        this.lstCategory = lst
        changeNotify()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        return TransactionHolder(ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int = if(lstCategory.isEmpty()) 0 else lstCategory.size

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        holder.onBind(position)
    }

    inner class TransactionHolder(private val binding: ItemCategoryBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun onBind(position: Int) {
            val item = lstCategory[position]

            Glide.with(context)
                .load(item.img)
                .into(binding.imvCategory)

            binding.tvName.text = item.name

            if(!item.isSelected)
                binding.imvCategory.setBackgroundResource(R.drawable.bg_category_unselected)
            else binding.imvCategory.setBackgroundResource(R.drawable.bg_category_selected)

            binding.root.setOnUnDoubleClickListener {
                setCurrent(position)
                callback?.callBack(item, position)
                changeNotify()
            }
        }
    }

    private fun setCurrent(position: Int) {
        for(pos in lstCategory.indices) lstCategory[pos].isSelected = pos == position
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}